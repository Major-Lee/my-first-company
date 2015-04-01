package com.bhu.vas.business.mdevices;

import javax.annotation.Resource;

import org.junit.Test;

import com.bhu.vas.business.ds.device.mdto.WifiHandsetDeviceLoginCountMDTO;
import com.bhu.vas.business.ds.device.service.WifiHandsetDeviceLoginCountMService;
import com.smartwork.msip.cores.cache.relationcache.impl.springmongo.Pagination;
import com.smartwork.msip.localunit.BaseTest;

public class WifiHandsetDeviceLoginCountMServiceTest extends BaseTest{
	@Resource
	private WifiHandsetDeviceLoginCountMService wifiHandsetDeviceLoginCountMService;
	
	@Test
	public void testIncr(){
		wifiHandsetDeviceLoginCountMService.incrCount("w1",4);
	}
	
	
	//@Test
	public void testFind(){
		Pagination<WifiHandsetDeviceLoginCountMDTO> result = wifiHandsetDeviceLoginCountMService.findWifiDevicesOrderMaxHandset(1, 10);
		System.out.println(result.getTotalCount());
		for(WifiHandsetDeviceLoginCountMDTO mdto : result.getDatas()){
			System.out.println(mdto.getCount());
		}
	}
	
}
