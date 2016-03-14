package com.bhu.vas.business.ds.device.facade;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.bhu.vas.api.helper.VapEnumType;
import com.bhu.vas.business.ds.device.service.UserDevicesSharedNetworkService;
import com.bhu.vas.business.ds.device.service.WifiDeviceSharedNetworkService;

@Service
public class SharedNetworkFacadeService {
	@Resource
	private WifiDeviceSharedNetworkService wifiDeviceSharedNetworkService;

	@Resource
	private UserDevicesSharedNetworkService userDevicesSharedNetworkService;

	
	public void saveSharedNetworksConfig(){
		
	}
	
	public void removeSharedNetworksConfig(){
		
	}
	
	public void fetchSharedNetworkDevices(VapEnumType.SharedNetworkType sharednetwork_type){
		
	}
	
	public void addDevices2SharedNetwork(VapEnumType.SharedNetworkType sharednetwork_type,String... macs){
		
	}
	
}
