package com.letv.portal.service.gce.impl;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.letv.common.dao.IBaseDao;
import com.letv.portal.dao.gce.IGceContainerDao;
import com.letv.portal.model.gce.GceContainer;
import com.letv.portal.service.gce.IGceContainerService;
import com.letv.portal.service.impl.BaseServiceImpl;

@Service("gceContainerService")
public class GceContainerServiceImpl extends BaseServiceImpl<GceContainer> implements IGceContainerService{
	
	private final static Logger logger = LoggerFactory.getLogger(GceContainerServiceImpl.class);
	
	@Resource
	private IGceContainerDao gceContainerDao;

	public GceContainerServiceImpl() {
		super(GceContainer.class);
	}

	@Override
	public IBaseDao<GceContainer> getDao() {
		return this.gceContainerDao;
	}

}
