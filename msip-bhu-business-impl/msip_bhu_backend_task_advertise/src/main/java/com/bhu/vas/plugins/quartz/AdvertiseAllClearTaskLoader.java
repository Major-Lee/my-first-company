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
import com.bhu.vas.business.bucache.redis.serviceimpl.advertise.WifiDeviceAdvertiseListService;
import com.bhu.vas.business.ds.advertise.service.AdvertiseService;
import com.smartwork.msip.cores.helper.DateTimeHelper;
import com.smartwork.msip.cores.helper.StringHelper;
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
		WifiDeviceAdvertiseListService.getInstance().wifiDevicesAllAdInvalid();
		devicesDomainClear(afterDate);
		initOnpublishSign();
		logger.info("AdvertiseAllClearTaskLoader end...");
	}
	//失效广告清除域名
	public void devicesDomainClear(String afterDate){
		ModelCriteria mc = new ModelCriteria();
		mc.createCriteria().andColumnLessThan("end", afterDate).andColumnEqualTo("state", BusinessEnumType.AdvertiseType.OnPublish.getType());
		List<Advertise> lists = advertiseService.findModelByModelCriteria(mc);
		if(!lists.isEmpty()){
			logger.info("ready invalid ad sum: " + lists.size());
			List<String> adIds = new ArrayList<String>();
			for(Advertise ad : lists){
				adIds.add(ad.getId());
				ad.setState(BusinessEnumType.AdvertiseType.Published.getType());
			}
			advertiseService.updateAll(lists);
			asyncDeliverMessageService.sendBatchDeviceApplyAdvertiseActionMessage(adIds,IDTO.ACT_DELETE);
		}
	}
	//把需要持续发布的广告标志位重置
	public void initOnpublishSign(){
		ModelCriteria mc = new ModelCriteria();
		mc.createCriteria().andColumnEqualTo("state", BusinessEnumType.AdvertiseType.OnPublish.getType()).andColumnEqualTo("sign",StringHelper.TRUE);
		List<Advertise> ads = advertiseService.findModelByModelCriteria(mc);
		for(Advertise ad :ads){
			ad.setSign(false);
		}
		advertiseService.updateAll(ads);
	}
}