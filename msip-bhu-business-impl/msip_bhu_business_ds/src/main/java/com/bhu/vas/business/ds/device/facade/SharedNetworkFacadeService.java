package com.bhu.vas.business.ds.device.facade;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.bhu.vas.api.helper.VapEnumType;
import com.bhu.vas.api.rpc.devices.dto.sharednetwork.ParamSharedNetworkDTO;
import com.bhu.vas.api.rpc.devices.dto.sharednetwork.SharedNetworkSettingDTO;
import com.bhu.vas.api.rpc.devices.model.UserDevicesSharedNetwork;
import com.bhu.vas.api.rpc.devices.model.WifiDeviceSharedNetwork;
import com.bhu.vas.business.ds.device.service.UserDevicesSharedNetworkService;
import com.bhu.vas.business.ds.device.service.WifiDeviceService;
import com.bhu.vas.business.ds.device.service.WifiDeviceSharedNetworkService;
import com.bhu.vas.business.ds.user.service.UserService;

@Service
public class SharedNetworkFacadeService {
	@Resource
	private UserService userService;

	@Resource
	private WifiDeviceService wifiDeviceService;
	
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
	
	/**
	 * 获取用户关于共享网络的配置
	 * 如果为空则采用缺省值构建
	 * @param uid
	 * @param sharednetwork_type
	 */
	public Collection<ParamSharedNetworkDTO> fetchAllUserSharedNetworkConf(int uid){
		UserDevicesSharedNetwork configs = userDevicesSharedNetworkService.getById(uid);
		//paramDto = ParamSharedNetworkDTO.fufillWithDefault(paramDto);
		if(configs == null){
			configs = new UserDevicesSharedNetwork();
			configs.setId(uid);
			configs.putInnerModel(VapEnumType.SharedNetworkType.SafeSecure.getKey(), ParamSharedNetworkDTO.builderDefault(VapEnumType.SharedNetworkType.SafeSecure.getKey()));
			configs.putInnerModel(VapEnumType.SharedNetworkType.Uplink.getKey(), ParamSharedNetworkDTO.builderDefault(VapEnumType.SharedNetworkType.Uplink.getKey()));
			userDevicesSharedNetworkService.insert(configs);
		}else{
			if(configs.getExtension().isEmpty()){
				configs.putInnerModel(VapEnumType.SharedNetworkType.SafeSecure.getKey(), ParamSharedNetworkDTO.builderDefault(VapEnumType.SharedNetworkType.SafeSecure.getKey()));
				configs.putInnerModel(VapEnumType.SharedNetworkType.Uplink.getKey(), ParamSharedNetworkDTO.builderDefault(VapEnumType.SharedNetworkType.Uplink.getKey()));
				userDevicesSharedNetworkService.update(configs);
			}
		}
		return configs.getExtension().values();
	}
	
	
	public ParamSharedNetworkDTO fetchUserSharedNetworkConf(int uid,VapEnumType.SharedNetworkType sharedNetwork){
		UserDevicesSharedNetwork configs = userDevicesSharedNetworkService.getById(uid);
		ParamSharedNetworkDTO dto = null;
		//paramDto = ParamSharedNetworkDTO.fufillWithDefault(paramDto);
		if(configs == null){
			configs = new UserDevicesSharedNetwork();
			configs.setId(uid);
			dto = ParamSharedNetworkDTO.builderDefault(sharedNetwork.getKey());
			configs.putInnerModel(sharedNetwork.getKey(), dto);
			userDevicesSharedNetworkService.insert(configs);
		}else{
			if(!configs.containsKey(sharedNetwork.getKey())){
				dto = ParamSharedNetworkDTO.builderDefault(sharedNetwork.getKey());
				configs.putInnerModel(sharedNetwork.getKey(), dto);
				userDevicesSharedNetworkService.update(configs);
			}else{
				dto = configs.getInnerModel(sharedNetwork.getKey());
			}
		}
		return dto;
	}
	
	/**
	 * 添加设备应用指定的配置
	 * 如果配置不存在则需要新建缺省的进去
	 * @param uid
	 * @param sharednetwork_type
	 * @param macs
	 * @return 配置变更了的具体设备地址集合
	 */
	public List<String> addDevices2SharedNetwork(int uid,VapEnumType.SharedNetworkType sharednetwork_type,String... macs){
		List<String> result = new ArrayList<String>();
		ParamSharedNetworkDTO configDto = fetchUserSharedNetworkConf(uid,sharednetwork_type);
		//验证设备是否真实绑定
		for(String mac:macs){
			String mac_lowercase = mac.toLowerCase();
			WifiDeviceSharedNetwork sharednetwork = wifiDeviceSharedNetworkService.getById(mac_lowercase);
			if(sharednetwork == null){
				sharednetwork = new WifiDeviceSharedNetwork();
				sharednetwork.setId(mac_lowercase);
				sharednetwork.setSharednetwork_type(configDto.getNtype());
				SharedNetworkSettingDTO sharedNetworkSettingDTO = new SharedNetworkSettingDTO();
				sharedNetworkSettingDTO.turnOn(configDto);
				sharednetwork.putInnerModel(sharedNetworkSettingDTO);
				wifiDeviceSharedNetworkService.insert(sharednetwork);
				result.add(mac_lowercase);
			}else{
				SharedNetworkSettingDTO sharedNetworkSettingDTO = sharednetwork.getInnerModel();
				ParamSharedNetworkDTO dbDto = sharedNetworkSettingDTO.getPsn();
				if(dbDto == null && ParamSharedNetworkDTO.wasChanged(configDto, dbDto)){
					sharedNetworkSettingDTO.turnOn(configDto);
					sharednetwork.replaceInnerModel(sharedNetworkSettingDTO);
					wifiDeviceSharedNetworkService.update(sharednetwork);
					result.add(mac_lowercase);
				}else{
					;
				}
			}
		}
		return result;
	}
	
	public List<VapEnumType.SharedNetworkType> fetchSupportedSharedNetwork(){
		return Arrays.asList(VapEnumType.SharedNetworkType.values());
	}

	public UserService getUserService() {
		return userService;
	}

	public WifiDeviceService getWifiDeviceService() {
		return wifiDeviceService;
	}

	public WifiDeviceSharedNetworkService getWifiDeviceSharedNetworkService() {
		return wifiDeviceSharedNetworkService;
	}

	public UserDevicesSharedNetworkService getUserDevicesSharedNetworkService() {
		return userDevicesSharedNetworkService;
	}
}
