package com.bhu.vas.business.backendonline.asyncprocessor.service.impl.batchsharedeal;

import java.util.Arrays;
import java.util.List;

import javax.annotation.Resource;

import org.elasticsearch.common.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.bhu.vas.business.asyn.spring.model.async.BatchSharedealModifyBySnDTO;
import com.bhu.vas.business.backendonline.asyncprocessor.service.iservice.IMsgHandlerService;
import com.bhu.vas.business.ds.charging.facade.ChargingFacadeService;
import com.bhu.vas.business.ds.device.service.WifiDeviceService;
import com.smartwork.msip.cores.helper.JsonHelper;
import com.smartwork.msip.cores.orm.support.criteria.CommonCriteria;

@Service
public class BatchSharedealBySnServiceHandler implements IMsgHandlerService {
	private final Logger logger = LoggerFactory.getLogger(BatchSharedealBySnServiceHandler.class);
	
	@Resource
	private WifiDeviceService wifiDeviceService;
	
	@Resource
	private ChargingFacadeService chargingFacadeService;
	
	@Override
	public void process(String message) {
		logger.info(String.format("process message[%s]", message));
		final BatchSharedealModifyBySnDTO sharedealDTO = JsonHelper.getDTO(message, BatchSharedealModifyBySnDTO.class);
		final String operUser = String.valueOf(sharedealDTO.getUid());

		if(StringUtils.isEmpty(sharedealDTO.getSns()))
			return;
		String[] sns =  sharedealDTO.getSns().split(",");
		
		CommonCriteria mc = new CommonCriteria();
		mc.createCriteria().andSimpleCaulse(" 1=1 ").andColumnIn("sn", Arrays.asList(sns));
		List<String> macList = wifiDeviceService.findIdsByCommonCriteria(mc);
		
		logger.info(String.format("pagesize:%s pages:%s",100,macList));
		for(String dmac:macList){
			chargingFacadeService.doWifiDeviceSharedealConfigsUpdate(null,null,null, null, null, dmac, 
					null, null,
					true,
					sharedealDTO.getOwner_percent(),sharedealDTO.getManufacturer_percent(),sharedealDTO.getDistributor_percent(),sharedealDTO.getDistributor_l2_percent(),
					//sharedealDTO.getRcm(), sharedealDTO.getRcp(), sharedealDTO.getAit(),
					false);
		}
		if(macList != null){
			macList.clear();
			macList = null;
		}
		logger.info(String.format("process message[%s] successful", message));
	}
}