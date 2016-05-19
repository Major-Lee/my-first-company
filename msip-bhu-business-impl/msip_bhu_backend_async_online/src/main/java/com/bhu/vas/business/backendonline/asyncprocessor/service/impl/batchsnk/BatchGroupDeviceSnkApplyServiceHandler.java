package com.bhu.vas.business.backendonline.asyncprocessor.service.impl.batchsnk;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import com.bhu.vas.api.helper.VapEnumType;
import com.bhu.vas.api.helper.VapEnumType.SharedNetworkType;
import com.bhu.vas.business.asyn.spring.model.async.group.BatchGroupDeviceSnkApplyDTO;
import com.bhu.vas.business.backendonline.asyncprocessor.service.iservice.IMsgHandlerService;
import com.bhu.vas.business.ds.device.service.WifiDeviceService;
import com.bhu.vas.business.search.model.WifiDeviceDocument;
import com.bhu.vas.business.search.service.WifiDeviceDataSearchService;
import com.smartwork.msip.cores.helper.JsonHelper;
import com.smartwork.msip.cores.orm.iterator.IteratorNotify;

@Service
public class BatchGroupDeviceSnkApplyServiceHandler implements IMsgHandlerService {
	private final Logger logger = LoggerFactory.getLogger(BatchGroupDeviceSnkApplyServiceHandler.class);
	
	@Resource
	private WifiDeviceService wifiDeviceService;
	
	@Resource
	private BatchSnkApplyService batchSnkApplyService;
	
	@Resource
	private WifiDeviceDataSearchService wifiDeviceDataSearchService;
	/*@Resource
	private SharedNetworksFacadeService sharedNetworksFacadeService;
	
	@Resource
	private WifiDeviceIndexIncrementService wifiDeviceIndexIncrementService;
	
	@Resource
	private WifiDeviceDataSearchService wifiDeviceDataSearchService;
	
	@Resource
	private DeviceCMDGenFacadeService deviceCMDGenFacadeService;
	
	@Resource
	private IDaemonRpcService daemonRpcService;*/

	@Override
	public void process(String message) {
		logger.info(String.format("process message[%s]", message));
		try{
			final BatchGroupDeviceSnkApplyDTO applyDto = JsonHelper.getDTO(message, BatchGroupDeviceSnkApplyDTO.class);
			final int userid = applyDto.getUid();
			//final String searchMessage = applyDto.getMessage();
			final SharedNetworkType sharedNetwork = VapEnumType.SharedNetworkType.fromKey(applyDto.getSnk_type());
			//final String template = applyDto.getTemplate();
			//final char dtoType = applyDto.getDtoType();
			wifiDeviceDataSearchService.iteratorAll(applyDto.getMessage(),100,new IteratorNotify<Page<WifiDeviceDocument>>() {
				@Override
				public void notifyComming(Page<WifiDeviceDocument> pages) {
					List<String> dmacs = new ArrayList<>();
					for (WifiDeviceDocument doc : pages) {
			    		String mac = doc.getD_mac();
			    		dmacs.add(mac);
			    	}
					if(!dmacs.isEmpty()){
						logger.info(String.format("prepare sharednetwork conf uid[%s] dtoType[%s] snk[%s] template[%s] dmacs[%s]",userid,applyDto.getDtoType(),sharedNetwork.getKey(),applyDto.getTemplate(), dmacs));
					}else{
						return;
					}
					batchSnkApplyService.apply(applyDto.getUid(), applyDto.getDtoType(), dmacs, sharedNetwork, applyDto.getTemplate());
				}
			});
		}finally{
		}
		logger.info(String.format("process message[%s] successful", message));
	}
}
