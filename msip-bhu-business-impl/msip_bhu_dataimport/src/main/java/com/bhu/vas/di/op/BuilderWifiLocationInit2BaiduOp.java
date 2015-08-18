package com.bhu.vas.di.op;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import com.bhu.vas.api.dto.baidumap.GeoPoiExtensionDTO;
import com.bhu.vas.api.rpc.devices.model.WifiDevice;
import com.bhu.vas.business.ds.device.facade.DeviceFacadeService;
import com.bhu.vas.business.ds.device.service.WifiDeviceService;
import com.smartwork.msip.cores.helper.JsonHelper;
import com.smartwork.msip.cores.helper.geo.GeocodingHelper;
import com.smartwork.msip.cores.helper.geo.GeocodingIpDTO;
import com.smartwork.msip.cores.helper.geo.GeocodingPoiRespDTO;
import com.smartwork.msip.cores.orm.iterator.EntityIterator;
import com.smartwork.msip.cores.orm.iterator.KeyBasedEntityBatchIterator;
import com.smartwork.msip.cores.orm.support.criteria.ModelCriteria;
/**
 * @author Edmond Lee
 *
 */
public class BuilderWifiLocationInit2BaiduOp {
	
	public static void main(String[] argv){
		ApplicationContext ctx = new FileSystemXmlApplicationContext("classpath*:com/bhu/vas/di/business/dataimport/dataImportCtx.xml");
		WifiDeviceService wifiDeviceService = (WifiDeviceService)ctx.getBean("wifiDeviceService");
		DeviceFacadeService deviceFacadeService = (DeviceFacadeService)ctx.getBean("deviceFacadeService");
		
		ModelCriteria mc = new ModelCriteria();
		mc.createCriteria().andSimpleCaulse(" 1=1 ");//.andColumnIsNotNull("lat").andColumnIsNotNull("lon");//.andColumnEqualTo("online", 1);
    	mc.setPageNumber(1);
    	mc.setPageSize(400);
		EntityIterator<String, WifiDevice> it = new KeyBasedEntityBatchIterator<String,WifiDevice>(String.class
				,WifiDevice.class, wifiDeviceService.getEntityDao(), mc);
		int total = 0;
		int create = 0;
		int update = 0;
		while(it.hasNext()){
			List<WifiDevice> entitys = it.next();
			for(WifiDevice device:entitys){
				try{
					String wan_ip = device.getWan_ip();
					String lat = device.getLat();
					String lon = device.getLon();
					String bdid = device.getBdid();
					if(StringUtils.isNotEmpty(lat) && StringUtils.isNotEmpty(lon) ){
						if(StringUtils.isNotEmpty(bdid)){
							total++;
							continue;
						}else{
							Map<String, String> params = new HashMap<String, String>();
							params.put("title",  StringUtils.isEmpty(device.getStreet())?device.getFormatted_address():device.getStreet());
							params.put("address", device.getFormatted_address());
							params.put("latitude", device.getLat());
							params.put("longitude", device.getLon());
							params.put("extension", JsonHelper.getJSONString(new GeoPoiExtensionDTO(device.getId(),device.isOnline()?1:0)));
							GeocodingPoiRespDTO response = null;
							if(StringUtils.isEmpty(bdid)){
								response = GeocodingHelper.geoPoiCreate(params);
								device.setBdid(String.valueOf(response.getId()));
								create++;
								total++;
							}else{
								params.put("id", bdid);
								response = GeocodingHelper.geoPoiUpdate(params);
								device.setBdid(String.valueOf(response.getId()));
								update++;
								total++;
							}
							wifiDeviceService.update(device);
						}
					}else{
						//通过外网ip判定地理位置
						if(StringUtils.isNotEmpty(wan_ip)){
							GeocodingIpDTO ipDto = GeocodingHelper.geoIpLocation(wan_ip);
							if(ipDto != null && ipDto.getContent() != null && ipDto.getContent().getPoint() != null ){
								lat = ipDto.getContent().getPoint().getY();
								lon = ipDto.getContent().getPoint().getX();
								device.setLat(lat);
								device.setLon(lon);
								//2:根据坐标提取地理位置详细信息
								boolean ret = deviceFacadeService.wifiDeiviceGeocoding(device);
								if(ret){
									try{
										Map<String, String> params = new HashMap<String, String>();
										params.put("title", StringUtils.isEmpty(device.getStreet())?device.getFormatted_address():device.getStreet());
										params.put("address", device.getFormatted_address());
										params.put("latitude", device.getLat());
										params.put("longitude", device.getLon());
										params.put("extension", JsonHelper.getJSONString(new GeoPoiExtensionDTO(device.getId(),device.isOnline()?1:0)));
										GeocodingPoiRespDTO response = null;
										if(StringUtils.isEmpty(bdid)){
											response = GeocodingHelper.geoPoiCreate(params);
											device.setBdid(String.valueOf(response.getId()));
											create++;
											total++;
										}else{
											params.put("id", bdid);
											response = GeocodingHelper.geoPoiUpdate(params);
											device.setBdid(String.valueOf(response.getId()));
											update++;
											total++;
										}
									}catch(Exception ex){
										ex.printStackTrace(System.out);
									}
								}
								device.setIpgen(true);
								wifiDeviceService.update(device);
							}
						}else{
							total++;
						}
					}
				}catch(Exception ex){
					ex.printStackTrace(System.out);
				}
			}
		}
		System.out.println(String.format("执行结果：total[%s] create[%s] update[%s]", total,create,update));
	}
}
