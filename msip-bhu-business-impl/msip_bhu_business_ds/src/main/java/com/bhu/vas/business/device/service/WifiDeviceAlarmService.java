package com.bhu.vas.business.device.service;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bhu.vas.api.rpc.devices.model.WifiDeviceAlarm;
import com.bhu.vas.business.device.dao.WifiDeviceAlarmDao;
import com.smartwork.msip.business.abstractmsd.service.AbstractCoreService;
//EntityCacheableSpliterService
@Service
@Transactional("coreTransactionManager")
public class WifiDeviceAlarmService extends AbstractCoreService<Integer,WifiDeviceAlarm, WifiDeviceAlarmDao>{//EntityCacheableSpliterService<StorePurchasedItemPK,StorePurchasedItem, StorePurchasedItemDao,Integer>{//EntitySpliterService
	
	@Resource
	@Override
	public void setEntityDao(WifiDeviceAlarmDao wifiDeviceAlarmDao) {
		super.setEntityDao(wifiDeviceAlarmDao);
	}

}
