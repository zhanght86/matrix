package com.letv.portal.clouddb.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.junit.Test;

import com.letv.common.paging.impl.Page;
import com.letv.common.result.ResultObject;
import com.letv.portal.junitBase.AbstractTest;
import com.letv.portal.model.HostModel;
import com.letv.portal.service.IHostService;

public class HostControllerTest extends AbstractTest{
	  
	@Resource
	private IHostService hostService;
	
	/**
	 * Methods Name: list <br>
	 * Description: 查询host的列表数据
	 * @author name: wujun
	 */
	@Test
	public void list(){
		try {
			Page page = new Page();
			page.setCurrentPage(0);
			page.setRecordsPerPage(10);
		
			Map<String,Object> params = new HashMap<String,Object>();
			params.put("hostName", null);
			
			ResultObject obj = new ResultObject();
			obj.setData(this.hostService.findPagebyParams(params, page));
            System.out.print(obj);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Methods Name: buildHost <br>
	 * Description: hostControllerInsert
	 * @author name: wujun
	 */
	@Test
	public void saveHost(){ 
		try {    
			HostModel hostModel = new HostModel();
			hostModel.setHostName("wujun6");
			hostModel.setHostIp("192.168.1.11");
			hostService.insert(hostModel);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	

	/**
	 * Methods Name: deletehost <br>
	 * Description: 删除host
	 * @author name: wujun
	 */
	@Test
	public void deleteHost(){
		try {
            Long id = 2L;
			HostModel hostModel = new HostModel();
			hostModel.setId(id);
           this.hostService.delete(hostModel);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * Methods Name: deletehost <br>
	 * Description: 删除host
	 * @author name: wujun
	 */
	@Test
	public void updateHost(){
		try {
			 HostModel hostModel = new HostModel();
			 hostModel.setHostName("wujunSpeacil");
			 hostModel.setId(2L);
			 this.hostService.update(hostModel);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	@Test
	public void selectByHclusterId(){
		try {
			 HostModel hostModel = new HostModel();
			 Long hclusterId = 12L;
			 List<HostModel>  list= this.hostService.selectByHclusterId(hclusterId);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
}
