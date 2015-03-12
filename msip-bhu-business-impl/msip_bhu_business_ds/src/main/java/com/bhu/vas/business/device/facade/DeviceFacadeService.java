package com.bhu.vas.business.device.facade;

import java.util.Date;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.bhu.vas.api.rpc.devices.dto.WifiDeviceDTO;
import com.bhu.vas.api.rpc.devices.model.WifiDevice;
import com.bhu.vas.business.bucache.redis.serviceimpl.devices.WifiDevicePresentService;
import com.bhu.vas.business.device.service.WifiDeviceService;

@Service
public class DeviceFacadeService {
	@Resource
	private WifiDeviceService wifiDeviceService;
	
	/**
	 * 
	 * wifi设备上线
	 * 1：wifi设备基础信息更新
	 * 2：wifi设备在线更新
	 * @param dto
	 */
	public void wifiDeviceRegister(WifiDeviceDTO dto){
		if(dto == null) return;
		if(StringUtils.isEmpty(dto.getMac())) return;
		//1:wifi设备基础信息更新
		WifiDevice wifi_device_entity = wifiDeviceDtoToEntity(dto);
		WifiDevice exist_wifi_device_entity = wifiDeviceService.getById(wifi_device_entity.getId());
		if(exist_wifi_device_entity == null){
			wifiDeviceService.insert(wifi_device_entity);
		}else{
			wifiDeviceService.update(wifi_device_entity);
		}
		//2：wifi设备在线更新
		WifiDevicePresentService.getInstance().addPresent(wifi_device_entity.getId(), dto.getCmId());
	}
	
	
	/*----------------------------DTO to Entity-----------------------------------*/
	
	public WifiDevice wifiDeviceDtoToEntity(WifiDeviceDTO dto){
		WifiDevice wifi_device_entity = new WifiDevice();
		wifi_device_entity.setId(dto.getMac());
		wifi_device_entity.setHdtype(dto.getHdtype());
		wifi_device_entity.setOrig_vendor(dto.getOrig_vendor());
		wifi_device_entity.setOrig_model(dto.getOrig_model());
		wifi_device_entity.setOrig_hdver(dto.getOrig_hdver());
		wifi_device_entity.setOrig_swver(dto.getOrig_swver());
		wifi_device_entity.setOem_vendor(dto.getOem_vendor());
		wifi_device_entity.setOem_model(dto.getOem_model());
		wifi_device_entity.setOem_hdver(dto.getOem_hdver());
		wifi_device_entity.setOem_swver(dto.getOem_swver());
		wifi_device_entity.setSn(dto.getSn());
		wifi_device_entity.setIp(dto.getIp());
		wifi_device_entity.setWan_ip(dto.getWan_ip());
		wifi_device_entity.setConfig_sequence(dto.getConfig_sequence());
		wifi_device_entity.setBuild_info(dto.getBuild_info());
		wifi_device_entity.setConfig_model_ver(dto.getConfig_model_ver());
		wifi_device_entity.setConfig_mode(dto.getConfig_mode());
		wifi_device_entity.setWork_mode(dto.getWork_mode());
		wifi_device_entity.setConfig_mode(dto.getConfig_mode());
		wifi_device_entity.setLast_reged_at(new Date());
		wifi_device_entity.setOnline(true);
		return wifi_device_entity;
	}
}
