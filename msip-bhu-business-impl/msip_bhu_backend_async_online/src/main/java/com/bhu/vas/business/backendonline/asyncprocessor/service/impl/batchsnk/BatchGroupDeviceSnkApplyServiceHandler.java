package com.bhu.vas.business.backendonline.asyncprocessor.service.impl.batchsnk;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import com.bhu.vas.api.dto.DistributorType;
import com.bhu.vas.api.dto.UserType;
import com.bhu.vas.api.helper.SharedNetworkChangeType;
import com.bhu.vas.api.helper.VapEnumType;
import com.bhu.vas.api.helper.VapEnumType.SharedNetworkType;
import com.bhu.vas.business.asyn.spring.model.IDTO;
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
						//TODO:需要判定设备本身是否能关闭访客网络策略
						if((IDTO.ACT_ADD == applyDto.getDtoType() || IDTO.ACT_UPDATE == applyDto.getDtoType()) 
								&& sharedNetwork ==SharedNetworkType.SafeSecure){//此种情况为开启SafeSecure网络 ，可以不做任何验证
							;
						}else{
							String allowturnoff = doc.getD_snk_allowturnoff();
							if("0".equals(allowturnoff)){
								if((IDTO.ACT_ADD == applyDto.getDtoType() || IDTO.ACT_UPDATE == applyDto.getDtoType()) 
										&& sharedNetwork ==SharedNetworkType.Uplink){//不允许开启Uplink网络
									logger.info(String.format("filter mac[%s] sharednetwork[%s] allowturnoff[%s] dtoType[%s]", mac,applyDto.getSnk_type(),allowturnoff,applyDto.getDtoType()));
									continue;
								}
								if((IDTO.ACT_DELETE == applyDto.getDtoType()) && sharedNetwork ==SharedNetworkType.SafeSecure){//不允许关闭SafeSecure网络
									logger.info(String.format("filter mac[%s] sharednetwork[%s] allowturnoff[%s] dtoType[%s]", mac,applyDto.getSnk_type(),allowturnoff,applyDto.getDtoType()));
									continue;
									//throw new BusinessI18nCodeException(ResponseErrorCode.USER_DEVICE_SHAREDNETWORK_SAFESECURE_CANNOT_BETURNOFF);
								}
							}
						}
						
						//目前分组只在ucloud中存在。所以此处不允许修改分组中属于运营商的设备
			    		if(DistributorType.City.getType().equals(doc.getD_distributor_type())) //非城市运营商不能修改城市运营商的设备
			    			continue;
			    		dmacs.add(mac);
			    	}
					if(!dmacs.isEmpty()){
						logger.info(String.format("prepare sharednetwork conf uid[%s] dtoType[%s] snk[%s] template[%s] dmacs[%s]",userid,applyDto.getDtoType(),sharedNetwork.getKey(),applyDto.getTemplate(), dmacs));
					}else{
						return;
					}
					batchSnkApplyService.apply(applyDto.getUid(), applyDto.getDtoType(), dmacs, sharedNetwork, applyDto.getTemplate(), SharedNetworkChangeType.SHARE_NETWORK_DEVICE_PART_CHANGED);
				}
			});
		}finally{
		}
		logger.info(String.format("process message[%s] successful", message));
	}
}
