package com.bhu.vas.business.device;

import javax.annotation.Resource;

import org.junit.Test;

import com.bhu.vas.api.helper.VapEnumType.SharedNetworkType;
import com.bhu.vas.api.rpc.devices.dto.sharednetwork.ParamSharedNetworkDTO;
import com.bhu.vas.api.rpc.devices.model.UserDevicesSharedNetwork;
import com.bhu.vas.business.ds.device.service.UserDevicesSharedNetworkService;
import com.smartwork.msip.localunit.BaseTest;
import com.smartwork.msip.localunit.RandomData;
import com.smartwork.msip.localunit.RandomPicker;

public class UserDevicesSharedNetworkServiceTest extends BaseTest{
	@Resource
	UserDevicesSharedNetworkService userDevicesSharedNetworkService;
	
	//@Test
	public void testFilter1CMD(){
		for(int i = 0;i<100;i++){
			UserDevicesSharedNetwork shared = new UserDevicesSharedNetwork();
			shared.setId(i);
			SharedNetworkType type1 = RandomPicker.pick(SharedNetworkType.values());
			shared.putInnerModel(type1.getKey(),ParamSharedNetworkDTO.builderDefault(type1.getKey(),RandomData.flag()));
			
			SharedNetworkType type2 = RandomPicker.pick(SharedNetworkType.values());
			shared.putInnerModel(type2.getKey(),ParamSharedNetworkDTO.builderDefault(type2.getKey(),RandomData.flag()));
			userDevicesSharedNetworkService.insert(shared);
		}
	}
	@Test
	public void testGet(){
		for(int i = 0;i<100;i++){
			UserDevicesSharedNetwork network = userDevicesSharedNetworkService.getById(i);
			System.out.println((network.keySet()));
		}
	}
}
