package com.bhu.vas.business.mdevices;

import java.util.Date;

import javax.annotation.Resource;

import org.junit.Test;

import com.bhu.vas.business.device.mdto.WifiHandsetDeviceRelationMDTO;
import com.bhu.vas.business.device.service.WifiHandsetDeviceRelationMService;
import com.smartwork.msip.cores.cache.relationcache.impl.springmongo.Pagination;
import com.smartwork.msip.localunit.BaseTest;

public class WifiHandsetDeviceRelationMServiceTest extends BaseTest{
	@Resource
	private WifiHandsetDeviceRelationMService wifiHandsetDeviceRelationMService;
	
	//@Test
	public void testSave(){
		wifiHandsetDeviceRelationMService.addRelation("h1", "w1", new Date());
	}
	
	@Test
	public void testFindRelations(){
		Pagination<WifiHandsetDeviceRelationMDTO> result = wifiHandsetDeviceRelationMService.findRelations("w1", 1, 10);
		System.out.println(result.getTotalCount());
		for(WifiHandsetDeviceRelationMDTO mdto : result.getDatas()){
			System.out.println(mdto.getHandsetId());
		}
	}
	
}
