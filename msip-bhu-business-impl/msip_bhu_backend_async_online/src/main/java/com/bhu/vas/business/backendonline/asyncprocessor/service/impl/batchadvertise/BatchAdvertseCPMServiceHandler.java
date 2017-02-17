package com.bhu.vas.business.backendonline.asyncprocessor.service.impl.batchadvertise;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.elasticsearch.common.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.bhu.vas.api.dto.advertise.AdvertiseCPMDTO;
import com.bhu.vas.api.helper.BusinessEnumType;
import com.bhu.vas.api.helper.BusinessEnumType.UConsumptiveWalletTransType;
import com.bhu.vas.api.helper.BusinessEnumType.UWalletTransMode;
import com.bhu.vas.api.helper.BusinessEnumType.UWalletTransType;
import com.bhu.vas.api.rpc.advertise.model.Advertise;
import com.bhu.vas.api.rpc.charging.dto.SharedealInfo;
import com.bhu.vas.api.rpc.user.notify.IWalletSharedealNotifyCallback;
import com.bhu.vas.business.asyn.spring.model.async.advertise.BatchAdvertiseCPMNotifyDTO;
import com.bhu.vas.business.backendonline.asyncprocessor.service.iservice.IMsgHandlerService;
import com.bhu.vas.business.bucache.local.serviceimpl.wallet.BusinessWalletCacheService;
import com.bhu.vas.business.bucache.redis.serviceimpl.advertise.AdvertiseSnapShotListService;
import com.bhu.vas.business.bucache.redis.serviceimpl.advertise.WifiDeviceAdvertiseSortedSetService;
import com.bhu.vas.business.ds.advertise.service.AdvertiseService;
import com.bhu.vas.business.ds.user.facade.UserConsumptiveWalletFacadeService;
import com.bhu.vas.business.ds.user.facade.UserWalletFacadeService;
import com.bhu.vas.business.search.model.advertise.AdvertiseDocument;
import com.bhu.vas.business.search.service.advertise.AdvertiseDataSearchService;
import com.bhu.vas.business.search.service.increment.advertise.AdvertiseIndexIncrementService;
import com.smartwork.msip.cores.helper.JsonHelper;
import com.smartwork.msip.exception.BusinessI18nCodeException;
import com.smartwork.msip.jdo.ResponseErrorCode;

@Service
public class BatchAdvertseCPMServiceHandler implements IMsgHandlerService{
	@Resource
	private AdvertiseService advertiseService;
	
	@Resource
	private UserConsumptiveWalletFacadeService userConsumptiveWalletFacadeService;
	@Resource
	private UserWalletFacadeService userWalletFacadeService;
	@Resource
	private BusinessWalletCacheService businessWalletCacheService;
	@Resource
	private AdvertiseDataSearchService advertiseDataSearchService;
	@Resource
	private AdvertiseIndexIncrementService advertiseIndexIncrementService;
	
	private final Logger logger = LoggerFactory
			.getLogger(BatchAdvertseCPMServiceHandler.class);
	
	public static final double cpm_price = 0.3d;
	
