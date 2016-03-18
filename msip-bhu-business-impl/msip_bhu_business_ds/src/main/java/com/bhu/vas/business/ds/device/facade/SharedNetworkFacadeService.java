package com.bhu.vas.business.ds.device.facade;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.bhu.vas.api.helper.VapEnumType;
import com.bhu.vas.api.rpc.devices.dto.sharednetwork.ParamSharedNetworkDTO;
import com.bhu.vas.api.rpc.devices.model.UserDevicesSharedNetwork;
import com.bhu.vas.business.ds.device.service.UserDevicesSharedNetworkService;
import com.bhu.vas.business.ds.device.service.WifiDeviceSharedNetworkService;

@Service
public class SharedNetworkFacadeService {
	@Resource
	private WifiDeviceSharedNetworkService wifiDeviceSharedNetworkService;

	@Resource
	private UserDevicesSharedNetworkService userDevicesSharedNetworkService;

	/**
	 * 接受页面传递的参数，
	 * 1、比对配置是否变化，如变化则更新用户的配置
	 * 2、配置变化了在其它调用程序发送异步消息到后台，批量更新其绑定的属于ntype的设备
	 * @param uid
	 * @param paramDto
	 * @return 配置是否变化了
	 */
	public boolean doApplySharedNetworksConfig(int uid,ParamSharedNetworkDTO paramDto){
		boolean configChanged = false;
		UserDevicesSharedNetwork configs = userDevicesSharedNetworkService.getById(uid);
		paramDto = ParamSharedNetworkDTO.fufillWithDefault(paramDto);
		if(configs == null){
			configs = new UserDevicesSharedNetwork();
			configs.setId(uid);
			configs.putInnerModel(paramDto.getNtype(), paramDto);
			userDevicesSharedNetworkService.insert(configs);
			configChanged = true;
		}else{
			ParamSharedNetworkDTO fromdb = configs.getInnerModel(paramDto.getNtype());
			if(fromdb == null || ParamSharedNetworkDTO.wasChanged(fromdb, paramDto)){
				//比对是否变化了
					configs.putInnerModel(paramDto.getNtype(), paramDto);
					userDevicesSharedNetworkService.update(configs);
					configChanged = true;
			}else{
				configChanged = false;
			}
		}
		return configChanged;
	}
	
	/**
	 * 此接口在后台backend执行
	 * 根据每个设备的work_mode生成指令
	 * @param uid
	 * @param paramDto
	 */
	public void doApplySharedNetworksConfig2Devices(int uid,ParamSharedNetworkDTO paramDto){
		
	}
	
	public void saveSharedNetworksConfig(){
		
	}
	
	public void removeSharedNetworksConfig(){
		
	}
	
	public void fetchSharedNetworkDevices(VapEnumType.SharedNetworkType sharednetwork_type){
		
	}
	
	public void addDevices2SharedNetwork(VapEnumType.SharedNetworkType sharednetwork_type,String... macs){
		
	}
	
}
