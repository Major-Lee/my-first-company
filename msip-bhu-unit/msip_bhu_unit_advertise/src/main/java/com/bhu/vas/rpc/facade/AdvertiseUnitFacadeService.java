package com.bhu.vas.rpc.facade;

import java.util.List;

import org.springframework.stereotype.Service;

import com.bhu.vas.business.bucache.redis.serviceimpl.devices.WifiDevicePositionListService;

/**
 * @author xiaowei by 16/10/20
 */
@Service
public class AdvertiseUnitFacadeService {
	
	/**
	 * 获取现有设备地理位置
	 * @param province
	 * @param city
	 * @return
	 */
	public List<String> fetchDevicePositionDistribution(String province,String city){
		if(city !=null){
			return WifiDevicePositionListService.getInstance().fetchCity(city);
		}else if(province !=null){
			return WifiDevicePositionListService.getInstance().fetchProvince(province);
		}else{
			return WifiDevicePositionListService.getInstance().fetchAllProvince();
		}
	}
}
