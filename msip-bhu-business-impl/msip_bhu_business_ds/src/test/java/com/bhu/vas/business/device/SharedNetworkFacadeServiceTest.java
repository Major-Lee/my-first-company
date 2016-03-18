package com.bhu.vas.business.device;

import javax.annotation.Resource;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.bhu.vas.api.helper.VapEnumType;
import com.bhu.vas.api.helper.VapEnumType.SharedNetworkType;
import com.bhu.vas.api.rpc.devices.dto.sharednetwork.ParamSharedNetworkDTO;
import com.bhu.vas.api.rpc.devices.model.UserDevicesSharedNetwork;
import com.bhu.vas.business.ds.device.facade.SharedNetworkFacadeService;
import com.smartwork.msip.localunit.BaseTest;
import com.smartwork.msip.localunit.RandomData;
import com.smartwork.msip.localunit.RandomPicker;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SharedNetworkFacadeServiceTest extends BaseTest{
	@Resource
	private SharedNetworkFacadeService sharedNetworkFacadeService;
	
	@Test
	public void test001BatchCreateUserDevicesSharedNetwork(){
		/*UserDevicesSharedNetwork shared = null;
		for(int i = 0;i<100;i++){
			shared = new UserDevicesSharedNetwork();
			shared.setId(i);
			SharedNetworkType type1 = RandomPicker.pick(SharedNetworkType.values());
			shared.putInnerModel(type1.getKey(),ParamSharedNetworkDTO.builderDefault(type1.getKey()));
			SharedNetworkType type2 = RandomPicker.pick(SharedNetworkType.values());
			shared.putInnerModel(type2.getKey(),ParamSharedNetworkDTO.builderDefault(type2.getKey()));
			sharedNetworkFacadeService.getUserDevicesSharedNetworkService().insert(shared);
		}*/
		ParamSharedNetworkDTO dto = new ParamSharedNetworkDTO();
		dto.setNtype(VapEnumType.SharedNetworkType.Uplink.getKey());
		dto.setMax_clients(233);
		dto.setUsers_rx_rate(128);
		dto.setUsers_tx_rate(126);
		
		ParamSharedNetworkDTO.fufillWithDefault(dto);
		UserDevicesSharedNetwork shared = null;
		for(int i = 0;i<100;i++){
			System.out.println(sharedNetworkFacadeService.doApplySharedNetworksConfig(i, dto));
		}
	}
	
	
	
	
	//@Test
	public void test100Get(){
		for(int i = 0;i<100;i++){
			UserDevicesSharedNetwork network = sharedNetworkFacadeService.getUserDevicesSharedNetworkService().getById(i);
			System.out.println((network.keySet()));
		}
	}
}
