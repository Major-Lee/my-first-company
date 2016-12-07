package com.bhu.vas.plugins.quartz;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bhu.vas.api.helper.BusinessEnumType;
import com.bhu.vas.api.rpc.advertise.model.Advertise;
import com.bhu.vas.business.ds.advertise.service.AdvertiseService;
import com.smartwork.msip.cores.orm.support.criteria.ModelCriteria;

public class AdvertisePaymentTimeOutTaskLoader {
	private static Logger logger = LoggerFactory.getLogger(AdvertisePaymentTimeOutTaskLoader.class);
	@Resource
	private AdvertiseService advertiseService;
	
	public void execute(){
		logger.info("AdvertisePaymentTimeOutTaskLoader start.....");
		Date nowDate = new Date();
		long nowTime = nowDate.getTime();
		homeImagePaymentTimeOut(nowTime);
		sortMessagePaymentTimeOut(nowTime);
		logger.info("AdvertisePaymentTimeOutTaskLoader end.....");
	}
	
	public void homeImagePaymentTimeOut(long nowTime){
		ModelCriteria mc = new ModelCriteria();
		mc.createCriteria().andColumnEqualTo("state", BusinessEnumType.AdvertiseType.UnPaid.getType()).andColumnEqualTo("type", Advertise.homeImage);
		List<Advertise> ads = advertiseService.findModelByModelCriteria(mc);
		List<Advertise> updateList = new ArrayList<Advertise>(); 		
		logger.info(String.format("AdvertiseHomeImagePaymentTimeOutTaskLoader unpaid timeout sum[%s]", ads.size()));
		
		for(Advertise ad : ads){
			if(nowTime - ad.getCreated_at().getTime() > 20*60*1000){
				ad.setState(BusinessEnumType.AdvertiseType.EscapeOrder.getType());
				ad.setReject_reason("因支付超时,已经取消本推广订单");
				updateList.add(ad);
			}
		}
		advertiseService.updateAll(updateList);
	}
	
	public void sortMessagePaymentTimeOut(long nowTime){
		ModelCriteria mc = new ModelCriteria();
		mc.createCriteria().andColumnEqualTo("state", BusinessEnumType.AdvertiseType.UnPaid.getType()).andColumnEqualTo("type", Advertise.sortMessage);
		List<Advertise> ads = advertiseService.findModelByModelCriteria(mc);
		List<Advertise> updateList = new ArrayList<Advertise>(); 		
		logger.info(String.format("AdvertiseSortMessagePaymentTimeOutTaskLoader unpaid timeout sum[%s]", ads.size()));
		
		for(Advertise ad : ads){
			if(ad.getStart().getTime() - nowTime < 24*60*60*1000){
				ad.setState(BusinessEnumType.AdvertiseType.EscapeOrder.getType());
				ad.setReject_reason("因支付超时,已经取消本推广订单");
				updateList.add(ad);
			}
		}
		advertiseService.updateAll(updateList);
	}
}
