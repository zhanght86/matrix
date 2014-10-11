package com.letv.portal.proxy.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.letv.portal.model.DbUserModel;
import com.letv.portal.proxy.IDbUserProxy;
import com.letv.portal.service.IBaseService;
import com.letv.portal.service.IDbUserService;


@Component
public class DbUserProxyImpl extends BaseProxyImpl<DbUserModel> implements
		IDbUserProxy{
	
	private final static Logger logger = LoggerFactory.getLogger(DbUserProxyImpl.class);

	@Autowired
	private IDbUserService dbUserService;
	
	@Override
	public IBaseService<DbUserModel> getService() {
		return dbUserService;
	}
	
	
}