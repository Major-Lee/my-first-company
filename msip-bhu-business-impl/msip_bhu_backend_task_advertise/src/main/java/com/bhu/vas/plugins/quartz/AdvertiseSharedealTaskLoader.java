package com.bhu.vas.plugins.quartz;


import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bhu.vas.api.helper.BusinessEnumType;
import com.bhu.vas.api.helper.BusinessEnumType.UWalletTransMode;
import com.bhu.vas.api.helper.BusinessEnumType.UWalletTransType;
import com.bhu.vas.api.rpc.advertise.model.Advertise;
import com.bhu.vas.api.rpc.advertise.model.AdvertiseDetails;
import com.bhu.vas.api.rpc.advertise.model.AdvertiseSharedealDetail;
import com.bhu.vas.api.rpc.charging.dto.SharedealInfo;
import com.bhu.vas.api.rpc.commdity.model.Order;
import com.bhu.vas.api.rpc.user.notify.IWalletSharedealNotifyCallback;
import com.bhu.vas.business.bucache.local.serviceimpl.wallet.BusinessWalletCacheService;
import com.bhu.vas.business.ds.advertise.service.AdvertiseDevicesIncomeService;
import com.bhu.vas.business.ds.advertise.service.AdvertiseService;
import com.bhu.vas.business.ds.advertise.service.AdvertiseSharedealDetailService;
import com.bhu.vas.business.ds.commdity.service.OrderService;
import com.bhu.vas.business.ds.user.facade.UserWalletFacadeService;
import com.smartwork.msip.business.runtimeconf.BusinessRuntimeConfiguration;
import com.smartwork.msip.cores.orm.iterator.EntityIterator;
import com.smartwork.msip.cores.orm.iterator.KeyBasedEntityBatchIterator;
import com.smartwork.msip.cores.orm.support.criteria.ModelCriteria;



/**
 * 此任务暂定每小时执行一次，但是只再广告发布后第二天的中午2点之后才开始进行分成
 * 
 * @author yetao
 * 
 */
public class AdvertiseSharedealTaskLoader {
	private static Logger logger = LoggerFactory.getLogger(AdvertiseSharedealTaskLoader.class);
	
	@Resource
	private AdvertiseService advertiseService;
	@Resource
	private OrderService orderService;
	@Resource 
	private AdvertiseDevicesIncomeService advertiseDevicesIncomeService;
	@Resource 
	private AdvertiseSharedealDetailService advertiseSharedealDetailService;
	@Resource 
	private UserWalletFacadeService userWalletFacadeService;
	@Resource
	private BusinessWalletCacheService businessWalletCacheService;
	 
	public void execute() {
		logger.info("AdvertiseSharedealTaskLoader start...");
		try{
			adOrdersForSharedeal();
		}catch(Exception e){
			e.printStackTrace();
		}
		logger.info("AdvertiseSharedealTaskLoader end...");
	}
	
