package com.letv.portal.service.subscription.impl;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.letv.common.dao.IBaseDao;
import com.letv.common.email.ITemplateMessageSender;
import com.letv.common.email.bean.MailMessage;
import com.letv.common.session.SessionServiceImpl;
import com.letv.portal.dao.subscription.ISubscriptionDao;
import com.letv.portal.dao.subscription.ISubscriptionDetailDao;
import com.letv.portal.model.UserVo;
import com.letv.portal.model.message.Message;
import com.letv.portal.model.subscription.Subscription;
import com.letv.portal.model.subscription.SubscriptionDetail;
import com.letv.portal.service.IUserService;
import com.letv.portal.service.impl.BaseServiceImpl;
import com.letv.portal.service.message.IMessageProxyService;
import com.letv.portal.service.order.IOrderService;
import com.letv.portal.service.product.IProductService;
import com.letv.portal.service.subscription.ISubscriptionService;
import com.letv.portal.util.SerialNumberUtil;
import com.mysql.jdbc.StringUtils;

@Service("subscriptionService")
public class SubscriptionServiceImpl extends BaseServiceImpl<Subscription> implements ISubscriptionService {
	
	@Autowired
	private IProductService productService;
	
	private final static Logger logger = LoggerFactory.getLogger(SubscriptionServiceImpl.class);
	
	public SubscriptionServiceImpl() {
		super(Subscription.class);
	}

	@Autowired
	private ISubscriptionDao subscriptionDao;
	@Autowired
	private ISubscriptionDetailDao subscriptionDetailDao;
	@Autowired
	private IMessageProxyService messageProxyService;
	@Autowired(required=false)
	private SessionServiceImpl sessionService;
	@Autowired
	private IUserService userService;
	@Autowired
	private ITemplateMessageSender defaultEmailSender;
	@Autowired
	private IOrderService orderService;
	@Value("${order.expire.day}")
	private String ORDER_EXPIRE;
	@Value("${order.overdue.day}")
	private String ORDER_OVERDUE;
	@Value("${order.delete.day}")
	private String ORDER_DELETE;

	@Override
	public IBaseDao<Subscription> getDao() {
		return this.subscriptionDao;
	}


	@Override
	public Subscription createSubscription(Long id, Map<String, Object> map, Long productInfoRecordId, Date date, String orderTime) {
		Subscription sub = new Subscription();
		sub.setSubscriptionNumber(SerialNumberUtil.getNumber(1));
		sub.setProductId(id);
		sub.setBaseRegionId(Long.parseLong((String)map.get("region")));
		sub.setChargeType(map.get("chargeType")==null?0:Integer.parseInt((String)map.get("chargeType")));
		sub.setProductInfoRecordId(productInfoRecordId);
		Integer t = Integer.parseInt(orderTime);
		sub.setOrderTime(t);
		sub.setStartTime(date);
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(date.getTime());
		cal.add(Calendar.MONTH, t);
		sub.setEndTime(cal.getTime());
		sub.setValid(1);
		sub.setUserId(sessionService.getSession().getUserId());
		sub.setCreateUser(sessionService.getSession().getUserId());
		sub.setDeleted(false);
		sub.setCreateTime(new Timestamp(date.getTime()));
		this.subscriptionDao.insert(sub);
		for (String key : map.keySet()) {
			if("region".equals(key) || key.endsWith("_type") 
					|| "order_num".equals(key) || "order_time".equals(key)) {
				continue;
			}
			SubscriptionDetail detail = new SubscriptionDetail();
			detail.setSubscriptionId(sub.getId());
			detail.setStandardName(key);
			detail.setStandardValue((String)map.get(key));
			detail.setOrderTime(t);
			detail.setStartTime(date);
			detail.setEndTime(cal.getTime());
			detail.setUserId(sessionService.getSession().getUserId());
			detail.setCreateUser(sessionService.getSession().getUserId());
			detail.setDeleted(false);
			detail.setValid(true);
			this.subscriptionDetailDao.insert(detail);
		}
		return sub;
	}


	@Override
	public List<Subscription> selectValidSubscription() {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userId", sessionService.getSession().getUserId());
		params.put("valid", 1);
		params.put("date", new Date());
		return this.subscriptionDao.selectValidSubscription(params);
	}


