package com.bhu.vas.business.device;

import javax.annotation.Resource;

import org.junit.Test;

import com.bhu.vas.business.ds.device.service.WifiDeviceGroupService;
import com.smartwork.msip.localunit.BaseTest;

public class WifiDeviceGroupTest extends BaseTest{

	@Resource
	WifiDeviceGroupService wifiDeviceGroupService;
	
	@Test
	public void testCleanUpByIds(){
		wifiDeviceGroupService.cleanUpByIds(3, "70,71");
	}

}
