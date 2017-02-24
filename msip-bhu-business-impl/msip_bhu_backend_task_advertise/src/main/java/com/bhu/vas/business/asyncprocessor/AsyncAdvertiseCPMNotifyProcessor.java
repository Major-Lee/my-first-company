package com.bhu.vas.business.asyncprocessor;

import java.util.ArrayList;
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
import com.bhu.vas.api.rpc.charging.model.WifiDeviceSharedealConfigs;
import com.bhu.vas.api.rpc.user.notify.IWalletSharedealNotifyCallback;
import com.bhu.vas.business.asyn.spring.activemq.service.async.AsyncDeliverMessageService;
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
import com.smartwork.msip.business.runtimeconf.BusinessRuntimeConfiguration;
import com.smartwork.msip.cores.helper.HashAlgorithmsHelper;
import com.smartwork.msip.cores.helper.JsonHelper;
import com.smartwork.msip.exception.BusinessI18nCodeException;
import com.smartwork.msip.jdo.ResponseErrorCode;
import com.smartwork.msip.plugins.hook.observer.ExecObserverManager;

@Service
public class AsyncAdvertiseCPMNotifyProcessor {
	public static final int ProcessesThreadCount = 5;
	private final Logger logger = LoggerFactory.getLogger(AsyncAdvertiseCPMNotifyProcessor.class);
	private List<ExecutorService> exec_processes = null;
	private ExecutorService exec_dispatcher = null;
	private ThreadPoolExecutor dispatch_processes = null;
	
	private List<ExecutorService> sharedeal_exec_processes = null;
	private int hash_prime = 10;

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
//		exec_processes = (ThreadPoolExecutor)ExecObserverManager.buildExecutorService(this.getClass(),"AsyncAdvertiseCPMNotify processes消息处理",ProcessesThreadCount);
		dispatch_processes = (ThreadPoolExecutor)ExecObserverManager.buildExecutorService(this.getClass(),"AsyncAdvertiseCPMNotify processes消息处理",ProcessesThreadCount);
        exec_dispatcher = ExecObserverManager.buildExecutorService(this.getClass(),"AsyncAdvertiseCPMNotify dispatcher消息处理",1);
	
		exec_processes = new ArrayList<ExecutorService>();
		sharedeal_exec_processes = new ArrayList<ExecutorService>();

