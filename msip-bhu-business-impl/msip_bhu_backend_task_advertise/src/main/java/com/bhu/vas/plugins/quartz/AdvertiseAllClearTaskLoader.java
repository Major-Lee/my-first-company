package com.bhu.vas.plugins.quartz;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bhu.vas.api.helper.BusinessEnumType;
import com.bhu.vas.api.rpc.advertise.model.Advertise;
import com.bhu.vas.business.asyn.spring.activemq.service.async.AsyncDeliverMessageService;
import com.bhu.vas.business.asyn.spring.model.IDTO;
import com.bhu.vas.business.bucache.redis.serviceimpl.advertise.UserMobilePositionRelationSortedSetService;
import com.bhu.vas.business.bucache.redis.serviceimpl.advertise.WifiDeviceAdvertiseSortedSetService;
import com.bhu.vas.business.ds.advertise.service.AdvertiseService;
import com.smartwork.msip.cores.helper.DateTimeHelper;
import com.smartwork.msip.cores.orm.support.criteria.ModelCriteria;

public class AdvertiseAllClearTaskLoader {
	
	private static Logger logger = LoggerFactory.getLogger(AdvertiseAllClearTaskLoader.class);

	@Resource
	private AdvertiseService advertiseService;
	
	@Resource
	private AsyncDeliverMessageService asyncDeliverMessageService;
	
	public void execute(){
		logger.info("AdvertiseAllClearTaskLoader start...");
		
		String afterDate = null;
		try {
			afterDate = DateTimeHelper.getAfterDate(DateTimeHelper.getDateTime(DateTimeHelper.FormatPattern5), 1);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		WifiDeviceAdvertiseSortedSetService.getInstance().wifiDevicesAllAdInvalid();
		devicesDomainClear(afterDate);
		initOnpublishSign();
		logger.info("AdvertiseAllClearTaskLoader end...");
	}
	//失效广告清除域名and状态变更
	public void devicesDomainClear(String afterDate){
		ModelCriteria mc = new ModelCriteria();
		mc.createCriteria().andColumnLessThan("end", afterDate).andColumnEqualTo("state", BusinessEnumType.AdvertiseStateType.OnPublish.getType()).andColumnEqualTo("type", BusinessEnumType.AdvertiseType.HomeImage.getType());
		List<Advertise> lists = advertiseService.findModelByModelCriteria(mc);
		if(!lists.isEmpty()){
			logger.info("devicesDomainClear  ready invalid ad sum: " + lists.size());
			List<String> adIds = new ArrayList<String>();
			for(Advertise ad : lists){
				adIds.add(ad.getId());
				ad.setState(BusinessEnumType.AdvertiseStateType.Published.getType());
				if (ad.getType() == BusinessEnumType.AdvertiseType.SortMessage.getType()) {
					UserMobilePositionRelationSortedSetService.getInstance().destoryMobilenoSnaoShot(ad.getId());
				}
			}
			advertiseService.updateAll(lists);
			asyncDeliverMessageService.sendBatchDeviceApplyAdvertiseActionMessage(adIds,IDTO.ACT_DELETE);
		}
	}
	//把需要持续发布的广告标志位重置
	public void initOnpublishSign(){
		ModelCriteria mc = new ModelCriteria();
		mc.createCriteria().andColumnEqualTo("state", BusinessEnumType.AdvertiseStateType.OnPublish.getType()).andColumnEqualTo("sign",true);
		List<Advertise> ads = advertiseService.findModelByModelCriteria(mc);
		for(Advertise ad :ads){
			System.out.println("initOnpublishSign id :" +ad.getId());
			ad.setSign(false);
		}
		advertiseService.updateAll(ads);
	}
}
