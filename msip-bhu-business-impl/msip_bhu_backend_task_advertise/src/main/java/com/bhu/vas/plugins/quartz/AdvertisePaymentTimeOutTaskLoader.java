package com.bhu.vas.plugins.quartz;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;

import com.bhu.vas.api.helper.BusinessEnumType;
import com.bhu.vas.api.rpc.advertise.model.Advertise;
import com.bhu.vas.business.asyn.spring.activemq.service.async.AsyncDeliverMessageService;
import com.bhu.vas.business.asyn.spring.model.IDTO;
import com.bhu.vas.business.bucache.redis.serviceimpl.advertise.AdvertiseSnapShotListService;
import com.bhu.vas.business.bucache.redis.serviceimpl.advertise.WifiDeviceAdvertiseSortedSetService;
import com.bhu.vas.business.ds.advertise.service.AdvertiseService;
import com.bhu.vas.business.search.model.device.WifiDeviceDocument;
import com.bhu.vas.business.search.service.device.WifiDeviceDataSearchService;
import com.smartwork.msip.cores.orm.iterator.IteratorNotify;
import com.smartwork.msip.cores.orm.support.criteria.ModelCriteria;
import com.smartwork.msip.cores.orm.support.criteria.PerfectCriteria.Criteria;

public class AdvertisePaymentTimeOutTaskLoader {
	private static Logger logger = LoggerFactory.getLogger(AdvertisePaymentTimeOutTaskLoader.class);
	@Resource
	private AdvertiseService advertiseService;
	
	@Resource
	private WifiDeviceDataSearchService wifiDeviceDataSearchService;	
	
	@Resource
	private AsyncDeliverMessageService asyncDeliverMessageService;
	
	public void execute(){
		logger.info("AdvertisePaymentTimeOutTaskLoader start.....");
		Date nowDate = new Date();
		long nowTime = nowDate.getTime();
		homeImagePaymentTimeOut(nowTime);
		invaildHomeImageSmallArea(nowDate);
		logger.info("AdvertisePaymentTimeOutTaskLoader end.....");
	}
	
	public void homeImagePaymentTimeOut(long nowTime){
		ModelCriteria mc = new ModelCriteria();
		Criteria homeImageCriteria=mc.createCriteria();
		homeImageCriteria.andColumnEqualTo("state", BusinessEnumType.AdvertiseStateType.UnPaid.getType()).andColumnEqualTo("type", BusinessEnumType.AdvertiseType.HomeImage.getType());
		
		Criteria sortMessageCriteria=mc.createCriteria();
		sortMessageCriteria.andColumnEqualTo("state", BusinessEnumType.AdvertiseStateType.UnPaid.getType()).andColumnEqualTo("type", BusinessEnumType.AdvertiseType.SortMessage.getType());
		
		mc.or(homeImageCriteria);
		mc.or(sortMessageCriteria);
		
		
		List<Advertise> ads = advertiseService.findModelByModelCriteria(mc);
		List<Advertise> updateList = new ArrayList<Advertise>(); 		
		logger.info(String.format("AdvertiseHomeImagePaymentTimeOutTaskLoader unpaid timeout sum[%s]", ads.size()));
		
		for(Advertise ad : ads){
			if(nowTime - ad.getCreated_at().getTime() > 20*60*1000){
				ad.setState(BusinessEnumType.AdvertiseStateType.EscapeOrder.getType());
				ad.setReject_reason("因支付超时,已经取消本推广订单");
				updateList.add(ad);
				if(ad.getType() == BusinessEnumType.AdvertiseType.HomeImage_SmallArea.getType()){
					AdvertiseSnapShotListService.getInstance().destorySnapShot(ad.getId());
				}
			}
		}
		advertiseService.updateAll(updateList);
	}
	
	public void invaildHomeImageSmallArea(Date date){
		logger.info("invaildHomeImageSmallArea start..");
		ModelCriteria mc = new ModelCriteria();
		mc.createCriteria().andColumnEqualTo("state", BusinessEnumType.AdvertiseStateType.OnPublish.getType()).andColumnEqualTo("type", BusinessEnumType.AdvertiseType.HomeImage_SmallArea.getType()).andColumnLessThan("end", date);
		List<Advertise> ads = advertiseService.findModelByModelCriteria(mc);
		List<String> adIds = new ArrayList<String>();
		if(!ads.isEmpty()){
			for(Advertise ad : ads){
				logger.info(String.format("invaildHomeImageSmallArea adid[%s] count[%s]", ad.getId(),ad.getCount()));
				List<String> macList = AdvertiseSnapShotListService.getInstance().fetchAdvertiseSnapShot(ad.getId());
				WifiDeviceAdvertiseSortedSetService.getInstance().wifiDevicesAdInvalid(macList, Double.valueOf(ad.getId()));
				AdvertiseSnapShotListService.getInstance().destorySnapShot(ad.getId());
				ad.setType(BusinessEnumType.AdvertiseStateType.Published.getType());
				adIds.add(ad.getId());
			}
			advertiseService.updateAll(ads);
		}
		asyncDeliverMessageService.sendBatchDeviceApplyAdvertiseActionMessage(adIds,IDTO.ACT_DELETE);
		logger.info("invaildHomeImageSmallArea end..");
	}
}
