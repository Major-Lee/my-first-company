package com.bhu.vas.business.ds.advertise.facade;


import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.bhu.vas.api.helper.BusinessEnumType;
import com.bhu.vas.api.rpc.advertise.model.Advertise;
import com.bhu.vas.business.ds.advertise.service.AdvertiseService;

@Service
public class AdvertiseFacadeService {
	private final Logger logger = LoggerFactory.getLogger(AdvertiseFacadeService.class);
	
	@Resource
	private AdvertiseService advertiseService;
	
	public String advertisePayment(String advertiseId){
		logger.info(String.format("advertisePayment  advertiseId[%s]", advertiseId));
		Advertise ad = advertiseService.getById(advertiseId);
//		return ad.getCash();
		return "0.1";
	}
	
	public void advertiseCompletionOfPayment(String advertiseId,String orderId){
		logger.info(String.format("advertiseCompletionOfPayment  advertiseId[%s]", advertiseId));
		Advertise ad = advertiseService.getById(advertiseId);
		ad.setState(BusinessEnumType.AdvertiseType.UnVerified.getType());
		ad.setOrderId(orderId);
		advertiseService.update(ad);
		logger.info("advertiseCompletionOfPayment  finish");
	}
	
}