	private void handleSharedetalDetail(Advertise ad, Order order){
		logger.info(String.format("handleSharedetalDetail start for  [%s]", ad.getId()));
		ModelCriteria mc = new ModelCriteria();
		mc.createCriteria().andSimpleCaulse(" 1=1 ").andColumnEqualTo("advertiseid", ad.getId());
		mc.setOrderByClause("id asc");
		mc.setPageNumber(1);
		mc.setPageSize(200);
    	EntityIterator<Long, AdvertiseSharedealDetail> it = new KeyBasedEntityBatchIterator<Long, AdvertiseSharedealDetail>(Long.class, AdvertiseSharedealDetail.class, advertiseSharedealDetailService.getEntityDao(), mc);
    	final Map<Integer, Double> incomeMap = new HashMap<Integer, Double>();
    	final Set<Integer> alluid = new HashSet<Integer>();

		long count = 0;
		while(it.hasNext()){
			List<AdvertiseSharedealDetail> list = it.next();
			for(AdvertiseSharedealDetail detail:list){
				count ++;
				if(detail.getStatus() == BusinessEnumType.AdvertiseType.SharedealCompleted.getType()){
					logger.info(String.format("detail [%s] already sharedeal handled", detail.getId()));
					continue;
				}

				if(detail.isRefund()){
					//给用户退款
					detail.setStatus(BusinessEnumType.AdvertiseType.SharedealCompleted.getType());
					advertiseSharedealDetailService.update(detail);
					if(detail.getCash() > 0){
						logger.info(String.format("refund advertise money to [%s], cash[%s]", ad.getUid(), detail.getCash()));
						userWalletFacadeService.advertiseRefundToUserWallet(ad.getUid(), order.getId(), detail.getCash(), 
								String.format("Order Cash:%s, refund cash:%s", ad.getCash(), detail.getCash()));
					}
				} else {
					//分成
					userWalletFacadeService.sharedealCashToUserWalletWithProcedure(detail.getTarget(), "", detail.getCash(), order.getId(), new Date(),
							BusinessEnumType.advertiseSharedealDesc,
							UWalletTransMode.SharedealPayment, UWalletTransType.Advertise2C, detail.getId(),
									new IWalletSharedealNotifyCallback(){
										public String notifyCashSharedealOper(SharedealInfo sharedeal) {
											if(sharedeal.getOwner() > 0){
												Double ct = incomeMap.get(sharedeal.getOwner());
												if(ct == null)
													incomeMap.put(sharedeal.getOwner(), sharedeal.getCash());
												else
													incomeMap.put(sharedeal.getOwner(),  sharedeal.getCash() + ct);
											}
											if(sharedeal.getOwner_cash() > 0 && sharedeal.getOwner() > 0)
												if(!alluid.contains(sharedeal.getOwner()))
													alluid.add(sharedeal.getOwner());
											if(sharedeal.getDistributor_cash() > 0 && sharedeal.getDistributor() > 0)
												if(!alluid.contains(sharedeal.getDistributor()))
													alluid.add(sharedeal.getDistributor());
											if(sharedeal.getDistributor_l2_cash() > 0 && sharedeal.getDistributor_l2() > 0)
												if(!alluid.contains(sharedeal.getDistributor_l2()))
													alluid.add(sharedeal.getDistributor_l2());
											return null;
										}
						});
					logger.info(String.format("handleSharedetalDetail id[%s], cash[%s], count[%s] finsh ", detail.getId(), detail.getCash(), detail.getCount()));
				}
			}
		}
		
		logger.info(String.format("handleSharedetalDetail total count [%s] done", count));
		
		Iterator<Integer> uit = incomeMap.keySet().iterator();
		while(uit.hasNext()){
			Integer uid = uit.next();
			Double money = incomeMap.get(uid);
			//给用户发送push通知
			if(money  >= 0.01){
				//暂时无法发送，当前系统push消息接收的key绑定再设备上.需要先改为绑定在uid上
//				SharedealNotifyPushDTO sharedeal_push_dto = new SharedealNotifyPushDTO();
//				sharedeal_push_dto.setMac(mac);
//				sharedeal_push_dto.setUid(sharedeal.getOwner());
//				sharedeal_push_dto.setCash(ArithHelper.getCuttedCurrency(String.valueOf(sharedeal.getOwner_cash())));
//				sharedeal_push_dto.setHd_mac(umac);
//				sharedeal_push_dto.setPayment_type(order_payment_type);
//				sharedeal_push_dto.setUmac_type(order_umac_type);
//				pushService.pushSharedealNotify(sharedeal_push_dto);
			}
		}
		
		for(Integer uid:alluid){
			//清除钱包缓存，用户能实时看到数据
			businessWalletCacheService.removeWalletLogStatisticsDSCacheResult(uid);
		}
	}
	
