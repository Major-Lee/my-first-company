//package com.bhu.vas.business.backendonline.asyncprocessor.service.impl.batchsnk;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import javax.annotation.Resource;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.data.domain.Page;
//import org.springframework.stereotype.Service;
//
//import com.bhu.vas.api.helper.SharedNetworkChangeType;
//import com.bhu.vas.api.helper.VapEnumType;
//import com.bhu.vas.api.helper.VapEnumType.SharedNetworkType;
//import com.bhu.vas.business.asyn.spring.model.IDTO;
//import com.bhu.vas.business.asyn.spring.model.async.group.BatchGroupModifyDeviceSnkDTO;
//import com.bhu.vas.business.backendonline.asyncprocessor.service.iservice.IMsgHandlerService;
//import com.bhu.vas.business.ds.device.service.WifiDeviceService;
//import com.bhu.vas.business.search.model.WifiDeviceDocument;
//import com.bhu.vas.business.search.service.WifiDeviceDataSearchService;
//import com.smartwork.msip.cores.helper.JsonHelper;
//import com.smartwork.msip.cores.orm.iterator.IteratorNotify;
//
//@Service
//public class BatchGroupModifyDeviceSnkServiceHandler implements IMsgHandlerService {
//	private final Logger logger = LoggerFactory.getLogger(BatchGroupModifyDeviceSnkServiceHandler.class);
//	
//	@Resource
//	private WifiDeviceService wifiDeviceService;
//	
//	@Resource
//	private BatchSnkApplyService batchSnkApplyService;
//	
//	@Resource
//	private WifiDeviceDataSearchService wifiDeviceDataSearchService;
//	/*@Resource
//	private SharedNetworksFacadeService sharedNetworksFacadeService;
//	
//	@Resource
//	private WifiDeviceIndexIncrementService wifiDeviceIndexIncrementService;
//	
//	@Resource
//	private WifiDeviceDataSearchService wifiDeviceDataSearchService;
//	
//	@Resource
//	private DeviceCMDGenFacadeService deviceCMDGenFacadeService;
//	
//	@Resource
//	private IDaemonRpcService daemonRpcService;*/
//
//	@Override
//	public void process(String message) {
//		logger.info(String.format("process message[%s]", message));
//		try{
//			final BatchGroupModifyDeviceSnkDTO applyDto = JsonHelper.getDTO(message, BatchGroupModifyDeviceSnkDTO.class);
//			wifiDeviceDataSearchService.iteratorAll(applyDto.getMessage(),100,new IteratorNotify<Page<WifiDeviceDocument>>() {
//				@Override
//				public void notifyComming(Page<WifiDeviceDocument> pages) {
//					List<String> dmacs = new ArrayList<>();
//					for (WifiDeviceDocument doc : pages) {
//						String mac = doc.getD_mac();
////						int owner = -1;
////						try{
////							owner = Integer.parseInt(doc.getD_snk_owner());
////						}catch(Exception e){
////							logger.info(String.format("parse [%s][%s] to int error", doc.getId(), doc.getD_snk_owner()));
////						}
////						
////						if(owner != applyDto.getUid()) //非属于自己的设备portal，不允许修改
////			    			continue;
//			    		dmacs.add(mac);
//			    	}
//					if(!dmacs.isEmpty()){
//						logger.info(String.format("prepare group sharednetwork modify conf uid[%s] ssid[%s] rate[%s] dmacs[%s]",applyDto.getUid(),applyDto.getSsid(), applyDto.getRate(), dmacs));
//					}else{
//						return;
//					}
//					batchSnkApplyService.modify(applyDto.getUid(), applyDto.getSsid(), applyDto.getRate(), dmacs);
//				}
//			});
//		}finally{
//		}
//		logger.info(String.format("process message[%s] successful", message));
//	}
//}
