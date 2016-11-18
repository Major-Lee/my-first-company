package com.bhu.vas.plugins.quartz;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bhu.vas.api.helper.BusinessEnumType;
import com.bhu.vas.api.rpc.advertise.model.Advertise;
import com.bhu.vas.business.bucache.redis.serviceimpl.advertise.WifiDeviceAdvertiseListService;
import com.bhu.vas.business.ds.advertise.service.AdvertiseService;
import com.smartwork.msip.cores.helper.DateTimeHelper;
import com.smartwork.msip.cores.orm.support.criteria.ModelCriteria;

public class AdvertiseAllClearTaskLoader {
	
	private static Logger logger = LoggerFactory.getLogger(AdvertiseAllClearTaskLoader.class);

	@Resource
	private AdvertiseService advertiseService;
	public void execute(){
		
		logger.info("AdvertiseAllClearTaskLoader start...");
		String afterDate = null;
		try {
			afterDate = DateTimeHelper.getAfterDate(DateTimeHelper.getDateTime(DateTimeHelper.FormatPattern5), 1);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		WifiDeviceAdvertiseListService.getInstance().wifiDevicesAllAdInvalid();
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
		}
		logger.info("AdvertiseAllClearTaskLoader end...");
	}
}