	private void cpmSharedeal(final String mac, final String umac, String adid, Long cpmid){
		try{
		userWalletFacadeService.sharedealCashToUserWalletWithProcedure(mac, umac, cpm_price, adid, new Date(),
				BusinessEnumType.templateCpmDesc,
				UWalletTransMode.SharedealPayment, UWalletTransType.CPM2C,  cpmid,
						new IWalletSharedealNotifyCallback(){
							public String notifyCashSharedealOper(SharedealInfo sharedeal) {
								logger.info(String.format("BatchAdvertseCPMServiceHandler cpmSharedeal: uid[%s] "
										+ "cash[%s] mac[%s] umac[%s]", sharedeal.getOwner(), sharedeal.getOwner_cash(),  mac, umac));
//								if(sharedeal.getOwner() > 0 && sharedeal.getOwner_cash()  >= 0.01){
//									SharedealNotifyPushDTO sharedeal_push_dto = new SharedealNotifyPushDTO();
//									sharedeal_push_dto.setMac(mac);
//									sharedeal_push_dto.setUid(sharedeal.getOwner());
//									sharedeal_push_dto.setCash(ArithHelper.getCuttedCurrency(String.valueOf(sharedeal.getOwner_cash())));
//									sharedeal_push_dto.setHd_mac(umac);
//									sharedeal_push_dto.setPayment_type(order_payment_type);
//									sharedeal_push_dto.setUmac_type(order_umac_type);
//									pushService.pushSharedealNotify(sharedeal_push_dto, PushType.QualityGoodsSharedealNotify);
//								}
								// 分成成功后清除用户钱包日志统计缓存数据,再次查询时就是最新的数据
								if(sharedeal.getOwner_cash() > 0 && sharedeal.getOwner() > 0)
									businessWalletCacheService.removeWalletLogStatisticsDSCacheResult(sharedeal.getOwner());
								if(sharedeal.getDistributor_cash() > 0 && sharedeal.getDistributor() > 0)
									businessWalletCacheService.removeWalletLogStatisticsDSCacheResult(sharedeal.getDistributor());
								if(sharedeal.getDistributor_l2_cash() > 0 && sharedeal.getDistributor_l2() > 0)
									businessWalletCacheService.removeWalletLogStatisticsDSCacheResult(sharedeal.getDistributor_l2());
								return null;
							}
			});
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	@Override
	public void process(String message) {
		logger.info(String.format("process message[%s]", message));
		final BatchAdvertiseCPMNotifyDTO notify = JsonHelper.getDTO(message,
				BatchAdvertiseCPMNotifyDTO.class);
		AdvertiseCPMDTO cpmDto = JsonHelper.getDTO(notify.getMessage(),AdvertiseCPMDTO.class);
		Advertise entity = advertiseService.getById(cpmDto.getAdid());
		AdvertiseDocument doc = advertiseDataSearchService.searchById(cpmDto.getAdid());

		int uid = entity.getUid();
		int result = 1;
		
		long oldScore = doc.getA_score();
		long topScore = 100000000000000L;
		
		if(uid< 80000 && uid >80999){
			Map<String, Long>outParam = new HashMap<String, Long>();
			result = userConsumptiveWalletFacadeService.userPurchaseGoods(uid, cpmDto.getAdid(), cpm_price, 
					UConsumptiveWalletTransType.AdsCPM, String.format("ad cpm计费 uid[%s] adid[%s]", uid,cpmDto.getAdid()), null, outParam);
			if(result == 0){
				if(StringUtils.isNotEmpty(cpmDto.getMac())){
					Long cpmid = outParam.get("cpmid");
					if(cpmid != null && cpmid.longValue() > 0){
						cpmSharedeal(cpmDto.getMac(), cpmDto.getUmac(), entity.getId(), cpmid);
					} else {
						logger.info("cpm id error");
					}
				}
				String balance = userConsumptiveWalletFacadeService.getUserCash(uid);
				if(Double.valueOf(balance) < cpm_price){
					entity.setTop(0);
					advertiseService.update(entity);
					List<String> maclist = AdvertiseSnapShotListService.getInstance().fetchAdvertiseSnapShot(cpmDto.getAdid());
					WifiDeviceAdvertiseSortedSetService.getInstance().wifiDevicesAdApply(
							maclist, JsonHelper.getJSONString(entity.toRedis()), oldScore - topScore);
					advertiseIndexIncrementService.adScoreUpdIncrement(cpmDto.getAdid(), oldScore - topScore,0);
					
					throw new BusinessI18nCodeException(ResponseErrorCode.ORDER_PAYMENT_VCURRENCY_NOTSUFFICIENT);
				}else{
					throw new BusinessI18nCodeException(ResponseErrorCode.COMMON_BUSINESS_ERROR);
				}
			}
		}
	}
}


