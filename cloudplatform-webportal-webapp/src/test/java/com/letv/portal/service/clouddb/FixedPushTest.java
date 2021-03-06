package com.letv.portal.service.clouddb;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.letv.common.result.ApiResultObject;
import com.letv.portal.fixedPush.IFixedPushService;
import com.letv.portal.junitBase.AbstractTest;

public class FixedPushTest extends AbstractTest {

	private final static Logger logger = LoggerFactory
			.getLogger(UserLoginTest.class);
	@Resource
	private IFixedPushService fixedPushService;

	@Test
	public void testSendFixedInfo() {
		ApiResultObject apiResult = fixedPushService.sendFixedInfo("10.154.156.129",
				"lisx-test", "10.200.83.1", "add");
		System.out.println(apiResult.getResult());
		Assert.assertTrue(apiResult.getAnalyzeResult());
		apiResult = fixedPushService.sendFixedInfo("10.154.156.129", "lisx-test",
				"10.200.83.1", "delete");
		Assert.assertTrue(apiResult.getAnalyzeResult());
	}

}