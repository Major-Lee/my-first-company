package com.bhu.vas.business.backendonline.asyncprocessor.service.impl.batchsharedeal;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import com.bhu.vas.api.rpc.devices.model.WifiDevice;
import com.bhu.vas.business.asyn.spring.model.async.BatchSharedealModifyDTO;
import com.bhu.vas.business.backendonline.asyncprocessor.buservice.BackendBusinessService;
import com.bhu.vas.business.backendonline.asyncprocessor.service.iservice.IMsgHandlerService;
import com.bhu.vas.business.bucache.redis.serviceimpl.commdity.RewardOrderAmountHashService;
import com.bhu.vas.business.ds.charging.facade.ChargingFacadeService;
import com.bhu.vas.business.ds.device.service.WifiDeviceService;
import com.bhu.vas.business.search.model.WifiDeviceDocument;
import com.bhu.vas.business.search.service.WifiDeviceDataSearchService;
import com.smartwork.msip.cores.helper.JsonHelper;
import com.smartwork.msip.cores.orm.iterator.IteratorNotify;

@Service
public class BatchSharedealServiceHandler implements IMsgHandlerService {
	private final Logger logger = LoggerFactory.getLogger(BatchSharedealServiceHandler.class);
	
	@Resource
	private WifiDeviceService wifiDeviceService;

//	@Resource
//	private UserDeviceFacadeService userDeviceFacadeService;
	
//	@Resource
//	private UserWifiDeviceFacadeService userWifiDeviceFacadeService;
	
	@Resource
	private ChargingFacadeService chargingFacadeService;
	
	//@Resource
	//private BackendBusinessService backendBusinessService;
	
	@Resource
	private WifiDeviceDataSearchService wifiDeviceDataSearchService;
	
	@Resource
	private BackendBusinessService backendBusinessService;
	/*@Resource
	private IDaemonRpcService daemonRpcService;*/

	@Override
	public void process(String message) {
		logger.info(String.format("process message[%s]", message));
		final BatchSharedealModifyDTO sharedealDTO = JsonHelper.getDTO(message, BatchSharedealModifyDTO.class);
		final String operUser = String.valueOf(sharedealDTO.getUid());
		final boolean needCheckBinding = sharedealDTO.isNeedCheckBinding();
		wifiDeviceDataSearchService.iteratorAll(sharedealDTO.getMessage(),100,
				new IteratorNotify<Page<WifiDeviceDocument>>() {
					@Override
					public void notifyComming(Page<WifiDeviceDocument> pages) {
						List<String> macList = new ArrayList<>();
						for (WifiDeviceDocument doc : pages) {
							if(needCheckBinding){
								logger.info(String.format("******** operUser:%s   uid:%s",operUser,doc.getU_id()));
								if(operUser.equals(doc.getU_id())){//需要检测绑定的情况下，判断索引中用户id是否和参数相等
									macList.add(doc.getD_mac());
								}
							}else{
								macList.add(doc.getD_mac());
							}
						}
						logger.info(String.format("pagesize:%s pages:%s",100,macList));
						for(String dmac:macList){
							chargingFacadeService.doWifiDeviceSharedealConfigsUpdate(null,null,null, dmac, 
									sharedealDTO.getCbto(),sharedealDTO.getEl(),
									sharedealDTO.isCustomized(),
									sharedealDTO.getOwner_percent(),sharedealDTO.getManufacturer_percent(),sharedealDTO.getDistributor_percent(),
									sharedealDTO.getRcm(), sharedealDTO.getRcp(), sharedealDTO.getAit(), false);
//							//更新出货渠道
//							WifiDevice wifidevice = wifiDeviceService.getById(dmac);
//							wifidevice.setChannel_lv1(sharedealDTO.getChannel_lv1());
//							wifidevice.setChannel_lv2(sharedealDTO.getChannel_lv2());
						}
						try {
							RewardOrderAmountHashService.getInstance().removeAllRAmountByMacs(macList.toArray(new String[0]));
							backendBusinessService.blukIndexs(macList);
							Thread.sleep(300);
						} catch (InterruptedException e) {
							e.printStackTrace(System.out);
						} catch (Exception e) {
							e.printStackTrace(System.out);
						} finally{
							if(macList != null){
								macList.clear();
								macList = null;
							}
						}
					}
				});
		logger.info(String.format("process message[%s] successful", message));
	}
}
