package com.bhu.vas.di.op.repair;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.bhu.vas.api.rpc.devices.model.WifiDevice;
import com.bhu.vas.api.rpc.devices.model.WifiDeviceForGps;
import com.bhu.vas.business.ds.device.service.WifiDeviceForGpsService;
import com.bhu.vas.business.ds.device.service.WifiDeviceService;
import com.smartwork.msip.cores.helper.geo.GPSUtil;
import com.smartwork.msip.cores.helper.geo.GeocodingAddressDTO;
import com.smartwork.msip.cores.helper.geo.GeocodingDTO;
import com.smartwork.msip.cores.helper.geo.GeocodingHelper;
import com.smartwork.msip.cores.helper.geo.GeocodingResultDTO;
import com.smartwork.msip.cores.orm.iterator.EntityIterator;
import com.smartwork.msip.cores.orm.iterator.KeyBasedEntityBatchIterator;
import com.smartwork.msip.cores.orm.support.criteria.ModelCriteria;

/**
 * 设备定位数据从gps坐标系转换为高德坐标系
 * t_wifi_devices_for_gps表
 * @author Yetao
 *
 */
public class WifiDeviceGpsToGaodeOp {
	
	
	public boolean wifiDeiviceGeocoding(WifiDevice entity){
		if(entity == null) return false;
		if(StringUtils.isEmpty(entity.getLat()) || StringUtils.isEmpty(entity.getLon())) return false;
		
		return false;
	}

	
	public static void main(String[] argv){
		long t0 = System.currentTimeMillis();
		String[] CONFIG = {"/com/bhu/vas/di/business/dataimport/dataImportCtx.xml"};
		final ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext(CONFIG, WifiDeviceGpsToGaodeOp.class);
		ctx.start();
		WifiDeviceService wifiDeviceService = (WifiDeviceService)ctx.getBean("wifiDeviceService");
		WifiDeviceForGpsService wifiDeviceForGpsService = (WifiDeviceForGpsService)ctx.getBean("wifiDeviceForGpsService");


		for(int i = 0; i < 1;i ++){
			ModelCriteria mc = new ModelCriteria();
			mc.createCriteria().andSimpleCaulse(" 1=1 ");
			mc.setPageNumber(1);
			mc.setPageSize(200);
	    	EntityIterator<String, WifiDeviceForGps> itit = new KeyBasedEntityBatchIterator<String, WifiDeviceForGps>(String.class, WifiDeviceForGps.class,
	    			wifiDeviceForGpsService.getEntityDao(), mc);
			while(itit.hasNext()){
				List<WifiDeviceForGps> list = itit.next();
				for(WifiDeviceForGps device:list){
					double[] gpsgd = GPSUtil.gps84_To_Gcj02(Double.valueOf(device.getLat()), Double.valueOf(device.getLon()));
					double[] gpsbd = GPSUtil.gps84_To_bd09(Double.valueOf(device.getLat()), Double.valueOf(device.getLon()));

					
					try{
						System.out.println("Geocoding  for :" + device.getId());
						//2:根据坐标提取地理位置详细信息
						GeocodingDTO geocodingDto = GeocodingHelper.geocodingGet(String.valueOf(gpsbd[0]), 
								String.valueOf(gpsbd[1]));
						if(geocodingDto == null){
							System.out.println("GeocodingDTO is null");
							continue;
						}
						if(geocodingDto.getStatus() != GeocodingDTO.Success_Status){
							System.out.println(String.format("GeocodingHelper fail  status[%s]",
									geocodingDto != null ? geocodingDto.getStatus() : ""));
							continue;
						}
						
						
						GeocodingResultDTO resultDto = geocodingDto.getResult();
						if(resultDto == null){
							System.out.println("resultDto is null");
							continue;
						}
						GeocodingAddressDTO addressDto = geocodingDto.getResult().getAddressComponent();
						WifiDevice dev = wifiDeviceService.getById(device.getId());
						dev.setLat(String.valueOf(gpsgd[0]));
						dev.setLon(String.valueOf(gpsgd[1]));
						if(addressDto != null){
							dev.setCountry(addressDto.getCountry());
							dev.setProvince(addressDto.getProvince());
							dev.setCity(addressDto.getCity());
							dev.setDistrict(addressDto.getDistrict());
							dev.setStreet(addressDto.getStreet());
							
							StringBuffer formatted_address_buffer = new StringBuffer();
							if(StringUtils.isNotEmpty(addressDto.getCountry())){
								formatted_address_buffer.append(addressDto.getCountry());
							}
							if(StringUtils.isNotEmpty(resultDto.getFormatted_address())){
								formatted_address_buffer.append(resultDto.getFormatted_address());
							}
							dev.setFormatted_address(formatted_address_buffer.toString());
						} else {
//							dev.setCountry(null);
//							dev.setProvince(null);
//							dev.setCity(null);
//							dev.setDistrict(null);
//							dev.setStreet(null);
//							dev.setFormatted_address(null);
						}
						wifiDeviceService.update(dev);
						wifiDeviceForGpsService.delete(device);
					}catch(Exception ex){
						ex.printStackTrace(System.out);
						System.out.println(String.format("GeocodingHelper exception exmsg[%s]",
							ex.getMessage()));
					}
				}
			}
		}

		ctx.stop();
		ctx.close();
		System.exit(1);
	}
}
