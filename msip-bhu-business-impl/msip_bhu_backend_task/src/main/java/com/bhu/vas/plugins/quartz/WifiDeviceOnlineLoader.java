package com.bhu.vas.plugins.quartz;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bhu.vas.api.dto.search.WifiDeviceIndexDTO;
import com.bhu.vas.api.helper.IndexDTOBuilder;
import com.bhu.vas.api.rpc.devices.model.WifiDevice;
import com.bhu.vas.business.bucache.redis.serviceimpl.devices.WifiDeviceHandsetPresentSortedSetService;
import com.bhu.vas.business.ds.device.service.WifiDeviceService;
import com.bhu.vas.business.search.service.device.WifiDeviceIndexService;
import com.smartwork.msip.cores.orm.iterator.EntityIterator;
import com.smartwork.msip.cores.orm.iterator.KeyBasedEntityBatchIterator;
import com.smartwork.msip.cores.orm.support.criteria.ModelCriteria;

public class WifiDeviceOnlineLoader {
	private static Logger logger = LoggerFactory.getLogger(WifiDeviceOnlineLoader.class);
	
	@Resource
	private WifiDeviceService wifiDeviceService;
	
	@Resource
	private WifiDeviceIndexService wifiDeviceIndexService;
	
	public void execute() {
		logger.info("WifiDeviceOnlineUser starting...");
		int bulk_success = 0;
		int bulk_fail = 0;
		int index_count = 0;
		
		ModelCriteria mc = new ModelCriteria();
		mc.createCriteria()
				.andColumnEqualTo("online", 1);
		//mc.setOrderByClause(" created_at ");
    	mc.setPageNumber(1);
    	mc.setPageSize(500);
		EntityIterator<String, WifiDevice> it = new KeyBasedEntityBatchIterator<String,WifiDevice>(String.class
				,WifiDevice.class, wifiDeviceService.getEntityDao(), mc);
		while(it.hasNext()){
			try{
				List<WifiDevice> entitys = it.next();
				List<WifiDeviceIndexDTO> indexDtos = new ArrayList<WifiDeviceIndexDTO>();
				WifiDeviceIndexDTO indexDto = null;
				for(WifiDevice device:entitys){
					String wifi_mac = device.getId();
					long count = WifiDeviceHandsetPresentSortedSetService.getInstance().presentNotOfflineSize(wifi_mac);
					indexDto = IndexDTOBuilder.builderWifiDeviceIndexDTO(device);
					indexDto.setOnline(WifiDeviceIndexDTO.Online_Status);
					indexDto.setCount((int)count);
					indexDtos.add(indexDto);
				}
				
				if(!indexDtos.isEmpty()){
					boolean bulk_result = wifiDeviceIndexService.createIndexComponents(indexDtos);
					if(bulk_result){
						bulk_success++;
					}else{
						bulk_fail++;
					}
					index_count = index_count + indexDtos.size();
				}
			}catch(Exception ex){
				ex.printStackTrace(System.out);
				logger.error(ex.getMessage(), ex);
			}
		}
		logger.info(String.format("WifiDeviceOnlineUser ended, total index [%s] bluk success [%s] fail [%s]", 
				index_count, bulk_success, bulk_fail));
	}
}
