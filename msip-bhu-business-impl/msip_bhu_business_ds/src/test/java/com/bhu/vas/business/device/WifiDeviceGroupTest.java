package com.bhu.vas.business.device;

import javax.annotation.Resource;

import com.bhu.vas.api.rpc.devices.model.WifiDeviceGroup;
import com.smartwork.msip.cores.orm.support.criteria.ModelCriteria;
import org.junit.Test;

import com.bhu.vas.business.ds.device.service.WifiDeviceGroupService;
import com.smartwork.msip.localunit.BaseTest;

import java.util.List;

public class WifiDeviceGroupTest extends BaseTest{

	@Resource
	WifiDeviceGroupService wifiDeviceGroupService;
	
	@Test
	public void testCleanUpByIds(){
		wifiDeviceGroupService.cleanUpByIds(3, "70,71");
	}



	@Test
	public void testDevicesGroup() {

		WifiDeviceGroup wifiDeviceGroup = wifiDeviceGroupService.getById(100);
		List<String> lists = wifiDeviceGroup.getInnerModels();

		System.out.println(lists);



	}


}
