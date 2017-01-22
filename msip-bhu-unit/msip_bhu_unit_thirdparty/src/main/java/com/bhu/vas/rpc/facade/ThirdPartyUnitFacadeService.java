package com.bhu.vas.rpc.facade;

import javax.annotation.Resource;

import org.elasticsearch.common.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.bhu.vas.api.dto.ret.setting.WifiDeviceSettingDTO;
import com.bhu.vas.api.dto.ret.setting.WifiDeviceSettingRadioDTO;
import com.bhu.vas.api.dto.ret.setting.WifiDeviceSettingVapDTO;
import com.bhu.vas.api.helper.OperationCMD;
import com.bhu.vas.api.helper.OperationDS;
import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.charging.model.WifiDeviceSharedealConfigs;
import com.bhu.vas.api.rpc.devices.model.WifiDevice;
import com.bhu.vas.api.rpc.devices.model.WifiDeviceSetting;
import com.bhu.vas.api.rpc.task.dto.TaskResDTO;
import com.bhu.vas.api.rpc.task.iservice.ITaskRpcService;
import com.bhu.vas.api.rpc.task.model.WifiDeviceDownTask;
import com.bhu.vas.api.rpc.thirdparty.dto.GomeConfigDTO;
import com.bhu.vas.api.rpc.thirdparty.dto.GomeDeviceDTO;
import com.bhu.vas.business.bucache.redis.serviceimpl.thirdparty.ThirdPartyDeviceService;
import com.bhu.vas.business.ds.charging.service.WifiDeviceSharedealConfigsService;
import com.bhu.vas.business.ds.device.service.WifiDeviceService;
import com.bhu.vas.business.ds.device.service.WifiDeviceSettingService;
import com.smartwork.msip.business.runtimeconf.BusinessRuntimeConfiguration;
import com.smartwork.msip.cores.helper.JsonHelper;
import com.smartwork.msip.exception.BusinessI18nCodeException;
import com.smartwork.msip.jdo.ResponseErrorCode;

/**
 * 第三方业务的service
 * @author yetao
 *
 */
@Service
public class ThirdPartyUnitFacadeService {
	private final Logger logger = LoggerFactory.getLogger(ThirdPartyUnitFacadeService.class);

	@Resource
	private ITaskRpcService taskRpcService;

	@Resource 
	private WifiDeviceSharedealConfigsService wifiDeviceSharedealConfigsService;

	@Resource
	private WifiDeviceSettingService wifiDeviceSettingService;
	
	@Resource
	private WifiDeviceService wifiDeviceService;
	
	/**
	 * 绑定国美设备，需要校验设备一级分销商是国美，才允许绑定
	 * @param mac
	 * @return
	 */
	public Boolean gomeBindDevice(String mac){
		if(ThirdPartyDeviceService.isThirdPartyDevice(mac)){
			ThirdPartyDeviceService.bindDevice(mac);
			return Boolean.TRUE;
		} else {
			WifiDeviceSharedealConfigs wifiDeviceSharedeal = wifiDeviceSharedealConfigsService.getById(mac);
			if(wifiDeviceSharedeal == null || wifiDeviceSharedeal.getDistributor() != BusinessRuntimeConfiguration.GomeDistributorId)
				throw new BusinessI18nCodeException(ResponseErrorCode.DEVICE_DATA_NOT_EXIST);
			ThirdPartyDeviceService.bindDevice(mac);
		}
		return Boolean.TRUE;
	}
	
	/**
	 * 解绑国美设备，只有已经再redis中存在的数据，才会解绑
	 * @param mac
	 * @return
	 */
	public Boolean gomeUnBindDevice(String mac){
		if(ThirdPartyDeviceService.isThirdPartyDevice(mac))
			ThirdPartyDeviceService.unBindDevice(mac);
		return Boolean.TRUE;
	}
	
	
	
	private void _callTaskCreate(String mac, String subopt, Object obj){
		String extparams = JsonHelper.getJSONString(obj);
		try{
			RpcResponseDTO<TaskResDTO> result = taskRpcService.createNewTask(BusinessRuntimeConfiguration.Sys_Uid, mac,
					OperationCMD.ModifyDeviceSetting.getNo(), subopt, extparams, WifiDeviceDownTask.Task_LOCAL_CHANNEL, null);
			if(result.hasError()){
				throw new BusinessI18nCodeException(result.getErrorCode());
			}
		}catch(Exception e){
			e.printStackTrace();
			throw e;
		}
	}
	
	/**
	 * 国美设备控制接口，需要校验是否国美设备
	 * @param mac
	 * @param dto
	 * @return
	 */
	public Boolean gomeDeviceControl(String mac, GomeConfigDTO dto){

		if(!ThirdPartyDeviceService.isThirdPartyDevice(mac)){
			throw new BusinessI18nCodeException(ResponseErrorCode.DEVICE_DATA_NOT_EXIST);
		}
		
		WifiDeviceSetting wifiDeviceSetting = wifiDeviceSettingService.getById(mac);

		if(wifiDeviceSetting == null)
			throw new BusinessI18nCodeException(ResponseErrorCode.DEVICE_DATA_NOT_EXIST);

		WifiDeviceSettingDTO settingDto = wifiDeviceSetting.getInnerModel();
		
		boolean change = false;
		
		WifiDeviceSettingVapDTO vap = settingDto.getVaps().get(0);
		
		if(StringUtils.isNotEmpty(dto.getSsid()) && !vap.getSsid().equals(dto.getSsid())){
			vap.setSsid(dto.getSsid());
			change = true;
		}
		//修改ssid和密码
		if(dto.getPassword() != null && dto.getPassword().equals(vap.getAuth_key())){
			if(StringUtils.isEmpty(dto.getPassword())){
				vap.setAuth(WifiDeviceSetting.AUTH_MODE_OPEN);
			} else {
				vap.setAuth(WifiDeviceSetting.AUTH_MODE_WPA2);
			}
			vap.setAuth_key(dto.getPassword());
			vap.setAuth_key_rsa("");
			change = true;
		}
		if(change)
			_callTaskCreate(mac, OperationDS.DS_VapPassword.getNo(), vap);
		
		//修改信号强度 
		change = false;
		WifiDeviceSettingRadioDTO radio = settingDto.getRadios().get(0);
		if(dto.getPower() != null && dto.getPower() != Integer.parseInt(radio.getPower())){
			radio.setPower(String.valueOf(dto.getPower()));
			change = true;
		}
		if(change)
			_callTaskCreate(mac, OperationDS.DS_Power.getNo(), radio);
		
		//增加黑名单
		
		//删除
		
		return Boolean.TRUE;
	}

	public GomeDeviceDTO gomeDeviceOnlineGet(String mac) {
		if(!ThirdPartyDeviceService.isThirdPartyDevice(mac)){
			throw new BusinessI18nCodeException(ResponseErrorCode.DEVICE_DATA_NOT_EXIST);
		}
		WifiDevice device = wifiDeviceService.getById(mac);
		GomeDeviceDTO dto = new GomeDeviceDTO();
		String online = "0";
		if (device.isOnline())
			online = "1";
		dto.setOnline(online);
		return dto;
	}

}
