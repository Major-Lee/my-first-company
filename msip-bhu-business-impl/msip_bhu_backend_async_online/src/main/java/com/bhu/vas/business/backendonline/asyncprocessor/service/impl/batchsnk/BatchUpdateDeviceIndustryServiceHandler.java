package com.bhu.vas.business.backendonline.asyncprocessor.service.impl.batchsnk;

import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.bhu.vas.api.rpc.devices.model.WifiDevice;
import com.bhu.vas.business.asyn.spring.model.async.device.BatchUpdateDeviceIndustryDTO;
import com.bhu.vas.business.asyn.spring.model.async.snk.BatchDeviceSnkApplyDTO;
import com.bhu.vas.business.backendonline.asyncprocessor.service.iservice.IMsgHandlerService;
import com.bhu.vas.business.ds.device.facade.SharedNetworksFacadeService;
import com.bhu.vas.business.ds.device.service.WifiDeviceService;
import com.bhu.vas.business.search.service.device.WifiDeviceDataSearchService;
import com.bhu.vas.business.search.service.increment.device.WifiDeviceIndexIncrementService;
import com.smartwork.msip.cores.helper.JsonHelper;

@Service
public class BatchUpdateDeviceIndustryServiceHandler implements IMsgHandlerService {
	private final Logger logger = LoggerFactory.getLogger(BatchUpdateDeviceIndustryServiceHandler.class);
	
	@Resource
	private SharedNetworksFacadeService sharedNetworksFacadeService;
	
	@Resource
	private WifiDeviceIndexIncrementService wifiDeviceIndexIncrementService;
	
	@Resource
	private WifiDeviceDataSearchService wifiDeviceDataSearchService;
	
	@Resource
	private WifiDeviceService wifiDeviceService;
	
	@Override
	public void process(String message) {
		logger.info(String.format("process message[%s]", message));
		try{
			BatchUpdateDeviceIndustryDTO applyDto = JsonHelper.getDTO(message, BatchUpdateDeviceIndustryDTO.class);
			List<WifiDevice> list = wifiDeviceService.findByIds(applyDto.getMacs());
			if(list != null && list.size() > 0){
				for(WifiDevice dev:list){
					dev.setIndustry(applyDto.getIndustry());
					dev.setMerchant_name(applyDto.getMerchant_name());
					wifiDeviceService.update(dev);
					wifiDeviceIndexIncrementService.industryUpdIncrement(dev.getId(), applyDto.getIndustry());
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
		}
		logger.info(String.format("process message[%s] successful", message));
	}


/*	@Override
	public void createDeviceGroupIndex(String message) {

		logger.info(String.format("WifiDeviceGroupServiceHandler createDeviceGroupIndex message[%s]", message))
		WifiDeviceGroupAsynCreateIndexDTO dto = JsonHelper.getDTO(message, WifiDeviceGroupAsynCreateIndexDTO.class);
		String wifiIdsStr = dto.getWifiIds();
		String type = dto.getType();
		Long gid = dto.getGid();

		List<String> wifiIds = new ArrayList<>();
		List<List<Long>> groupIdList = new ArrayList<List<Long>>();
		String[] wifiIdArray = wifiIdsStr.split(StringHelper.COMMA_STRING_GAP);
		for (String wifiId : wifiIdArray) {
			wifiIds.add(wifiId);

			List<Long> gids = wifiDeviceGroupRelationService.getDeviceGroupIds(wifiId);
			if (type.equals(WifiDeviceGroupAsynCreateIndexDTO.GROUP_INDEX_GRANT)) {
				gids.add(gid);
			} else if (type.equals(WifiDeviceGroupAsynCreateIndexDTO.GROUP_INDEX_UNGRANT)) {
				gids.remove(gid);
			}
			groupIdList.add(gids);
		}
		List<WifiDevice> wifiDeviceList = wifiDeviceService.findByIds(wifiIds);
		wifiDeviceIndexIncrementService.wifiDeviceIndexBlukIncrement(wifiDeviceList, groupIdList);

	}*/
}
