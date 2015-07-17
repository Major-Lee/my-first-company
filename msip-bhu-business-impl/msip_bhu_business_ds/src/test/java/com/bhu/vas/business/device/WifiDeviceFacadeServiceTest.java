package com.bhu.vas.business.device;

import java.util.List;

import javax.annotation.Resource;

import org.junit.Test;

import com.bhu.vas.business.ds.device.facade.DeviceFacadeService;
import com.smartwork.msip.localunit.BaseTest;

public class WifiDeviceFacadeServiceTest extends BaseTest{

	@Resource
	DeviceFacadeService deviceFacadeService;
	

	@Test
	public void fetchWifiDevicePersistenceCMDTest() throws InterruptedException{
		List<String> payloads = deviceFacadeService.fetchWifiDevicePersistenceCMD("84:82:f4:19:01:0c");
		for(String payload:payloads){
			Thread.sleep(1000);
			System.out.println(payload);
		}
	}
}
