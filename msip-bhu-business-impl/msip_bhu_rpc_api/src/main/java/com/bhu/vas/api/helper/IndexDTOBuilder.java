package com.bhu.vas.api.helper;

import java.util.List;

import com.bhu.vas.api.dto.search.WifiDeviceIndexDTO;
import com.bhu.vas.api.rpc.devices.model.WifiDevice;

public class IndexDTOBuilder {
	
	public static WifiDeviceIndexDTO builderWifiDeviceIndexDTO(WifiDevice entity, List<Long> groupids){
		WifiDeviceIndexDTO indexDto = new WifiDeviceIndexDTO();
		indexDto.setWifiId(entity.getId());
		indexDto.setCountry(entity.getCountry());
		indexDto.setCity(entity.getCity());
		indexDto.setProvince(entity.getProvince());
		indexDto.setDistrict(entity.getDistrict());
		indexDto.setStreet(entity.getStreet());
		indexDto.setFormat_address(entity.getFormatted_address());
		indexDto.setWorkmodel(entity.getWork_mode());
		indexDto.setDevicetype(entity.getHdtype());
		indexDto.setConfigmodel(entity.getConfig_mode());
		indexDto.setOrigswver(entity.getOrig_swver());
		indexDto.setNvd(DeviceHelper.isNewOrigSwverDevice(entity.getOrig_swver()) ? 1 : 0);
		if(entity.isOnline()){
			indexDto.setOnline(WifiDeviceIndexDTO.Online_Status);
		}else{
			indexDto.setCount(0);
			indexDto.setOnline(WifiDeviceIndexDTO.offline_Status);
		}
//		indexDto.setCount(1);
//		indexDto.setOnline(1);
		indexDto.setGroupids(groupids);
		indexDto.setLat(entity.getLat());
		indexDto.setLon(entity.getLon());
		//System.out.println(entity.getId() + "-" +entity.getCreated_at());
		indexDto.setRegister_at(entity.getCreated_at().getTime());
		return indexDto;
	}
}
