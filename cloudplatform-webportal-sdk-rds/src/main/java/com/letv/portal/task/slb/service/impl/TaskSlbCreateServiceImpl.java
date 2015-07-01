package com.letv.portal.task.slb.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.letv.common.result.ApiResultObject;
import com.letv.portal.model.HostModel;
import com.letv.portal.model.slb.SlbCluster;
import com.letv.portal.model.slb.SlbServer;
import com.letv.portal.model.task.TaskResult;
import com.letv.portal.model.task.service.IBaseTaskService;
import com.letv.portal.python.service.ISlbPythonService;

@Service("taskSlbCreateService")
public class TaskSlbCreateServiceImpl extends BaseTask4SlbServiceImpl implements IBaseTaskService{
	
	@Autowired
	private ISlbPythonService slbPythonService;
	@Value("${matrix.slb.default.image}")
	private String MATRIX_SLB_DEFAULT_IMAGE;
	private final static Logger logger = LoggerFactory.getLogger(TaskSlbCreateServiceImpl.class);
	
	@Override
	public TaskResult execute(Map<String, Object> params) throws Exception{
		TaskResult tr = super.execute(params);
		if(!tr.isSuccess())
			return tr;
		
		SlbServer server = super.getServer(params);
		SlbCluster cluster = super.getCluster(params);
		HostModel host = super.getHost(cluster.getHclusterId());
		
		Map<String,String> map = new HashMap<String,String>();
		map.put("containerClusterName",cluster.getClusterName());
		map.put("componentType", "gbalancerCluster");
		map.put("networkMode", "ip");
		map.put("image", MATRIX_SLB_DEFAULT_IMAGE);
		ApiResultObject result = this.slbPythonService.createContainer(map,host.getHostIp(),host.getName(),host.getPassword());
		tr = analyzeRestServiceResult(result);
		
		tr.setParams(params);
		return tr;
	}
	
}
