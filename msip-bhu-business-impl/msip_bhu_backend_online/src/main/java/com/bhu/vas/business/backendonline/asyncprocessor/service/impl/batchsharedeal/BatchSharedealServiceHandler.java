package com.bhu.vas.business.backendonline.asyncprocessor.service.impl.batchsharedeal;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import com.bhu.vas.business.asyn.spring.model.BatchSharedealModifyDTO;
import com.bhu.vas.business.backendonline.asyncprocessor.service.iservice.IMsgHandlerService;
import com.bhu.vas.business.ds.charging.facade.ChargingFacadeService;
import com.bhu.vas.business.ds.device.service.WifiDeviceService;
import com.bhu.vas.business.ds.user.facade.UserDeviceFacadeService;
import com.bhu.vas.business.search.model.WifiDeviceDocument;
import com.bhu.vas.business.search.service.WifiDeviceDataSearchService;
import com.smartwork.msip.cores.helper.JsonHelper;
import com.smartwork.msip.cores.orm.iterator.IteratorNotify;

@Service
public class BatchSharedealServiceHandler implements IMsgHandlerService {
	private final Logger logger = LoggerFactory.getLogger(BatchSharedealServiceHandler.class);
	
	@Resource
	private WifiDeviceService wifiDeviceService;

	@Resource
	private UserDeviceFacadeService userDeviceFacadeService;
	
	@Resource
	private ChargingFacadeService chargingFacadeService;
	
	//@Resource
	//private BackendBusinessService backendBusinessService;
	
	@Resource
	private WifiDeviceDataSearchService wifiDeviceDataSearchService;
	
	
	/*@Resource
	private IDaemonRpcService daemonRpcService;*/

	@Override
	public void process(String message) {
		logger.info(String.format("process message[%s]", message));
		final BatchSharedealModifyDTO sharedealDTO = JsonHelper.getDTO(message, BatchSharedealModifyDTO.class);
		wifiDeviceDataSearchService.iteratorAll(sharedealDTO.getMessage(),100,
				new IteratorNotify<Page<WifiDeviceDocument>>() {
					@Override
					public void notifyComming(Page<WifiDeviceDocument> pages) {
						List<String> macList = new ArrayList<String>();
						for (WifiDeviceDocument doc : pages) {
							macList.add(doc.getD_mac());
						}
						logger.info(String.format("pagesize:%s pages:%s",100,macList));
						for(String dmac:macList){
							chargingFacadeService.doWifiDeviceSharedealConfigsUpdate(null,null, dmac, 
									sharedealDTO.getOwner_percent(),
									sharedealDTO.getRcm(), sharedealDTO.getRcp(), sharedealDTO.getAit(), sharedealDTO.isCbto(), false);
						}
					}
				});
		logger.info(String.format("process message[%s] successful", message));
	}
}
