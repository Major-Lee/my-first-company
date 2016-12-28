package com.bhu.vas.business.ds.advertise.facade;


import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.bhu.vas.api.dto.HandsetDeviceDTO;
import com.bhu.vas.api.helper.BusinessEnumType;
import com.bhu.vas.api.rpc.advertise.model.Advertise;
import com.bhu.vas.api.rpc.devices.model.WifiDevice;
import com.bhu.vas.api.vto.advertise.AdCommdityVTO;
import com.bhu.vas.business.bucache.redis.serviceimpl.advertise.UserMobilePositionRelationSortedSetService;
import com.bhu.vas.business.ds.advertise.service.AdvertiseService;
import com.bhu.vas.business.ds.device.service.WifiDeviceService;
import com.bhu.vas.business.ds.user.facade.UserIdentityAuthFacadeService;
import com.bhu.vas.business.ds.user.facade.UserWalletFacadeService;
import com.smartwork.msip.business.runtimeconf.BusinessRuntimeConfiguration;
import com.smartwork.msip.cores.helper.sms.SmsSenderFactory;

@Service
public class AdvertiseFacadeService {
	private final Logger logger = LoggerFactory.getLogger(AdvertiseFacadeService.class);
	
	@Resource
	private AdvertiseService advertiseService;
	
	@Resource
	private UserWalletFacadeService userWalletFacadeService;
	
	@Resource
	private WifiDeviceService wifiDeviceService;
	
	@Resource 
	private UserIdentityAuthFacadeService userIdentityAuthFacadeService;
	
	public AdCommdityVTO advertisePayment(String advertiseId){
		logger.info(String.format("advertisePayment  advertiseId[%s]", advertiseId));
		Advertise ad = advertiseService.getById(advertiseId);
		AdCommdityVTO vto = new AdCommdityVTO();
		vto.setType(ad.getType());
		vto.setCash("0.1");
		vto.setCreated_at(ad.getCreated_at());
		return vto;
	}
	
	public void advertiseCompletionOfPayment(String advertiseId,String orderId){
		logger.info(String.format("advertiseCompletionOfPayment  advertiseId[%s]", advertiseId));
		Advertise ad = advertiseService.getById(advertiseId);
		if(ad.getState() == BusinessEnumType.AdvertiseStateType.UnPaid.getType()){
			ad.setState(BusinessEnumType.AdvertiseStateType.UnVerified.getType());
			ad.setOrderId(orderId);
			advertiseService.update(ad);
			logger.info("advertiseCompletionOfPayment  finish");
			
//			String smsg = String.format(BusinessRuntimeConfiguration.Advertise_Verify_Notify_Template, advertiseId);
//			String response = SmsSenderFactory.buildSender(
//						BusinessRuntimeConfiguration.InternalCaptchaCodeSMS_Gateway).send(smsg, "15127166171");
//			logger.info(String.format("全程热播订单%s已支付,发送短信提醒成功 response: %s",advertiseId,response));
		}else{
			ad.setReject_reason("因订单超时，您所支付的费用已经退回至必虎钱包，您可以在必虎钱包中申请提现");
			advertiseService.update(ad);
			userWalletFacadeService.advertiseRefundToUserWallet(ad.getUid(), ad.getOrderId(), Double.parseDouble(ad.getCash()), 
					String.format("Order Cash:%s, all refund cash:%s", ad.getCash(), ad.getCash()));
		}
	}
	
	public void userMobilenoCollect(String mac,String hdmac){
		WifiDevice wifiDevice = wifiDeviceService.getById(mac);
		String mobileno = userIdentityAuthFacadeService.fetchUserMobilenoByHdmac(hdmac);
		if(!mobileno.isEmpty()){
			UserMobilePositionRelationSortedSetService.getInstance().mobilenoRecord(wifiDevice.getProvince(), wifiDevice.getCity(), wifiDevice.getDistrict(), mobileno);
		}
	}
	
	public void userMobilenoCollect(String mac,List<HandsetDeviceDTO> dtos){
		List<String> hdmacs = new ArrayList<String>();
		for(HandsetDeviceDTO dto : dtos){
			hdmacs.add(dto.getMac());
		}
		WifiDevice wifiDevice = wifiDeviceService.getById(mac);
		for(String hdmac : hdmacs){
			if(!hdmac.isEmpty()){
				String mobileno = userIdentityAuthFacadeService.fetchUserMobilenoByHdmac(hdmac);
				UserMobilePositionRelationSortedSetService.getInstance().mobilenoRecord(wifiDevice.getProvince(), wifiDevice.getCity(), wifiDevice.getDistrict(), mobileno);
			}
		}
	}
	
}