	private void sharedealOne(Advertise ad){
		logger.info(String.format("ad sharedeal adid[%s], order id[%s]", ad.getId(), ad.getOrderId()));
		Order order = orderService.getById(ad.getOrderId());
		//获取所有待分成明细。计算每个分成身份及其收益
		ModelCriteria mc = new ModelCriteria();
		mc.createCriteria().andSimpleCaulse(" 1=1 ").andColumnEqualTo("advertiseid", ad.getId());
		List<AdvertiseDetails> list = advertiseDevicesIncomeService.findModelByModelCriteria(mc);
		logger.info("advertise detail size:" + list.size());
		Map<String, Integer> devCountMap = new HashMap<String, Integer>(1000);
		long total = 0;
		for(AdvertiseDetails detail:list){
            logger.info(String.format("adverertise[%s], detail[%s]", ad.getId(), detail.getId()));
			total += detail.getPublish_count();
			List<String> macs = detail.getInnerModels();
			if(macs == null || macs.isEmpty()){
                logger.info("detail content is null");
				continue;
            }
			for(String mac:macs){
				Integer count = devCountMap.get(mac);
				if(count == null)
					devCountMap.put(mac, Integer.valueOf(1));
				else
					devCountMap.put(mac, Integer.valueOf(++count));
			}
		}
		if(total <= 0){
			logger.info(String.format("order id[%s] sharedeal dev is 0", ad.getId()));
			return;
		}
		logger.info("calculaing privce");
		float cash = 0;
		cash = Float.parseFloat(ad.getCash());
		double back_money = 0;
		double real_price = Double.parseDouble(ad.getCash())/ad.getCount(); //BusinessRuntimeConfiguration.Advertise_Unit_Price;
		//根据总金额和单价，计算出广告订单的预设设备广告天次
		long pre_count = ad.getCount();//(int) (cash/BusinessRuntimeConfiguration.Advertise_Unit_Price); 
		if(pre_count <= total){
			//实际广告数量大于预收款项，需要降低实际单价，来计算各个设备的分成
			real_price = cash / total;
		} else {
			//需要退还给用户的款项
			back_money = (pre_count - total) * real_price;
		}
		
		logger.info("generating sharedeal detail");

		//逐个设备计算分成并入账
		Iterator<String> it = devCountMap.keySet().iterator();
		while(it.hasNext()){
			String mac = it.next();
			int count = devCountMap.get(mac);
			AdvertiseSharedealDetail sd = new AdvertiseSharedealDetail();
			sd.setAdvertiseid(ad.getId());
			sd.setCount(count);
			sd.setCash(real_price * count);
			sd.setTarget(mac);
			sd.setRefund(false);
			advertiseSharedealDetailService.insert(sd);
		}
		
//写入一条金额为0的退费，可以用来判断整个批次是否写完整...
//		if(back_money != 0){
			AdvertiseSharedealDetail sd = new AdvertiseSharedealDetail();
			sd.setAdvertiseid(ad.getId());
			sd.setCount(1);
			sd.setRefund(true);
			sd.setCash(back_money);
			sd.setTarget(String.valueOf(ad.getUid()));
			advertiseSharedealDetailService.insert(sd);
//		}
		handleSharedetalDetail(ad, order);
	}
	
	//找出待处理的订单
	public void adOrdersForSharedeal(){
		Date tm = new Date(System.currentTimeMillis() - 14 * 3600 * 1000);
		ModelCriteria mc = new ModelCriteria();
		mc.createCriteria().andColumnLessThan("end", tm).andColumnEqualTo("type", Advertise.homeImage).andColumnEqualTo("state", BusinessEnumType.AdvertiseType.Published.getType()).
							andColumnNotEqualTo("process_state", BusinessEnumType.AdvertiseType.SharedealCompleted.getType());
		List<Advertise> lists = advertiseService.findModelByModelCriteria(mc);
		if(!lists.isEmpty()){
			logger.info("going to sharedeal:" + lists.size());
			for(Advertise ad : lists){
				try{
					//先标记为已处理。可以允许实际未处理，但是需要避免处理多次
					ad.setProcess_state(BusinessEnumType.AdvertiseType.SharedealCompleted.getType());
					advertiseService.update(ad);
					sharedealOne(ad);
				}catch(Exception e){
					e.printStackTrace();
				}
			}
			logger.info("sharedeal ad end");
		}
	}
}
