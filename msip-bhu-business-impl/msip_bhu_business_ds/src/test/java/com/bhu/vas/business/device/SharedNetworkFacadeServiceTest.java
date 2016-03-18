package com.bhu.vas.business.device;

import java.util.Collection;

import javax.annotation.Resource;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.bhu.vas.api.helper.VapEnumType;
import com.bhu.vas.api.rpc.devices.dto.sharednetwork.ParamSharedNetworkDTO;
import com.bhu.vas.api.rpc.devices.model.UserDevicesSharedNetwork;
import com.bhu.vas.business.ds.device.facade.SharedNetworkFacadeService;
import com.smartwork.msip.cores.helper.JsonHelper;
import com.smartwork.msip.localunit.BaseTest;

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
		dto.setNtype(VapEnumType.SharedNetworkType.SafeSecure.getKey());
		dto.setSsid("w哇哈哈");
		dto.setMax_clients(233);
		dto.setUsers_rx_rate(128);
		dto.setUsers_tx_rate(126);
		
		ParamSharedNetworkDTO.fufillWithDefault(dto);
		for(int i = 0;i<100;i++){
			System.out.println(sharedNetworkFacadeService.doApplySharedNetworksConfig(i, dto));
		}
	}
	@Test
	public void test002FetchAllUserSharedNetworkConf(){
		for(int i = 0;i<100;i++){
			Collection<ParamSharedNetworkDTO> dtos = sharedNetworkFacadeService.fetchAllUserSharedNetworkConf(i);
			for(ParamSharedNetworkDTO dto:dtos){
				System.out.println(JsonHelper.getJSONString(dto));	
			}
			
		}
	}
	
	@Test
	public void test002FetchUserSharedNetworkConf(){
		for(int i = 0;i<100;i++){
			ParamSharedNetworkDTO dto = sharedNetworkFacadeService.fetchUserSharedNetworkConf(i,VapEnumType.SharedNetworkType.SafeSecure);
			System.out.println("2"+JsonHelper.getJSONString(dto));	
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
