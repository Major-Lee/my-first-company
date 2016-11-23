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
		ModelCriteria mc = new ModelCriteria();
		mc.createCriteria().andColumnEqualTo("state", BusinessEnumType.AdvertiseType.UnPaid.getType());
		List<Advertise> ads = advertiseService.findModelByModelCriteria(mc);
		List<Advertise> updateList = new ArrayList<Advertise>(); 		
		logger.info(String.format("AdvertisePaymentTimeOutTaskLoader unpaid timeout sum[%s]", ads.size()));
		
		for(Advertise ad : ads){
			if(nowTime - ad.getCreated_at().getTime() > 10*60*1000){
				ad.setState(BusinessEnumType.AdvertiseType.EscapeOrder.getType());
				ad.setReject_reason("因支付超时,已经取消本推广订单");
				updateList.add(ad);
			}
		}
		advertiseService.updateAll(updateList);
		logger.info("AdvertisePaymentTimeOutTaskLoader end.....");
	}
}
