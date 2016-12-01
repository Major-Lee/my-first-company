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
//import com.bhu.vas.api.dto.DistributorType;
//import com.bhu.vas.api.dto.UserType;
//import com.bhu.vas.api.helper.VapEnumType;
//import com.bhu.vas.api.helper.VapEnumType.SharedNetworkType;
//import com.bhu.vas.api.helper.WifiDeviceDocumentEnumType.SnkTurnStateEnum;
//import com.bhu.vas.api.rpc.user.model.User;
//import com.bhu.vas.business.asyn.spring.model.IDTO;
//import com.bhu.vas.business.asyn.spring.model.async.snk.BatchModifyDeviceSnkDTO;
//import com.bhu.vas.business.backendonline.asyncprocessor.service.iservice.IMsgHandlerService;
//import com.bhu.vas.business.bucache.redis.serviceimpl.commdity.RewardOrderAmountHashService;
//import com.bhu.vas.business.ds.device.service.WifiDeviceService;
//import com.bhu.vas.business.ds.user.service.UserService;
//import com.bhu.vas.business.search.model.WifiDeviceDocument;
//import com.bhu.vas.business.search.service.WifiDeviceDataSearchService;
//import com.bhu.vas.business.search.service.increment.WifiDeviceIndexIncrementService;
//import com.smartwork.msip.cores.helper.JsonHelper;
//import com.smartwork.msip.cores.orm.iterator.IteratorNotify;
//
//@Service
//public class BatchModifyDeviceSnkServiceHandler implements IMsgHandlerService {
//	private final Logger logger = LoggerFactory.getLogger(BatchModifyDeviceSnkServiceHandler.class);
//	
//	@Resource
//	private WifiDeviceService wifiDeviceService;
//	
//	@Resource
//	private BatchSnkApplyService batchSnkApplyService;
//	
//	//@Resource
//	//private SharedNetworksFacadeService sharedNetworksFacadeService;
//	
//	@Resource
//	private WifiDeviceIndexIncrementService wifiDeviceIndexIncrementService;
//	
//	@Resource
//	private WifiDeviceDataSearchService wifiDeviceDataSearchService;
//	
//	@Resource
//	private UserService userService;
//	
///*	@Resource
//	private DeviceCMDGenFacadeService deviceCMDGenFacadeService;
//	
//	@Resource
//	private IDaemonRpcService daemonRpcService;*/
//
//	@Override
//	public void process(String message) {
//		logger.info(String.format("process message[%s]", message));
//		try{
//			BatchModifyDeviceSnkDTO dto = JsonHelper.getDTO(message, BatchModifyDeviceSnkDTO.class);
//			final int userid = dto.getUid();
//			final List<String> dmacs = dto.getMacs()== null?new ArrayList<String>():dto.getMacs();
//			
//			if(userid <= 0 && dmacs.isEmpty()){
//				logger.info("BatchModifyDeviceSnkServiceHandler 条件不符");
//				return;
//			}
//			
//			final User user = userService.getById(userid);
//			if(user == null){
//				logger.info(String.format("用户[%s]不存在", userid));
//				return;
//			}
//			batchSnkApplyService.modify(dto.getUid(), dto.getSsid(), dto.getRate(), dmacs);
//		}finally{
//		}
//		logger.info(String.format("process message[%s] successful", message));
//	}
//
//
///*	@Override
//	public void createDeviceGroupIndex(String message) {
//
//		logger.info(String.format("WifiDeviceGroupServiceHandler createDeviceGroupIndex message[%s]", message))
//		WifiDeviceGroupAsynCreateIndexDTO dto = JsonHelper.getDTO(message, WifiDeviceGroupAsynCreateIndexDTO.class);
//		String wifiIdsStr = dto.getWifiIds();
//		String type = dto.getType();
//		Long gid = dto.getGid();
//
//		List<String> wifiIds = new ArrayList<>();
//		List<List<Long>> groupIdList = new ArrayList<List<Long>>();
//		String[] wifiIdArray = wifiIdsStr.split(StringHelper.COMMA_STRING_GAP);
//		for (String wifiId : wifiIdArray) {
//			wifiIds.add(wifiId);
//
//			List<Long> gids = wifiDeviceGroupRelationService.getDeviceGroupIds(wifiId);
//			if (type.equals(WifiDeviceGroupAsynCreateIndexDTO.GROUP_INDEX_GRANT)) {
//				gids.add(gid);
//			} else if (type.equals(WifiDeviceGroupAsynCreateIndexDTO.GROUP_INDEX_UNGRANT)) {
//				gids.remove(gid);
//			}
//			groupIdList.add(gids);
//		}
//		List<WifiDevice> wifiDeviceList = wifiDeviceService.findByIds(wifiIds);
//		wifiDeviceIndexIncrementService.wifiDeviceIndexBlukIncrement(wifiDeviceList, groupIdList);
//
//	}*/
//}
