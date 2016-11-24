package com.bhu.vas.business.ds.advertise.facade;


import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.bhu.vas.api.helper.BusinessEnumType;
import com.bhu.vas.api.rpc.advertise.model.Advertise;
import com.bhu.vas.api.vto.advertise.AdCommdityVTO;
import com.bhu.vas.business.ds.advertise.service.AdvertiseService;
import com.bhu.vas.business.ds.user.facade.UserWalletFacadeService;

@Service
public class AdvertiseFacadeService {
	private final Logger logger = LoggerFactory.getLogger(AdvertiseFacadeService.class);
	
	@Resource
	private AdvertiseService advertiseService;
	
	@Resource
	private UserWalletFacadeService userWalletFacadeService;
	
	public AdCommdityVTO advertisePayment(String advertiseId){
		logger.info(String.format("advertisePayment  advertiseId[%s]", advertiseId));
		Advertise ad = advertiseService.getById(advertiseId);
		AdCommdityVTO vto = new AdCommdityVTO();
		vto.setCash("0.1");
		vto.setCreated_at(ad.getCreated_at());
		return vto;
	}
	
	public void advertiseCompletionOfPayment(String advertiseId,String orderId){
		logger.info(String.format("advertiseCompletionOfPayment  advertiseId[%s]", advertiseId));
		Advertise ad = advertiseService.getById(advertiseId);
		if(ad.getState() == BusinessEnumType.AdvertiseType.UnPaid.getType()){
			ad.setState(BusinessEnumType.AdvertiseType.UnVerified.getType());
			ad.setOrderId(orderId);
			advertiseService.update(ad);
			logger.info("advertiseCompletionOfPayment  finish");
		}else{
			ad.setReject_reason("因订单超时，您所支付的费用已经退回至必虎钱包，您可以在必虎钱包中申请提现");
			advertiseService.update(ad);
			userWalletFacadeService.advertiseRefundToUserWallet(ad.getUid(), ad.getOrderId(), Double.parseDouble(ad.getCash()), 
					String.format("Order Cash:%s, all refund cash:%s", ad.getCash(), ad.getCash()));
		}
	}
}
