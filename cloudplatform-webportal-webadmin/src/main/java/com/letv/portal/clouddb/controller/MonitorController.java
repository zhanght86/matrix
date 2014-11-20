package com.letv.portal.clouddb.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.letv.common.result.ResultObject;
import com.letv.portal.model.ContainerMonitorModel;
import com.letv.portal.proxy.IContainerProxy;
import com.letv.portal.proxy.IMonitorProxy;
import com.letv.portal.service.IContainerService;
import com.letv.portal.service.IMonitorIndexService;
import com.letv.portal.service.IMonitorService;
/**
 * Program Name: MonitorController <br>
 * Description:  监控<br>
 * @author name: wujun <br>
 * Written Date: 2014年11月6日 <br>
 * Modified By: <br>
 * Modified Date: <br>
 */
@Controller
@RequestMapping("/monitor")
public class MonitorController {
	
	@Resource
	private IContainerService containerService;
	@Resource
	private IMonitorService monitorService;
	@Resource
	private IMonitorProxy monitorProxy;
	@Resource
	private IMonitorIndexService monitorIndexService;
	@Resource
	private IContainerProxy containerProxy;
	
	
	
	/**
	 * Methods Name: mclusterList <br>
	 * Description: 集群列表展示<br>
	 * @author name: wujun
	 * @param ip
	 * @param result
	 * @return
	 */
	@RequestMapping(value="/mcluster/list",method=RequestMethod.GET)
	public @ResponseBody ResultObject mclusterList(ResultObject result) {
		Map map = new HashMap<String, String>();
		map.put("type", "mclustervip");
		result.setData(this.containerProxy.selectMonitorMclusterList(map));
		return result; 
	} 
	/**
	 * Methods Name: mclusterMonitorList <br>
	 * Description: 集群状态是否正常<br>
	 * @author name: wujun
	 * @param result
	 * @return
	 */
	@RequestMapping(value="/{ip}/mcluster/status",method=RequestMethod.GET)
	public @ResponseBody ResultObject mclusterMonitorList(@PathVariable String ip,ResultObject result) {
		Map map = new HashMap<String, String>();
		map.put("ipAddr",ip);
		List<ContainerMonitorModel> list =this.containerProxy.selectMonitorMclusterDetailOrList(map);
		if((list.get(0).getClMoList())==null){
			result.addMsg(list.get(0).getMclusterName()+"集群信息抓取失败，请重新刷新");
			result.setResult(0);
		}
		result.setData(list);
		return result;  
	}
    /**
     * Methods Name: mclusterMonitorNodeAndDbDetail <br>
     * Description: 集群节点详情展示<br>
     * @author name: wujun
     * @param ip
     * @param result
     * @return
     */
	@RequestMapping(value="/{ip}/mcluster/nodeAndDb",method=RequestMethod.GET)
	public @ResponseBody ResultObject mclusterMonitorNodeAndDbDetail(@PathVariable String ip,ResultObject result) {
		result.setData(this.containerProxy.selectMonitorDetailNodeAndDbData(ip));
		return result;
	}
	/**
	 * Methods Name: mclusterMonitorClusterDetail <br>
	 * Description: 集群cluster信息展示<br>
	 * @author name: wujun
	 * @param ip
	 * @param result
	 * @return
	 */
	@RequestMapping(value="/{ip}/mcluster/cluster",method=RequestMethod.GET)
	public @ResponseBody ResultObject mclusterMonitorClusterDetail(@PathVariable String ip,ResultObject result) {
		result.setData(this.containerProxy.selectMonitorDetailClusterData(ip));
		return result;
	}
	/**
	 * Methods Name: mclusterMonitorCharts <br>
	 * Description: 监控视图<br>
	 * @author name: wujun
	 * @param mclusterId
	 * @param result
	 * @return
	 */
	@RequestMapping(value="/{mclusterId}/{chartId}/{strategy}",method=RequestMethod.GET)
	public @ResponseBody ResultObject mclusterMonitorCharts(@PathVariable Long mclusterId,@PathVariable Long chartId,@PathVariable Integer strategy,ResultObject result) {
		result.setData(this.monitorProxy.getMonitorViewData(mclusterId,chartId,strategy));
		return result;
	}
	
	/**
	 * Methods Name: mclusterMonitorCharts <br>
	 * Description: 监控视图数目<br>
	 * @author name: wujun
	 * @param mclusterId
	 * @param result
	 * @return
	 */
	@RequestMapping(value="/index",method=RequestMethod.GET)
	public @ResponseBody ResultObject mclusterMonitorChartsCount(ResultObject result) {
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("status", 1);
		result.setData(this.monitorIndexService.selectByMap(map));
		return result;
	}

}
