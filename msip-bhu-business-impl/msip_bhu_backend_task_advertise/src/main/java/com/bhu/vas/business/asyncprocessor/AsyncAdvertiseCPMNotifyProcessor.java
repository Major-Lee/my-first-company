package com.bhu.vas.business.asyncprocessor;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
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
import com.bhu.vas.business.asyn.spring.activemq.service.async.AsyncDeliverMessageService;
import com.bhu.vas.business.asyn.spring.model.async.advertise.BatchAdvertiseCPMNotifyDTO;
import com.bhu.vas.business.bucache.local.serviceimpl.wallet.BusinessWalletCacheService;
import com.bhu.vas.business.bucache.redis.serviceimpl.advertise.AdvertiseCPMListService;
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
import com.smartwork.msip.plugins.hook.observer.ExecObserverManager;

@Service
public class AsyncAdvertiseCPMNotifyProcessor {
	public static final int ProcessesThreadCount = 10;
	private final Logger logger = LoggerFactory.getLogger(AsyncAdvertiseCPMNotifyProcessor.class);
	private ThreadPoolExecutor exec_processes = null;
	private ExecutorService exec_dispatcher = null;
	
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
	@Resource
	private AsyncDeliverMessageService asyncDeliverMessageService;
	@PostConstruct
	public void initialize() {
		logger.info("AsyncAdvertiseCPMNotifyProcessor initialize...");
		exec_processes = (ThreadPoolExecutor)ExecObserverManager.buildExecutorService(this.getClass(),"AsyncAdvertiseCPMNotify processes消息处理",ProcessesThreadCount);
		exec_dispatcher = ExecObserverManager.buildExecutorService(this.getClass(),"AsyncAdvertiseCPMNotify dispatcher消息处理",1);
		runDispatcherExecutor();
	}
	
	/**
	 * 启动运行分发线程
	 */
	public void runDispatcherExecutor(){
		logger.info("AsyncAdvertiseCPMNotifyProcessor runDispatcherExecutor");
		exec_dispatcher.submit((new Runnable() {
			@Override
			public void run() {
				while(true){
					try{
						if(ProcessesThreadCount > exec_processes.getActiveCount()){
							String message = AdvertiseCPMListService.getInstance().AdCPMNotify();
							if (StringUtils.isNotEmpty(message)){
								onProcessor(message);
							}else{
								Thread.sleep(10);
							}
						}else{
							Thread.sleep(10);
						}
					}catch(Exception ex){
						ex.printStackTrace(System.out);
						logger.error("AsyncAdvertiseCPMNotifyProcessor Dispatcher Executor", ex);
					}
				}
			}
		}));
	}
	
	/**
	 * 执行线程处理消息
	 * @param message
	 */
	public void onProcessor(final String message){
		if(StringUtils.isEmpty(message)) return;
		
		exec_processes.submit((new Runnable() {
			@Override
			public void run() {
				try{
//					asyncDeliverMessageService.sendBatchAdvertiseCPMNotifyActionMessage(message);
					process(message);
				}catch(Exception ex){
					ex.printStackTrace(System.out);
					logger.error("AsyncAdvertiseCPMNotify onProcessor", ex);
				}
			}
		}));
	}
	
	
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
	
	public void process(String message) {
		logger.info(String.format("process message[%s]", message));
		AdvertiseCPMDTO cpmDto = JsonHelper.getDTO(message,AdvertiseCPMDTO.class);
		Advertise entity = advertiseService.getById(cpmDto.getAdid());
		AdvertiseDocument doc = advertiseDataSearchService.searchById(cpmDto.getAdid());

		int uid = entity.getUid();
		int result = 1;
		
		long oldScore = doc.getA_score();
		long topScore = 100000000000000L;
		
		if(uid< 80000 && uid >80999 && entity.getTop() == 1){
			Map<String, Object>outParam = new HashMap<String, Object>();
			result = userConsumptiveWalletFacadeService.userPurchaseGoods(uid, cpmDto.getAdid(), cpm_price, 
					UConsumptiveWalletTransType.AdsCPM, String.format("ad cpm计费 uid[%s] adid[%s]", uid,cpmDto.getAdid()), null, outParam);
			if(result == 0){
				if(StringUtils.isNotEmpty(cpmDto.getMac())){
					Long cpmid = (Long)outParam.get("cpmid");
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
