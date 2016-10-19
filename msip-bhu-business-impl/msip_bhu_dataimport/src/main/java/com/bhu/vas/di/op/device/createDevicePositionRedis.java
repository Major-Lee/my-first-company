package com.bhu.vas.di.op.device;

import java.util.List;
import java.util.Map;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import com.bhu.vas.business.bucache.redis.serviceimpl.devices.WifiDevicePositionListService;
import com.bhu.vas.business.ds.device.service.WifiDeviceService;

public class createDevicePositionRedis {
	
	private static WifiDeviceService wifiDeviceService;
	
	public static void initialize(){
		ApplicationContext ctx = new FileSystemXmlApplicationContext("classpath*:com/bhu/vas/di/business/dataimport/dataImportCtx.xml");
		wifiDeviceService = (WifiDeviceService)ctx.getBean("wifiDeviceService");
	}
	
	public static void main(String[] args) {
		initialize();
		findDevicesPosition();
	}
	
	public static void findDevicesPosition(){
		
		List<Map<String, Object>> provinceList =  wifiDeviceService.selectByField("province", true, true, null, null);
		for(Map<String,Object> provinceMap :provinceList){
			WifiDevicePositionListService.getInstance().generateAllProvince((String)provinceMap.get("province"));
			System.out.println((String)provinceMap.get("province"));
			
			List<Map<String, Object>> cityList =  wifiDeviceService.selectByField("city", true, true, "province", (String)provinceMap.get("province"));
			for(Map<String,Object> cityMap :cityList){
				WifiDevicePositionListService.getInstance().generateProvince((String)provinceMap.get("province"), (String)cityMap.get("city"));
				System.out.println((String)provinceMap.get("province") +"-"+(String)cityMap.get("city"));
				
				List<Map<String, Object>> districtList =  wifiDeviceService.selectByField("district", true, true, "city", (String)cityMap.get("city"));
				for(Map<String,Object> districtMap : districtList){
					WifiDevicePositionListService.getInstance().generateCity((String)cityMap.get("city"), (String)districtMap.get("district"));
					System.out.println((String)provinceMap.get("province") +"-"+ (String)cityMap.get("city") + "-"+(String)districtMap.get("district"));
				}
			}
		}
	}
}