		for(int i=0;i<hash_prime;i++){
			ExecutorService exec_process = ExecObserverManager.buildSingleThreadExecutor(this.getClass(),"CPMS process消息处理".concat(String.valueOf(i)));
			exec_processes.add(exec_process);
			
			ExecutorService sharedeal_exec_process = ExecObserverManager.buildSingleThreadExecutor(this.getClass(),"CPMSharedeal process消息处理".concat(String.valueOf(i)));
			sharedeal_exec_processes.add(sharedeal_exec_process);

		}

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
						if(ProcessesThreadCount > dispatch_processes.getActiveCount()){
							String message = AdvertiseCPMListService.getInstance().AdCPMNotify();
							if (StringUtils.isNotEmpty(message)){
								onDispatch(message);
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
	
	
	public void onCpmProcessor(final String mac, final String umac, final String adid, final Long cpmid){
		int hash = HashAlgorithmsHelper.rotatingHash(mac, hash_prime);
		sharedeal_exec_processes.get(hash).submit((new Runnable() {
			@Override
			public void run(){
				cpmSharedeal(mac, umac, adid, cpmid);
			}
		}));
	}
	
	private void dispatch(String message){
		final AdvertiseCPMDTO cpmDto = JsonHelper.getDTO(message,AdvertiseCPMDTO.class);
		final Advertise entity = advertiseService.getById(cpmDto.getAdid());
		int hash = HashAlgorithmsHelper.rotatingHash(String.valueOf(entity.getUid()), hash_prime);
		exec_processes.get(hash).submit((new Runnable(){
			@Override
			public void run(){
//				asyncDeliverMessageService.sendBatchAdvertiseCPMNotifyActionMessage(message);
				process(cpmDto, entity);
			}
		}));
	}
	
	
	/**
	 * 执行线程处理消息
	 * @param message
	 */
	public void onDispatch(final String message){
		if(StringUtils.isEmpty(message)) return;
		
		dispatch_processes.submit((new Runnable() {
			@Override
			public void run() {
				try{
					logger.info(String.format("dispatch message[%s]", message));
					dispatch(message);
				}catch(Exception ex){
					ex.printStackTrace(System.out);
					logger.error("AsyncAdvertiseCPMNotify onDispatch", ex);
				}
			}
		}));
	}
	
	private void cpmSharedeal(final String mac, final String umac, final String adid, final Long cpmid){
		try{
		userWalletFacadeService.sharedealCashToUserWalletWithProcedure(mac, umac, BusinessRuntimeConfiguration.AdvertiseCPMPrices, adid, new Date(),
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
	
	public void process(AdvertiseCPMDTO cpmDto, Advertise entity) {
//		logger.info(String.format("process message[%s]", message));
//		AdvertiseCPMDTO cpmDto = JsonHelper.getDTO(message,AdvertiseCPMDTO.class);
		AdvertiseDocument doc = advertiseDataSearchService.searchById(cpmDto.getAdid());

		int uid = entity.getUid();
		int result = 1;
		
		long oldScore = doc.getA_score();
		long topScore = 100000000000000L;
		
		if((uid< 80000 || uid >80999) && entity.getTop() == 1){
			
			logger.info(String.format("BatchAdvertseCPMServiceHandler cpm: uid[%s] adid[%s]", uid, cpmDto.getAdid()));
			
			Map<String, Object>outParam = new HashMap<String, Object>();
			result = userConsumptiveWalletFacadeService.userPurchaseGoods(uid, cpmDto.getAdid(), BusinessRuntimeConfiguration.AdvertiseCPMPrices, 
					UConsumptiveWalletTransType.AdsCPM, String.format("ad cpm计费 uid[%s] adid[%s]", uid,cpmDto.getAdid()), null, outParam);
			if(result == 0){
				if(StringUtils.isNotEmpty(cpmDto.getMac())){
					Long cpmid = (Long)outParam.get("cpmid");
					if(cpmid != null && cpmid.longValue() > 0){
						if(!WifiDeviceSharedealConfigs.Default_ConfigsWifiID.equals(cpmDto.getMac()))
							onCpmProcessor(cpmDto.getMac(), cpmDto.getUmac(), entity.getId(), cpmid);
					} else {
						logger.info("cpm id error");
					}
				}
				String balance = userConsumptiveWalletFacadeService.getUserCash(uid);
				if(Double.valueOf(balance) < BusinessRuntimeConfiguration.AdvertiseCPMPrices){
					entity.setTop(0);
					advertiseService.update(entity);
					List<String> maclist = AdvertiseSnapShotListService.getInstance().fetchAdvertiseSnapShot(cpmDto.getAdid());
					WifiDeviceAdvertiseSortedSetService.getInstance().wifiDevicesAdApply(
							maclist, JsonHelper.getJSONString(entity.toRedis()), oldScore - topScore);
					advertiseIndexIncrementService.adScoreUpdIncrement(cpmDto.getAdid(), oldScore - topScore,0);
					
					throw new BusinessI18nCodeException(ResponseErrorCode.ORDER_PAYMENT_VCURRENCY_NOTSUFFICIENT);
				}
			}else if(result == 1){
				entity.setTop(0);
				advertiseService.update(entity);
				List<String> maclist = AdvertiseSnapShotListService.getInstance().fetchAdvertiseSnapShot(cpmDto.getAdid());
				WifiDeviceAdvertiseSortedSetService.getInstance().wifiDevicesAdApply(
						maclist, JsonHelper.getJSONString(entity.toRedis()), oldScore - topScore);
				advertiseIndexIncrementService.adScoreUpdIncrement(cpmDto.getAdid(), oldScore - topScore,0);
				
				throw new BusinessI18nCodeException(ResponseErrorCode.ORDER_PAYMENT_VCURRENCY_NOTSUFFICIENT);
			}
		}
	}
}
