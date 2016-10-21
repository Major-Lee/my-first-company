package com.bhu.vas.business.backendonline.asyncprocessor.service.impl.batchdevice;


import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import com.bhu.vas.api.rpc.advertise.model.Advertise;
import com.bhu.vas.api.rpc.daemon.iservice.IDaemonRpcService;
import com.bhu.vas.business.asyn.spring.model.IDTO;
import com.bhu.vas.business.asyn.spring.model.async.device.BatchDeviceApplyAdvertiseDTO;
import com.bhu.vas.business.backendonline.asyncprocessor.service.iservice.IMsgHandlerService;
import com.bhu.vas.business.bucache.redis.serviceimpl.advertise.WifiDeviceAdvertiseListService;
import com.bhu.vas.business.search.model.WifiDeviceDocument;
import com.bhu.vas.business.search.service.WifiDeviceDataSearchService;
import com.smartwork.msip.cores.helper.JsonHelper;
import com.smartwork.msip.cores.orm.iterator.IteratorNotify;

@Service
public class BatchDeviceApplyAdvertseServiceHandler implements IMsgHandlerService{
	private final Logger logger = LoggerFactory.getLogger(BatchDeviceApplyAdvertseServiceHandler.class);
	
	@Resource
	private WifiDeviceDataSearchService wifiDeviceDataSearchService;
	
    @Resource
    private IDaemonRpcService daemonRpcService;
	
	@Override
	public void process(String message) {
		logger.info(String.format("process message[%s]", message));
		final BatchDeviceApplyAdvertiseDTO adDTO = JsonHelper.getDTO(message, BatchDeviceApplyAdvertiseDTO.class);
		for(Advertise ad : adDTO.getAdList()){
			final List<String> macList = new ArrayList<String>();
			wifiDeviceDataSearchService.iteratorWithPosition(ad.getProvince(), ad.getCity(), ad.getDistrict(), 200, new IteratorNotify<Page<WifiDeviceDocument>>() {
				
				@Override
				public void notifyComming(Page<WifiDeviceDocument> pages) {
					for (WifiDeviceDocument doc : pages) {
						macList.add(doc.getD_mac());
					}
				}
			});
			
			switch(adDTO.getDto_type()){
				case IDTO.ACT_ADD:
					WifiDeviceAdvertiseListService.getInstance().wifiDevicesAdApply(macList, JsonHelper.getJSONString(ad));
				  break;
				case IDTO.ACT_DELETE:
					WifiDeviceAdvertiseListService.getInstance().wifiDevicesAdInvalid(macList);
					break;
				default:
					break;
			}
		}
	}
}
