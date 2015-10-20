package com.bhu.vas.business.backendonline.asyncprocessor.service.indexincr;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.bhu.vas.api.rpc.devices.model.WifiDevice;
import com.bhu.vas.business.bucache.redis.serviceimpl.devices.WifiDeviceHandsetPresentSortedSetService;
import com.bhu.vas.business.ds.device.service.WifiDeviceGroupRelationService;
import com.bhu.vas.business.ds.device.service.WifiDeviceModuleService;
import com.bhu.vas.business.ds.device.service.WifiDeviceService;
import com.bhu.vas.business.search.model.WifiDeviceDocument;
import com.bhu.vas.business.search.model.WifiDeviceDocumentHelper;
import com.bhu.vas.business.search.service.WifiDeviceDataSearchService;
/**
 * wifi设备增量索引service
 * @author tangzichao
 *
 */
@Service
public class WifiDeviceIndexIncrementService {
	private final Logger logger = LoggerFactory.getLogger(WifiDeviceIndexIncrementService.class);
	
	//@Resource
	//private WifiDeviceIndexService wifiDeviceIndexService;
	@Resource
	private WifiDeviceDataSearchService wifiDeviceDataSearchService;
	
	@Resource
	private WifiDeviceService wifiDeviceService;
	
	@Resource
	private WifiDeviceModuleService wifiDeviceModuleService;
	
	@Resource
	private WifiDeviceGroupRelationService wifiDeviceGroupRelationService;
	
	/**
	 * 当设备上线的时候增量索引
	 * @param message
	 * @throws ESException 
	 * @throws IOException 
	 */
/*	public void wifiDeviceOnlineIndexIncrement(String wifiId) throws Exception{
		logger.info(String.format("wifiDeviceOnlineIndexIncrement wifiId[%s]", wifiId));
		
		WifiDevice entity = wifiDeviceService.getById(wifiId);
		if(entity != null){
			WifiDeviceIndexDTO indexDto = IndexDTOBuilder.builderWifiDeviceIndexDTO(entity);
			indexDto.setOnline(WifiDeviceIndexDTO.Online_Status);
			wifiDeviceIndexService.createIndexComponent(indexDto);
		}
		logger.info(String.format("wifiDeviceOnlineIndexIncrement wifiId[%s] successful", wifiId));
	}*/
	/**
	 * 当获取到wifi设备的坐标位置时候增量索引
	 * @param wifiId
	 * @throws Exception
	 */
/*	public void wifiDeviceLocationIndexIncrement(WifiDevice entity) throws Exception{
		logger.info(String.format("wifiDeviceLocationIndexIncrement wifiId[%s]", entity.getId()));

		List<Integer> groupids = wifiDeviceGroupRelationService.getDeviceGroupIds(entity.getId());
		WifiDeviceIndexDTO indexDto = IndexDTOBuilder.builderWifiDeviceIndexDTO(entity, groupids);
		//Long count = WifiDeviceHandsetPresentSortedSetService.getInstance().presentNotOfflineSize(entity.getId());
		Long count = WifiDeviceHandsetPresentSortedSetService.getInstance().presentOnlineSize(entity.getId());
		if(count != null){
			indexDto.setCount(count.intValue());
		}
		indexDto.setOnline(WifiDeviceIndexDTO.Online_Status);
		wifiDeviceIndexService.createIndexComponent(indexDto);
		
		logger.info(String.format("wifiDeviceLocationIndexIncrement wifiId[%s] successful", entity.getId()));
	}*/
	
	/**
	 * cm上线以后的设备同步信息
	 * @param entitys
	 * @throws Exception
	 */
	public void cmupWithWifiDeviceOnlinesIndexIncrement(List<WifiDevice> entitys) throws Exception{
		logger.info(String.format("wifiDeviceOnlinesIndexIncrement size[%s]", entitys.size()));
		List<WifiDeviceDocument> docs = new ArrayList<>();
		WifiDeviceDocument doc = null;
		//List<WifiDeviceIndexDTO> indexDtos = new ArrayList<WifiDeviceIndexDTO>();
		for(WifiDevice entity : entitys){
			List<Long> groupids = wifiDeviceGroupRelationService.getDeviceGroupIds(entity.getId());
			/*WifiDeviceIndexDTO indexDto = IndexDTOBuilder.builderWifiDeviceIndexDTO(entity, groupids);
			indexDto.setCount(WifiDeviceHandsetPresentSortedSetService.getInstance().presentOnlineSize(entity.getId()).intValue());
			indexDto.setOnline(WifiDeviceIndexDTO.Online_Status);
			indexDtos.add(indexDto);*/
			doc = WifiDeviceDocumentHelper.fromWifiDevice(entity,wifiDeviceModuleService.getById(entity.getId()), groupids);
			doc.setOnline(Boolean.TRUE);
			doc.setCount(WifiDeviceHandsetPresentSortedSetService.getInstance().presentOnlineSize(entity.getId()).intValue());
			docs.add(doc);
			//WifiDeviceIndexDTO indexDto = IndexDTOBuilder.builderWifiDeviceIndexDTO(entity, groupids);
			//indexDto.setOnline(WifiDeviceIndexDTO.Online_Status);
			//indexDtos.add(indexDto);
		}
		//wifiDeviceIndexService.createIndexComponents(indexDtos);
		wifiDeviceDataSearchService.getRepository().save(docs);
		logger.info(String.format("wifiDeviceOnlinesIndexIncrement size[%s] successful", entitys.size()));
	}
	