	@Override
	public void serviceWarn() {
		//订单到期前30天、15天、7天、3天、1天的9:00，分别进行通知
		//欠费后第1、2、3、5天的9:00，根据通知时间的不同，剩余天数分别为5、3、2、1、0
		//欠费后第7天的9:00释放
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("valid", 1);
		params.put("chargeType", 0);//包年包月
		List<Subscription> subscriptions = this.subscriptionDao.selectMaxEndTimeSubscription(params);
		Date now = new Date();
		Map<Long, Map<Long, List<Map<String, Object>>>> expires = new HashMap<Long, Map<Long, List<Map<String, Object>>>>();
		Map<Long, Map<Long, List<Map<String, Object>>>> overdues = new HashMap<Long, Map<Long, List<Map<String, Object>>>>();
		Map<Long, Map<Long, List<Map<String, Object>>>> deletes = new HashMap<Long, Map<Long, List<Map<String, Object>>>>();
		for (Subscription subscription : subscriptions) {
			long day = (subscription.getEndTime().getTime()-now.getTime())/(1000*3600*24);
			if(ORDER_EXPIRE.contains(","+day+",")) {
				Map<Long, List<Map<String, Object>>> products = null;
				if(expires.get(subscription.getUserId())==null) {
					products = new HashMap<Long, List<Map<String, Object>>>();
					expires.put(subscription.getUserId(), products);
				} else {
					products = expires.get(subscription.getUserId());
				}
				if(products.get(day)==null) {
					List<Map<String, Object>> names = new ArrayList<Map<String, Object>>();
					products.put(day, names);
				}
				Map<String, Object> instanceInfo = transResult(subscription.getProductInfoRecord().getParams());
				instanceInfo.put("instanceId", subscription.getProductInfoRecord().getInstanceId());
				instanceInfo.put("productName", subscription.getProductName());
				instanceInfo.put("regionName", subscription.getBaseRegionName());
				products.get(day).add(instanceInfo);
			} else if(ORDER_OVERDUE.contains(","+day+",")) {
				Map<Long, List<Map<String, Object>>> products = null;
				if(overdues.get(subscription.getUserId())==null) {
					products = new HashMap<Long, List<Map<String, Object>>>();
					overdues.put(subscription.getUserId(), products);
				} else {
					products = overdues.get(subscription.getUserId());
				}
				if(products.get(day)==null) {
					List<Map<String, Object>> names = new ArrayList<Map<String, Object>>();
					products.put(day, names);
				}
				Map<String, Object> instanceInfo = transResult(subscription.getProductInfoRecord().getParams());
				instanceInfo.put("instanceId", subscription.getProductInfoRecord().getInstanceId());
				instanceInfo.put("productName", subscription.getProductName());
				instanceInfo.put("regionName", subscription.getBaseRegionName());
				Calendar cal = Calendar.getInstance();
				cal.setTime(subscription.getEndTime());
				cal.add(Calendar.DATE, 7);
				cal.add(Calendar.HOUR, 9);
				instanceInfo.put("deleteTime", sdf.format(cal.getTime()));
				products.get(day).add(instanceInfo);
			} else if(ORDER_DELETE.contains(","+day+",")) {
				Map<Long, List<Map<String, Object>>> products = null;
				if(deletes.get(subscription.getUserId())==null) {
					products = new HashMap<Long, List<Map<String, Object>>>();
					deletes.put(subscription.getUserId(), products);
				} else {
					products = deletes.get(subscription.getUserId());
				}
				if(products.get(day)==null) {
					List<Map<String, Object>> names = new ArrayList<Map<String, Object>>();
					products.put(day, names);
				}
				Map<String, Object> instanceInfo = transResult(subscription.getProductInfoRecord().getParams());
				instanceInfo.put("instanceId", subscription.getProductInfoRecord().getInstanceId());
				instanceInfo.put("productName", subscription.getProductName());
				instanceInfo.put("regionName", subscription.getBaseRegionName());
				products.get(day).add(instanceInfo);
			}
		}
		//保存通知和发送邮件
		for(Long userId : expires.keySet()) {
			saveExpiresMessage(userId, expires.get(userId));
			sendEmailsByType(this.userService.getUcUserById(userId), expires.get(userId), 1);
		}
		for(Long userId : overdues.keySet()) {
			saveOverduesMessage(userId, overdues.get(userId));
			sendEmailsByType(this.userService.getUcUserById(userId), overdues.get(userId), 2);
		}
		for(Long userId : deletes.keySet()) {
			saveDeletesMessage(userId, deletes.get(userId));
			sendEmailsByType(this.userService.getUcUserById(userId), deletes.get(userId), 3);
		}
        
	}
	
