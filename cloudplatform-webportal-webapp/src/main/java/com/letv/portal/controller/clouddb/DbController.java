package com.letv.portal.controller.clouddb;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.letv.common.exception.ValidateException;
import com.letv.common.paging.impl.Page;
import com.letv.common.result.ResultObject;
import com.letv.common.session.SessionServiceImpl;
import com.letv.common.util.HttpUtil;
import com.letv.portal.model.DbModel;
import com.letv.portal.proxy.IDbProxy;
import com.letv.portal.service.IDbService;

/**Program Name: DbController <br>
 * Description:  db数据库的相关操作<br>
 * @author name: liuhao1 <br>
 * Written Date: 2014年9月9日 <br>
 * Modified By: <br>
 * Modified Date: <br>
 */
@Controller
@RequestMapping("/db")
public class DbController {
	
	@Autowired
	private IDbService dbService;
	@Autowired
	private IDbProxy dbProxy;
	@Autowired(required=false)
	private SessionServiceImpl sessionService;
	
	
	private final static Logger logger = LoggerFactory.getLogger(DbController.class);
	
	/**Methods Name: list <br>
	 * Description: http://localhost:8080/db/list/${currentPage}/${recordsPerPage}/${dbName}<br>
	 * @author name: liuhao1
	 * @param request
	 * @return
	 */
	@RequestMapping(method=RequestMethod.GET)   
	public @ResponseBody ResultObject list(Page page,HttpServletRequest request,ResultObject obj) {
		Map<String,Object> params = HttpUtil.requestParam2Map(request);
		params.put("createUser", sessionService.getSession().getUserId());
		obj.setData(this.dbService.findPagebyParams(params, page));
		
		return obj;
	}
	@RequestMapping(value="/{currentPage}/{recordsPerPage}/{dbName}", method=RequestMethod.GET)   
	public @ResponseBody ResultObject list(@PathVariable int currentPage,@PathVariable int recordsPerPage,@PathVariable String dbName) {
		Page page = new Page();
		page.setCurrentPage(currentPage);
		page.setRecordsPerPage(recordsPerPage);
	
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("dbName", dbName);
		params.put("createUser", sessionService.getSession().getUserId());		
		ResultObject obj = new ResultObject();
		obj.setData(this.dbService.findPagebyParams(params, page));
		return obj;
	}
	
	/**Methods Name: save <br>
	 * Description: 保存创建信息  http://localhost:8080/db/<br>
	 * @author name: liuhao1
	 * @return
	 */
	@RequestMapping(method=RequestMethod.POST)   
	public @ResponseBody ResultObject save(DbModel dbModel,boolean isCreateAdmin) {
		this.dbProxy.saveAndBuild(dbModel,isCreateAdmin);
		ResultObject obj = new ResultObject();
		return obj;
	}
	@RequestMapping(value="/1/1/1/1",method=RequestMethod.POST)   
	public @ResponseBody ResultObject test(DbModel dbModel,boolean isCreateAdmin) {
		this.dbProxy.saveAndBuild(dbModel,isCreateAdmin);
		ResultObject obj = new ResultObject();
		return obj;
	}
	
	/**Methods Name: detail <br>
	 * Description: 根据id获取db信息 http://localhost:8080/db/detail/{dbId}<br>
	 * @author name: liuhao1
	 * @param dbId
	 * @return
	 */
	@RequestMapping(value="/{dbId}",method=RequestMethod.GET)
	public @ResponseBody ResultObject detail(@PathVariable Long dbId){
		isAuthorityDb(dbId);
		ResultObject obj = new ResultObject();
		DbModel db = this.dbService.dbList(dbId);
		obj.setData(db);
		return obj;
	}	
	@RequestMapping(value="/gbConfig/{dbId}",method=RequestMethod.GET)
	public @ResponseBody ResultObject getGbaConfig(@PathVariable Long dbId){
		isAuthorityDb(dbId);
		ResultObject obj = new ResultObject();
		Map<String,Object> config = this.dbService.getGbaConfig(dbId);
		obj.setData(config);
		return obj;
	}	
	
	@RequestMapping(value="/validate",method=RequestMethod.POST)
	public @ResponseBody Map<String,Object> validate(String dbName,HttpServletRequest request) {
		if(StringUtils.isEmpty(dbName))
			throw new ValidateException("参数不合法");
		List<DbModel> list = this.dbService.selectByDbNameForValidate(dbName,sessionService.getSession().getUserId());
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("valid", list.size()>0?false:true);
		return map;
	}
	
	private void isAuthorityDb(Long dbId) {
		if(dbId == null)
			throw new ValidateException("参数不合法");
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("id", dbId);
		map.put("createUser", sessionService.getSession().getUserId());
		List<DbModel> dbs = this.dbService.selectByMap(map);
		if(dbs == null || dbs.isEmpty())
			throw new ValidateException("参数不合法");
	}
}
