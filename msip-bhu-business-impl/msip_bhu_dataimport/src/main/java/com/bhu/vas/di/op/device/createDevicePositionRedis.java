package com.bhu.vas.di.op.device;

import java.util.List;
import java.util.Map;

import org.elasticsearch.common.lang3.StringUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import com.bhu.vas.business.bucache.redis.serviceimpl.devices.WifiDevicePositionListService;
import com.bhu.vas.business.ds.device.service.WifiDeviceService;
import com.smartwork.msip.cores.helper.JsonHelper;

public class createDevicePositionRedis {
	
	public static void main(String[] args) {
		ApplicationContext ctx = new FileSystemXmlApplicationContext("classpath*:com/bhu/vas/di/business/dataimport/dataImportCtx.xml");
		WifiDeviceService  wifiDeviceService = (WifiDeviceService)ctx.getBean("wifiDeviceService");
		
		List<String> provinceList =  wifiDeviceService.selectByField("province", true, true, null, null);
		System.out.println(JsonHelper.getJSONString(provinceList));
		for(String province :provinceList){
			if(StringUtils.isNotBlank(province)){
				boolean provinceFilter = isEnglish(province);
				if(provinceFilter){
					continue;
				}
				WifiDevicePositionListService.getInstance().generateAllProvince(province);
				System.out.println(province);
				
				List<String> cityList =  wifiDeviceService.selectByField("city", true, true, "province", province);
				for(String city :cityList){
					if(StringUtils.isNoneBlank(city)){
						boolean cityFilter = isEnglish(province);
						if(cityFilter){
							continue;
						}
						WifiDevicePositionListService.getInstance().generateProvince(province, city);
						System.out.println(province +"-"+city);
						
						List<String> districtList =  wifiDeviceService.selectByField("district", true, true, "city", city);
						for(String district : districtList){
							if(StringUtils.isNotBlank(district)){
								boolean districtFilter = isEnglish(province);
								if(districtFilter){
									continue;
								}
								WifiDevicePositionListService.getInstance().generateCity(city, district);
								System.out.println(province +"-"+ city + "-"+district);
							}
						}
					}
				}
			}
		}
		System.exit(0);;
	}
	
	public static boolean isEnglish(String str){
		String reg = "^[a-zA-Z]+$";
		return str.trim().substring(0, 1).matches(reg);
	}
}