	/**
	  * @Title: saveExpiresMessage
	  * @Description: 保存到期提醒消息
	  * @param userId
	  * @param infos void   
	  * @throws 
	  * @author lisuxiao
	  * @date 2015年10月27日 下午2:41:55
	  */
	private void saveExpiresMessage(Long userId, Map<Long, List<Map<String, Object>>> infos) {
		if(infos.size()!=0) {
			StringBuffer buffer = new StringBuffer();
			buffer.append("您的");
			buffer.append("[");
			for (Long day : infos.keySet()) {
				List<Map<String, Object>> params = infos.get(day);
				Map<String, List<String>> products = getProductNames(params);
				for(String productName : products.keySet()) {
					buffer.append(productName);
					buffer.append(products.get(productName).toString());
				}
		        buffer.append("]");
		        buffer.append(Math.abs(day));
		        buffer.append("天之后到期,");
			}
			buffer.append("请及时续费；");
	        buffer.append("注意：如不及时续费，到期后资源将被暂停使用。");
	        Message msg = new Message();
	        msg.setMsgTitle("云产品到期提醒");
	        msg.setMsgContent(buffer.toString());
	        msg.setMsgStatus("0");//未读
	        msg.setMsgType("2");//个人消息
	        msg.setCreatedTime(new Date());
	        Map<String,Object> msgRet = this.messageProxyService.saveMessage(userId, msg);
	        if(!(Boolean) msgRet.get("result")) {
	        	logger.error("保存云产品到期提醒通知失败，失败原因:"+msgRet.get("message"));
	        }
		}
	}
	
	private Map<String, List<String>> getProductNames(List<Map<String, Object>> params) {
		Map<String, List<String>> products = new HashMap<String, List<String>>();
		for (Map<String, Object> map : params) {
			List<String> names = null;
			if(products.get(map.get("productName"))==null) {
				names = new ArrayList<String>();
				products.put((String)map.get("productName"), names);
			} else {
				names = products.get(map.get("productName"));
			}
			names.add((String)map.get("name"));
		}
		return products;
	}
	
	/**
	  * @Title: saveOverduesMessage
	  * @Description: 保存欠费提醒信息
	  * @param userId
	  * @param infos void   
	  * @throws 
	  * @author lisuxiao
	  * @date 2015年10月27日 下午2:42:19
	  */
	private void saveOverduesMessage(Long userId, Map<Long, List<Map<String, Object>>> infos) {
		if(infos.size()!=0) {
			StringBuffer buffer = new StringBuffer();
			buffer.append("您的");
			buffer.append("[");
			for (Long day : infos.keySet()) {
				List<Map<String, Object>> params = infos.get(day);
				Map<String, List<String>> products = getProductNames(params);
				for(String productName : products.keySet()) {
					buffer.append(productName);
					buffer.append(products.get(productName).toString());
				}
		        buffer.append("]");
		        buffer.append("因欠费已延期使用");
		        buffer.append(Math.abs(day));
		        buffer.append("天，");
			}
			buffer.append("请尽快充值。");
	        buffer.append("注意：如不及时续费，到期后资源将被释放，无法恢复；如果您不再使用这些资源, 可以主动销毁, 避免再次收到提醒。");
	        Message msg = new Message();
	        msg.setMsgTitle("云产品欠费延期使用");
	        msg.setMsgContent(buffer.toString());
	        msg.setMsgStatus("0");//未读
	        msg.setMsgType("2");//个人消息
	        msg.setCreatedTime(new Date());
	        Map<String,Object> msgRet = this.messageProxyService.saveMessage(userId, msg);
	        if(!(Boolean) msgRet.get("result")) {
	        	logger.error("保存云产品欠费暂停使用通知失败，失败原因:"+msgRet.get("message"));
	        }
		}
	}
	
