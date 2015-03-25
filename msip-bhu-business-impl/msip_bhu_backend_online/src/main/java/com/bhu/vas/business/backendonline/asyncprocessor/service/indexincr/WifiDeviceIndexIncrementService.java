package com.bhu.vas.business.backendonline.asyncprocessor.service.indexincr;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.bhu.vas.api.dto.search.WifiDeviceIndexDTO;
import com.bhu.vas.api.helper.IndexDTOBuilder;
import com.bhu.vas.api.rpc.devices.model.WifiDevice;
import com.bhu.vas.business.bucache.redis.serviceimpl.devices.WifiDeviceHandsetPresentSortedSetService;
import com.bhu.vas.business.ds.device.service.WifiDeviceService;
import com.bhu.vas.business.search.service.device.WifiDeviceIndexService;
import com.smartwork.msip.es.exception.ESException;
/**
 * wifi设备增量索引service
 * @author tangzichao
 *
 */
@Service
public class WifiDeviceIndexIncrementService {
	private final Logger logger = LoggerFactory.getLogger(WifiDeviceIndexIncrementService.class);
	
	@Resource
	private WifiDeviceIndexService wifiDeviceIndexService;
	
	@Resource
	private WifiDeviceService wifiDeviceService;
	
	/**
	 * 当设备上线的时候增量索引
	 * @param message
	 * @throws ESException 
	 * @throws IOException 
	 */
	public void wifiDeviceOnlineIndexIncrement(String wifiId) throws Exception{
		logger.info(String.format("wifiDeviceOnlineIndexIncrement wifiId[%s]", wifiId));
		
		WifiDevice entity = wifiDeviceService.getById(wifiId);
		if(entity != null){
			WifiDeviceIndexDTO indexDto = IndexDTOBuilder.builderWifiDeviceIndexDTO(entity);
			indexDto.setOnline(WifiDeviceIndexDTO.Online_Status);
			wifiDeviceIndexService.createIndexComponent(indexDto);
		}
		logger.info(String.format("wifiDeviceOnlineIndexIncrement wifiId[%s] successful", wifiId));
	}
	/**
	 * 当获取到wifi设备的坐标位置时候增量索引
	 * @param wifiId
	 * @throws Exception
	 */
	public void wifiDeviceLocationIndexIncrement(WifiDevice entity) throws Exception{
		logger.info(String.format("wifiDeviceLocationIndexIncrement wifiId[%s]", entity.getId()));

		WifiDeviceIndexDTO indexDto = IndexDTOBuilder.builderWifiDeviceIndexDTO(entity);
		Long count = WifiDeviceHandsetPresentSortedSetService.getInstance().presentNotOfflineSize(entity.getId());
		if(count != null){
			indexDto.setCount(count.intValue());
		}
		indexDto.setOnline(WifiDeviceIndexDTO.Online_Status);
		wifiDeviceIndexService.createIndexComponent(indexDto);
		
		logger.info(String.format("wifiDeviceLocationIndexIncrement wifiId[%s] successful", entity.getId()));
	}
	
	/**
	 * cm上线以后的设备同步信息
	 * @param entitys
	 * @throws Exception
	 */
	public void cmupWithWifiDeviceOnlinesIndexIncrement(List<WifiDevice> entitys) throws Exception{
		logger.info(String.format("wifiDeviceOnlinesIndexIncrement size[%s]", entitys.size()));
		
		List<WifiDeviceIndexDTO> indexDtos = new ArrayList<WifiDeviceIndexDTO>();
		for(WifiDevice entity : entitys){
			WifiDeviceIndexDTO indexDto = IndexDTOBuilder.builderWifiDeviceIndexDTO(entity);
			indexDto.setOnline(WifiDeviceIndexDTO.Online_Status);
			indexDtos.add(indexDto);
		}
		wifiDeviceIndexService.createIndexComponents(indexDtos);
		
		logger.info(String.format("wifiDeviceOnlinesIndexIncrement size[%s] successful", entitys.size()));
	}
	
	/**
	 * wifi设备下线
	 * @param wifiId
	 * @throws Exception
	 */
	public void wifiDeviceOfflineIndexIncrement(String wifiId) throws Exception{
		logger.info(String.format("wifiDeviceOfflineIndexIncrement wifiId[%s]", wifiId));
		
		WifiDevice entity = wifiDeviceService.getById(wifiId);
		if(entity != null){
			WifiDeviceIndexDTO indexDto = IndexDTOBuilder.builderWifiDeviceIndexDTO(entity);
			indexDto.setCount(0);
			indexDto.setOnline(WifiDeviceIndexDTO.offline_Status);
			wifiDeviceIndexService.createIndexComponent(indexDto);
		}
		logger.info(String.format("wifiDeviceOfflineIndexIncrement wifiId[%s] successful", wifiId));
	}
}