	/**
	 * wifi设备下线
	 * @param wifiId
	 * @throws Exception
	 */
/*	public void wifiDeviceOfflineIndexIncrement(String wifiId) throws Exception{
		logger.info(String.format("wifiDeviceOfflineIndexIncrement wifiId[%s]", wifiId));
		
		WifiDevice entity = wifiDeviceService.getById(wifiId);
		if(entity != null){
			WifiDeviceIndexDTO indexDto = IndexDTOBuilder.builderWifiDeviceIndexDTO(entity);
			indexDto.setCount(0);
			indexDto.setOnline(WifiDeviceIndexDTO.offline_Status);
			wifiDeviceIndexService.createIndexComponent(indexDto);
		}
		logger.info(String.format("wifiDeviceOfflineIndexIncrement wifiId[%s] successful", wifiId));
	}*/
	
	/**
	 * 设备增量索引操作
	 * @param wifiId
	 * @param entity
	 */
	public void wifiDeviceIndexIncrement(WifiDevice entity) {
		try{
			List<Long> groupids = wifiDeviceGroupRelationService.getDeviceGroupIds(entity.getId());
			wifiDeviceIndexIncrement(entity, groupids);
		}catch(Exception ex){
			logger.error(String.format("wifiDeviceIndexIncrement wifiId[%s] online[%s] exception", 
					entity.getId(), entity.isOnline()), ex);
		}
	}
	
	
	public void wifiDeviceIndexIncrement(WifiDevice entity, List<Long> groupids) {
		logger.info(String.format("wifiDeviceIndexIncrement wifiId[%s] online[%s]", entity.getId(), entity.isOnline()));
		try{
/*			WifiDeviceIndexDTO indexDto = IndexDTOBuilder.builderWifiDeviceIndexDTO(entity, groupids);
			if(entity.isOnline()){
				indexDto.setCount(WifiDeviceHandsetPresentSortedSetService.getInstance().presentOnlineSize(entity.getId()).intValue());
			}
			wifiDeviceIndexService.createIndexComponent(indexDto);*/
			WifiDeviceDocument doc = WifiDeviceDocumentHelper.fromWifiDevice(entity,wifiDeviceModuleService.getById(entity.getId()), groupids);
			if(entity.isOnline()){
				doc.setCount(WifiDeviceHandsetPresentSortedSetService.getInstance().presentOnlineSize(entity.getId()).intValue());
			}
			wifiDeviceDataSearchService.getRepository().save(doc);
			//WifiDeviceIndexDTO indexDto = IndexDTOBuilder.builderWifiDeviceIndexDTO(entity, groupids);
			//wifiDeviceIndexService.createIndexComponent(indexDto);
		}catch(Exception ex){
			//ex.printStackTrace();
			logger.error(String.format("wifiDeviceIndexIncrement wifiId[%s] online[%s] exception", 
					entity.getId(), entity.isOnline()), ex);
		}
		
		logger.info(String.format("wifiDeviceIndexIncrement wifiId[%s] online[%s] successful", entity.getId(), entity.isOnline()));
	}
	
	public void wifiDeviceIndexBlukIncrement(List<WifiDevice> entitys, List<List<Long>> groupids_list){
		if(entitys == null || entitys.isEmpty()) return;
		if(groupids_list == null || groupids_list.isEmpty()) return;
		
		logger.info(String.format("wifiDeviceIndexBlukIncrement wifiId[%s] online[%s]", entitys.size(), groupids_list.size()));
		List<WifiDeviceDocument> docs = new ArrayList<>();
		WifiDeviceDocument doc = null;
		try{
			//List<WifiDeviceIndexDTO> indexDtos = new ArrayList<WifiDeviceIndexDTO>();
			int cursor = 0;
			for(WifiDevice entity : entitys){
				doc = WifiDeviceDocumentHelper.fromWifiDevice(entity,wifiDeviceModuleService.getById(entity.getId()), groupids_list.get(cursor));
				docs.add(doc);
				cursor++;
			}
			//wifiDeviceIndexService.createIndexComponents(indexDtos);
			wifiDeviceDataSearchService.getRepository().save(docs);
			/*List<WifiDeviceIndexDTO> indexDtos = new ArrayList<WifiDeviceIndexDTO>();
			int cursor = 0;
			for(WifiDevice entity : entitys){
				WifiDeviceIndexDTO indexDto = IndexDTOBuilder.builderWifiDeviceIndexDTO(entity, groupids_list.get(cursor));
				if(entity.isOnline()){
					indexDto.setCount(WifiDeviceHandsetPresentSortedSetService.getInstance().presentOnlineSize(entity.getId()).intValue());
				}
				indexDtos.add(indexDto);
				cursor++;
			}
			wifiDeviceIndexService.createIndexComponents(indexDtos);*/
		}catch(Exception ex){
			//ex.printStackTrace();
			logger.error(String.format("wifiDeviceIndexBlukIncrement wifiId[%s] online[%s] exception", 
					entitys.size(), groupids_list.size()), ex);
		}
		
		logger.info(String.format("wifiDeviceIndexBlukIncrement wifiId[%s] online[%s] successful", entitys.size(), groupids_list.size()));

	}
}
