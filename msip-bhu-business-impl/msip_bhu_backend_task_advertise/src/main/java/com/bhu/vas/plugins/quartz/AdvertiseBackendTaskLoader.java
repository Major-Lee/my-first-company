package com.bhu.vas.plugins.quartz;


import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bhu.vas.api.helper.BusinessEnumType;
import com.bhu.vas.api.rpc.advertise.model.Advertise;
import com.bhu.vas.business.asyn.spring.activemq.service.async.AsyncDeliverMessageService;
import com.bhu.vas.business.asyn.spring.model.IDTO;
import com.bhu.vas.business.bucache.redis.serviceimpl.advertise.WifiDeviceAdvertiseListService;
import com.bhu.vas.business.ds.advertise.service.AdvertiseService;
import com.smartwork.msip.cores.helper.DateTimeHelper;
import com.smartwork.msip.cores.orm.support.criteria.ModelCriteria;



/**
 * 此任务暂定5分钟执行一次 根据配置的同时运行的任务数量决定是否需要重新把新的任务加入到任务池中
 * 
 * @author xiaowei
 * 
 */
public class AdvertiseBackendTaskLoader {
	private static Logger logger = LoggerFactory.getLogger(AdvertiseBackendTaskLoader.class);

	@Resource
	private AdvertiseService advertiseService;
	
	@Resource
	private AsyncDeliverMessageService asyncDeliverMessageService;
		
	public void execute() {
		logger.info("AdvertiseBackendTaskLoader start...");
		String afterDate = null;
		try {
			afterDate = DateTimeHelper.getAfterDate(DateTimeHelper.getDateTime(DateTimeHelper.FormatPattern5), 1);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		logger.info("afterDate:"+afterDate);
		omittedOrTimelyAdApplyNotify(afterDate);
		logger.info("AdvertiseBackendTaskLoader end...");
	}
	
	public void omittedOrTimelyAdApplyNotify(String afterDate){
		ModelCriteria mc = new ModelCriteria();
		mc.createCriteria().andColumnLessThan("start", afterDate).andColumnGreaterThan("end", afterDate).andColumnEqualTo("state", BusinessEnumType.AdvertiseType.UnPublish.getType());
		List<Advertise> lists = advertiseService.findModelByModelCriteria(mc);
		if(!lists.isEmpty()){
			logger.info("ready applied ad sum" + lists.size());
			List<String> adIds = new ArrayList<String>();
			for(Advertise ad : lists){
				adIds.add(ad.getId());
				ad.setState(BusinessEnumType.AdvertiseType.OnPublish.getType());
			}
			advertiseService.updateAll(lists);
			logger.info("apply notify backend ..start");
			asyncDeliverMessageService.sendBatchDeviceApplyAdvertiseActionMessage(adIds,IDTO.ACT_ADD);
			logger.info("apply notify backend ..done");
		}
	}
	
	public void OnpublicContinueApply(String afterDate){
		ModelCriteria mc = new ModelCriteria();
		mc.createCriteria().andColumnLessThan("start", afterDate).andColumnGreaterThan("end", afterDate).andColumnEqualTo("state", BusinessEnumType.AdvertiseType.OnPublish.getType());
		List<Advertise> lists = advertiseService.findModelByModelCriteria(mc);
		if(!lists.isEmpty()){
			logger.info("ready applied ad sum" + lists.size());
			List<String> adIds = new ArrayList<String>();
			for(Advertise ad : lists){
				adIds.add(ad.getId());
			}
			logger.info("apply notify backend ..start");
			asyncDeliverMessageService.sendBatchDeviceApplyAdvertiseActionMessage(adIds,IDTO.ACT_ADD);
			logger.info("apply notify backend ..done");
		}
	}
	
	
//	public void AdInvalidNotify(String afterDate){
//		WifiDeviceAdvertiseListService.getInstance().wifiDevicesAllAdInvalid();
//		ModelCriteria mc = new ModelCriteria();
//		mc.createCriteria().andColumnLessThan("end", afterDate).andColumnEqualTo("state", BusinessEnumType.AdvertiseType.OnPublish.getType());
//		List<Advertise> lists = advertiseService.findModelByModelCriteria(mc);
//		if(!lists.isEmpty()){
//			logger.info("ready invalid ad sum: " + lists.size());
//			List<String> adIds = new ArrayList<String>();
//			for(Advertise ad : lists){
//				adIds.add(ad.getId());
//				ad.setState(BusinessEnumType.AdvertiseType.Published.getType());
//			}
//			advertiseService.updateAll(lists);
//			logger.info("invalid notify backend ..start");
//			asyncDeliverMessageService.sendBatchDeviceApplyAdvertiseActionMessage(adIds,IDTO.ACT_DELETE);
//			logger.info("invalid notify backend ..done");
//		}
//	}
}
