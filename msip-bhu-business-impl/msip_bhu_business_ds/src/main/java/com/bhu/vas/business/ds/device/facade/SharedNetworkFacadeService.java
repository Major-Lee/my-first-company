package com.bhu.vas.business.ds.device.facade;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.bhu.vas.api.helper.VapEnumType;
import com.bhu.vas.api.helper.VapEnumType.SharedNetworkType;
import com.bhu.vas.api.rpc.devices.dto.sharednetwork.ParamSharedNetworkDTO;
import com.bhu.vas.api.rpc.devices.dto.sharednetwork.SharedNetworkSettingDTO;
import com.bhu.vas.api.rpc.devices.dto.sharednetwork.SharedNetworkVTO;
import com.bhu.vas.api.rpc.devices.model.UserDevicesSharedNetwork;
import com.bhu.vas.api.rpc.devices.model.WifiDeviceSharedNetwork;
import com.bhu.vas.api.rpc.devices.notify.ISharedNetworkNotifyCallback;
import com.bhu.vas.business.ds.device.service.UserDevicesSharedNetworkService;
import com.bhu.vas.business.ds.device.service.WifiDeviceService;
import com.bhu.vas.business.ds.device.service.WifiDeviceSharedNetworkService;
import com.bhu.vas.business.ds.user.service.UserDeviceService;
import com.bhu.vas.business.ds.user.service.UserService;
import com.smartwork.msip.exception.BusinessI18nCodeException;
import com.smartwork.msip.jdo.ResponseErrorCode;

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

    @Resource
    private UserDeviceService userDeviceService;
	
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
	 * 此接口在后台backend执行,主要配合doApplySharedNetworksConfig后续执行
	 * 获取用户绑定的所有设备中开启了指定的sharedNetwork的设备，比对是否变化然后进行相关共享网络变更并且发送指令到设备
	 * 每次应用后替换用户指定类型的所有设备的共享网络配置
	 * @param uid
	 * @param sharedNetwork
	 * @return 需要发送指令的mac地址
	 */
	//@Deprecated
	/*public List<String> doApplySharedNetworksConfig2Devices(int uid,VapEnumType.SharedNetworkType sharedNetwork){
		List<String> dmacs = new ArrayList<String>();
		List<String> tmpDmacs = new ArrayList<String>();
		try{
			ModelCriteria mc = new ModelCriteria();
	        mc.createCriteria().andColumnEqualTo("uid", uid);
	        mc.setPageNumber(1);
	        mc.setPageSize(100);
	        EntityIterator<UserDevicePK, UserDevice> it = new KeyBasedEntityBatchIterator<UserDevicePK,UserDevice>(UserDevicePK.class
					,UserDevice.class, userDeviceService.getEntityDao(), mc);
			while(it.hasNext()){
				List<UserDevicePK> nextKeys = it.nextKeys();
				for(UserDevicePK pk:nextKeys){
					String dmac= pk.getMac();
					tmpDmacs.add(dmac);
				}
				if(!tmpDmacs.isEmpty()){
					//未设定的sharedNetwork 或sharedNetwork相等的需要应用
					dmacs.addAll(addDevices2SharedNetwork(uid,sharedNetwork,true,tmpDmacs));
					tmpDmacs.clear();
				}
			}
		}finally{
			if(tmpDmacs != null){
				tmpDmacs.clear();
				tmpDmacs = null;
			}
		}
		return dmacs;
	}*/
	
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
	
	/**
	 * 获取用户的指定的共享网络配置
	 * 如果不存在则建立缺省值
	 * @param uid
	 * @param sharedNetwork
	 * @return
	 */
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
	
	public SharedNetworkSettingDTO fetchDeviceSharedNetworkConf(String mac){
		WifiDeviceSharedNetwork configs = wifiDeviceSharedNetworkService.getById(mac);
		if(configs != null){
			return configs.getInnerModel();
		}else{
			return null;
		}
	}
	
	
	/**
	 * 添加设备应用指定的配置
	 * 如果用户不存在此配置，需要建立新的用户配置
	 * 如果配置不存在则需要新建缺省的进去
	 * 此接口在后台backend执行,主要配合doApplySharedNetworksConfig 和 修改一个或多个设备的共享网络配置的后续执行
	 * @param uid
	 * @param sharednetwork_type
	 * @param sharednetworkMatched true 进行比对，只有sharednetwork_type相同的配置才会更新 false 都更新
	 * @param macs
	 * @return 配置变更了的具体设备地址集合
	 */
	public void addDevices2SharedNetwork(int uid,
			VapEnumType.SharedNetworkType sharednetwork_type,
			boolean sharednetworkMatched,
			List<String> macs,ISharedNetworkNotifyCallback callback){
		if(sharednetwork_type == null){
			throw new BusinessI18nCodeException(ResponseErrorCode.COMMON_DATA_NOTEXIST,new String[]{"sharednetwork_type:".concat(String.valueOf(sharednetwork_type))});
		}
		
		if(macs == null || macs.isEmpty()){
			throw new BusinessI18nCodeException(ResponseErrorCode.COMMON_DATA_VALIDATE_EMPTY,new String[]{"macs:"});
		}
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
				if(sharednetworkMatched){
					if(!sharednetwork_type.getKey().equals(sharednetwork.getSharednetwork_type())){
						continue;
					}
				}
				sharednetwork.setSharednetwork_type(configDto.getNtype());
				SharedNetworkSettingDTO sharedNetworkSettingDTO = sharednetwork.getInnerModel();
				ParamSharedNetworkDTO dbDto = sharedNetworkSettingDTO.getPsn();
				if(dbDto == null || ParamSharedNetworkDTO.wasChanged(configDto, dbDto)){
					sharedNetworkSettingDTO.turnOn(configDto);
					sharednetwork.replaceInnerModel(sharedNetworkSettingDTO);
					wifiDeviceSharedNetworkService.update(sharednetwork);
					result.add(mac_lowercase);
				}else{
					;
				}
			}
		}
		if(callback != null){
			callback.notify(configDto, result);
		}
		//return result;
	}
	
	/**
	 * 单个设备设置共享网络，不更新用户共享网络配置
	 * @param mac
	 * @param configDto
	 * @return
	 */
	public boolean updateDevices2SharedNetwork(String mac,ParamSharedNetworkDTO configDto){
		boolean wasUpdated = false;
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
			wasUpdated = true;
		}else{
			sharednetwork.setSharednetwork_type(configDto.getNtype());
			SharedNetworkSettingDTO sharedNetworkSettingDTO = sharednetwork.getInnerModel();
			ParamSharedNetworkDTO dbDto = sharedNetworkSettingDTO.getPsn();
			if(dbDto == null || ParamSharedNetworkDTO.wasChanged(configDto, dbDto)){
				sharedNetworkSettingDTO.turnOn(configDto);
				sharednetwork.replaceInnerModel(sharedNetworkSettingDTO);
				wifiDeviceSharedNetworkService.update(sharednetwork);
				wasUpdated = true;
			}else{
				;
			}
		}
		return wasUpdated;
	}
	
	
	/**
	 * 移除设备从指定的配置
	 * 需要置Sharednetwork_type null
	 * 需要置turnOff
	 * @param uid
	 * @param sharednetwork_type
	 * @param macs
	 * @return 配置变更了的具体设备地址集合
	 */
	public List<String> removeDevicesFromSharedNetwork(String... macs){
		List<String> result = new ArrayList<String>();
		for(String mac:macs){
			String mac_lowercase = mac.toLowerCase();
			WifiDeviceSharedNetwork sharednetwork = wifiDeviceSharedNetworkService.getById(mac_lowercase);
			if(sharednetwork != null){
				sharednetwork.setSharednetwork_type(null);
				SharedNetworkSettingDTO sharedNetworkSettingDTO = sharednetwork.getInnerModel();
				sharedNetworkSettingDTO.turnOff();
				sharednetwork.putInnerModel(sharedNetworkSettingDTO);
				wifiDeviceSharedNetworkService.update(sharednetwork);
				result.add(mac_lowercase);
			}
		}
		return result;
	}
	
	public void remoteResponseNotifyFromDevice(String mac){
		WifiDeviceSharedNetwork sharedNetwork = wifiDeviceSharedNetworkService.getById(mac);
		if(sharedNetwork != null){
			SharedNetworkSettingDTO settingDto = sharedNetwork.getInnerModel();
			if(!settingDto.isDs()){
				settingDto.remoteNotify();
				sharedNetwork.putInnerModel(settingDto);
				wifiDeviceSharedNetworkService.update(sharedNetwork);
			}
		}
	}
	
	public List<SharedNetworkVTO> fetchSupportedSharedNetwork(){
		return SharedNetworkType.getSharedNetworkVtos();
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
