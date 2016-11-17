package com.bhu.vas.plugins.quartz;


import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bhu.vas.api.helper.BusinessEnumType;
import com.bhu.vas.api.rpc.advertise.model.Advertise;
import com.bhu.vas.business.ds.advertise.service.AdvertiseDevicesIncomeService;
import com.bhu.vas.business.ds.advertise.service.AdvertiseService;
import com.bhu.vas.business.search.service.WifiDeviceDataSearchService;
import com.smartwork.msip.cores.orm.support.criteria.ModelCriteria;


/**
 * 每天中午十二点统计符合广告分成的设备及数量
 * @author xiaowei
 *
 */
public class AdvertiseOnPublishDevicesStatisticsLoader {
	private static Logger logger = LoggerFactory.getLogger(AdvertiseOnPublishDevicesStatisticsLoader.class);
	
	@Resource
	private WifiDeviceDataSearchService wifiDeviceDataSearchService;
	@Resource
	private AdvertiseService advertiseService;
	
	@Resource
	private AdvertiseDevicesIncomeService advertiseDevicesIncomeService;
	
	public void execute() {

	}
	
	public List<Advertise> fetchOnpublishAdvertise(){
		ModelCriteria mc = new ModelCriteria();
		mc.createCriteria().andColumnEqualTo("state", BusinessEnumType.AdvertiseType.OnPublish.getType());
		return advertiseService.findModelByModelCriteria(mc);
	}
}
