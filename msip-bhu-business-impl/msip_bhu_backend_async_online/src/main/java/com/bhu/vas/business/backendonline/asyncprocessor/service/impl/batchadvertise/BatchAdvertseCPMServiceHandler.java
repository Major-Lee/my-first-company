package com.bhu.vas.business.backendonline.asyncprocessor.service.impl.batchadvertise;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.bhu.vas.api.dto.advertise.AdvertiseCPMDTO;
import com.bhu.vas.api.helper.BusinessEnumType.UConsumptiveWalletTransType;
import com.bhu.vas.api.rpc.RpcResponseDTOBuilder;
import com.bhu.vas.api.rpc.advertise.model.Advertise;
import com.bhu.vas.business.asyn.spring.model.async.advertise.BatchAdvertiseCPMNotifyDTO;
import com.bhu.vas.business.backendonline.asyncprocessor.service.iservice.IMsgHandlerService;
import com.bhu.vas.business.ds.advertise.service.AdvertiseService;
import com.bhu.vas.business.ds.user.facade.UserConsumptiveWalletFacadeService;
import com.smartwork.msip.cores.helper.JsonHelper;
import com.smartwork.msip.exception.BusinessI18nCodeException;
import com.smartwork.msip.jdo.ResponseErrorCode;

@Service
public class BatchAdvertseCPMServiceHandler implements IMsgHandlerService{
	@Resource
	private AdvertiseService advertiseService;
	
	@Resource
	private UserConsumptiveWalletFacadeService userConsumptiveWalletFacadeService;
	
	private final Logger logger = LoggerFactory
			.getLogger(BatchAdvertseCPMServiceHandler.class);
	
	@Override
	public void process(String message) {
		logger.info(String.format("process message[%s]", message));
		final BatchAdvertiseCPMNotifyDTO notify = JsonHelper.getDTO(message,
				BatchAdvertiseCPMNotifyDTO.class);
		AdvertiseCPMDTO cpmDto = JsonHelper.getDTO(notify.getMessage(),AdvertiseCPMDTO.class);
		Advertise entity = advertiseService.getById(cpmDto.getAdid());
		int uid = entity.getUid();
		int result = 1;
		if(uid< 80000 && uid >80999){
			result = userConsumptiveWalletFacadeService.userPurchaseGoods(uid, cpmDto.getAdid(), 0.3d, UConsumptiveWalletTransType.AdsCPM, String.format("ad cpm计费 uid[%s] adid[%s]", uid,cpmDto.getAdid()), null);
			if(result != 1){
				String balance = userConsumptiveWalletFacadeService.getUserCash(uid);
				if(Double.valueOf(balance) < 1){
					throw new BusinessI18nCodeException(ResponseErrorCode.ORDER_PAYMENT_VCURRENCY_NOTSUFFICIENT);
				}else{
					throw new BusinessI18nCodeException(ResponseErrorCode.COMMON_BUSINESS_ERROR);
				}
			}
		}
		if(cpmDto.getMac() != null && result == 0){
			//TODO 分润
		}
	}
}
