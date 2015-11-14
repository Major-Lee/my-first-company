package com.bhu.vas.business.device;

import javax.annotation.Resource;

import org.junit.Test;

import com.bhu.vas.api.helper.OperationCMD;
import com.bhu.vas.api.helper.OperationDS;
import com.bhu.vas.business.ds.device.service.WifiDevicePersistenceCMDStateService;
import com.smartwork.msip.localunit.BaseTest;

public class WifiDevicePersistenceCMDStateServiceTest extends BaseTest{
	@Resource
	WifiDevicePersistenceCMDStateService wifiDevicePersistenceCMDStateService;
	
	@Test
	public void testFilter1CMD(){
		String mac = "84:82:f4:19:01:0c";
		wifiDevicePersistenceCMDStateService.filterPersistenceCMD(mac,OperationCMD.ModifyDeviceSetting,OperationDS.DS_Http_VapModuleCMD_Start,"{\"style\":\"style002\"}");
	}
	
	//@Test
	public void testFilter2CMD(){
		String mac = "84:82:f4:19:01:0c";
		wifiDevicePersistenceCMDStateService.filterPersistenceCMD(mac,OperationCMD.ModifyDeviceSetting,OperationDS.DS_Http_VapModuleCMD_Stop,null);
	}
}