	/**
	  * @Title: saveDeletesMessage
	  * @Description: 保存释放资源信息
	  * @param userId
	  * @param infos void   
	  * @throws 
	  * @author lisuxiao
	  * @date 2015年10月27日 下午2:42:41
	  */
	private void saveDeletesMessage(Long userId, Map<Long, List<Map<String, Object>>> infos) {
		if(infos.size()!=0) {
			StringBuffer buffer = new StringBuffer();
			buffer.append("您的");
			buffer.append("[");
			for (Long day : infos.keySet()) {
				List<Map<String, Object>> params = infos.get(day);
				Map<String, List<String>> products = getProductNames(params);
				for(String productName : products.keySet()) {
					buffer.append(productName);
					buffer.append(products.get(productName).toString());
				}
				buffer.append("]");
				buffer.append("因欠费");
				buffer.append(Math.abs(day));
				buffer.append("天被释放，");
			}
			buffer.append("释放后将无法恢复。");
			Message msg = new Message();
			msg.setMsgTitle("云产品欠费资源释放");
			msg.setMsgContent(buffer.toString());
			msg.setMsgStatus("0");//未读
			msg.setMsgType("2");//个人消息
			msg.setCreatedTime(new Date());
			Map<String,Object> msgRet = this.messageProxyService.saveMessage(userId, msg);
			if(!(Boolean) msgRet.get("result")) {
				logger.error("保存云产品欠费资源释放通知失败，失败原因:"+msgRet.get("message"));
			}
		}
	}
	
	/**
	  * @Title: sendEmailsByType
	  * @Description: 根据类型发送邮件
	  * @param user
	  * @param infos
	  * @param type void   
	  * @throws 
	  * @author lisuxiao
	  * @date 2015年10月27日 下午2:43:01
	  */
	private void sendEmailsByType(UserVo user, Map<Long, List<Map<String, Object>>> infos, int type) {
		if("400065".equals(user.getUserId())) {
			Map<String, Object> mailMessageModel = new HashMap<String, Object>();
			mailMessageModel.put("userName", user.getUsername());

			List<Map<String, Object>> resModelList = new LinkedList<Map<String, Object>>();
			mailMessageModel.put("resList", resModelList);

			for (Long day : infos.keySet()) {
				List<Map<String, Object>> info = infos.get(day);
				for (Map<String, Object> map : info) {
					Map<String, Object> resModel = new HashMap<String, Object>();
					resModel.put("region", map.get("regionName"));
					resModel.put("type", map.get("productName"));
					resModel.put("id", ((String)map.get("instanceId")).split("_")[1]);
					resModel.put("name", map.get("name"));
					resModel.put("day", Math.abs(day));
					resModel.put("deleteTime", map.get("deleteTime"));
					resModelList.add(resModel);
				}
			}
			MailMessage mailMessage = null;
			if(type==1) {//到期提醒
				mailMessage = new MailMessage(infos.get("productName")+"到期提醒", user.getEmail(),
						infos.get("productName")+"到期提醒", "product/expireNotice.ftl", mailMessageModel);
			} else if(type==2) {//欠费提醒
				mailMessage = new MailMessage(infos.get("productName")+"欠费延期使用", user.getEmail(),
						infos.get("productName")+"欠费延期使用", "product/overdueNotice.ftl", mailMessageModel);
			} else if(type==3) {//资源释放
				mailMessage = new MailMessage(infos.get("productName")+"欠费资源释放", user.getEmail(),
						infos.get("productName")+"欠费资源释放", "product/deleteNotice.ftl", mailMessageModel);
			}
			if(mailMessage!=null) {
				mailMessage.setHtml(true);
				this.defaultEmailSender.sendMessage(mailMessage); 
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	private Map<String, Object> transResult(String result) {
		ObjectMapper resultMapper = new ObjectMapper();
		Map<String, Object> jsonResult = new HashMap<String, Object>();
		if (StringUtils.isNullOrEmpty(result))
			return jsonResult;
		try {
			jsonResult = (Map<String, Object>) resultMapper.readValue(result,
					Map.class);
		} catch (Exception e) {
			logger.error("transResult had error:", e);
		}
		return jsonResult;
	}


}
