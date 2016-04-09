package com.bhu.vas.business.device;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.bhu.vas.api.helper.VapEnumType;
import com.bhu.vas.api.rpc.devices.dto.sharednetwork.ParamSharedNetworkDTO;
import com.bhu.vas.api.rpc.devices.dto.sharednetwork.SharedNetworkVTO;
import com.bhu.vas.api.rpc.devices.notify.ISharedNetworkNotifyCallback;
import com.bhu.vas.business.ds.device.facade.SharedNetworksFacadeService;
import com.smartwork.msip.cores.helper.JsonHelper;
import com.smartwork.msip.localunit.BaseTest;
import com.smartwork.msip.localunit.RandomData;
import com.smartwork.msip.localunit.RandomPicker;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SharedNetworksFacadeServiceTest extends BaseTest{
	@Resource
	private SharedNetworksFacadeService sharedNetworksFacadeService;
	
	//@Test
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
		/*ParamSharedNetworkDTO dto = new ParamSharedNetworkDTO();
		dto.setNtype(VapEnumType.SharedNetworkType.SafeSecure.getKey());
		dto.setSsid("w哇哈哈");
		dto.setMax_clients(233);
		dto.setUsers_rx_rate(128);
		dto.setUsers_tx_rate(126);
		
		ParamSharedNetworkDTO.fufillWithDefault(dto);*/
		for(int i = 0;i<100;i++){
			ParamSharedNetworkDTO dto = ParamSharedNetworkDTO.builderDefault(RandomPicker.pick(VapEnumType.SharedNetworkType.values()).getKey());
			dto.setTemplate("0005");
			System.out.println(sharedNetworksFacadeService.doApplySharedNetworksConfig(i, dto));
			System.out.println(dto.getTemplate());
		}
	}
	
	//@Test
	public void test002FetchAllUserSharedNetworkConfAndModify(){
		for(int i = 0;i<5;i++){
			List<ParamSharedNetworkDTO> dtos = sharedNetworksFacadeService.fetchAllUserSharedNetworkConf(i,RandomPicker.pick(VapEnumType.SharedNetworkType.values()));
			for(ParamSharedNetworkDTO dto:dtos){
				System.out.println(JsonHelper.getJSONString(dto));	
			}
			if(!dtos.isEmpty()){
				ParamSharedNetworkDTO current = RandomPicker.pick(dtos);
				System.out.println(JsonHelper.getJSONString(current));
				if(RandomData.flag())
					current.setForce_timeout(100);
				System.out.println(sharedNetworksFacadeService.doApplySharedNetworksConfig(i, current));
			}
			
		}
	}
	
	
	
	//@Test
	public void test003FetchUserSharedNetworkConf(){
			ParamSharedNetworkDTO dto = sharedNetworksFacadeService.fetchUserSharedNetworkConf(4,VapEnumType.SharedNetworkType.SafeSecure,"0003");
			System.out.println("0"+JsonHelper.getJSONString(dto));	
	}
	
	//@Test
	public void test004AddDevices2SharedNetwork(){
		List<String> macs = new ArrayList<>();
		macs.add("84:82:f4:de:26:8a");
		macs.add("84:82:f4:de:26:8c");
		macs.add("84:82:f4:de:26:88");
		sharedNetworksFacadeService.addDevices2SharedNetwork(3,VapEnumType.SharedNetworkType.SafeSecure,"0001",true, 
					macs,new ISharedNetworkNotifyCallback(){
						@Override
						public void notify(ParamSharedNetworkDTO current,
								List<String> dmacs) {
							System.out.println(dmacs);
							
						}
		});
			//System.out.println("1"+JsonHelper.getJSONString(result));	
	}

	
	@Test
	public void test004RemoveDevicesFromSharedNetwork(){
			List<String> result =  sharedNetworksFacadeService.removeDevicesFromSharedNetwork("84:82:f4:de:26:8a","84:82:f4:de:26:8c","84:82:f4:de:26:89");
			System.out.println("2"+JsonHelper.getJSONString(result));	
	}
	
	@Test
	public void test005FetchSupportedSharedNetwork(){
			List<SharedNetworkVTO> result = sharedNetworksFacadeService.fetchSupportedSharedNetwork();
			System.out.println("3"+JsonHelper.getJSONString(result));	
	}
	/*
	//@Test
	public void test100Get(){
		for(int i = 0;i<100;i++){
			UserDevicesSharedNetwork network = sharedNetworkFacadeService.getUserDevicesSharedNetworkService().getById(i);
			System.out.println((network.keySet()));
		}
	}*/
}
