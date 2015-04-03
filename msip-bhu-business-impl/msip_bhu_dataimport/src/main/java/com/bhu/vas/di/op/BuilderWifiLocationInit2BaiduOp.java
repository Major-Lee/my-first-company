package com.bhu.vas.di.op;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.elasticsearch.ElasticsearchException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.util.StringUtils;

import com.bhu.vas.api.dto.baidumap.GeoPoiExtensionDTO;
import com.bhu.vas.api.dto.search.WifiDeviceIndexDTO;
import com.bhu.vas.api.helper.IndexDTOBuilder;
import com.bhu.vas.api.rpc.devices.model.WifiDevice;
import com.bhu.vas.business.bucache.redis.serviceimpl.devices.WifiDeviceHandsetPresentSortedSetService;
import com.bhu.vas.business.ds.device.service.WifiDeviceService;
import com.smartwork.msip.cores.helper.JsonHelper;
import com.smartwork.msip.cores.helper.geo.GeocodingHelper;
import com.smartwork.msip.cores.helper.geo.GeocodingPoiRespDTO;
import com.smartwork.msip.cores.orm.iterator.EntityIterator;
import com.smartwork.msip.cores.orm.iterator.KeyBasedEntityBatchIterator;
import com.smartwork.msip.cores.orm.support.criteria.ModelCriteria;
import com.smartwork.msip.es.exception.ESException;
/**
 * @author Edmond Lee
 *
 */
public class BuilderWifiLocationInit2BaiduOp {
	
	public static void main(String[] argv) throws ElasticsearchException, ESException, IOException, ParseException{
		ApplicationContext ctx = new FileSystemXmlApplicationContext("classpath*:com/bhu/vas/di/business/dataimport/dataImportCtx.xml");
		WifiDeviceService wifiDeviceService = (WifiDeviceService)ctx.getBean("wifiDeviceService");
		ModelCriteria mc = new ModelCriteria();
		mc.createCriteria().andColumnIsNotNull("lat").andColumnIsNotNull("lon");//.andColumnEqualTo("online", 1);
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
					Map<String, String> params = new HashMap<String, String>();
					params.put("title", device.getStreet());
					params.put("address", device.getFormatted_address());
					params.put("latitude", device.getLat());
					params.put("longitude", device.getLon());
					params.put("extension", JsonHelper.getJSONString(new GeoPoiExtensionDTO(device.getId(),device.isOnline()?1:0)));
					String bdid = device.getBdid();
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
				}catch(Exception ex){
					ex.printStackTrace(System.out);
				}
				
			}
			
		}
		System.out.println(String.format("执行结果：total[%s] create[%s] update[%s]", total,create,update));
	}
}
