package com.bhu.vas.business.ds.device.service;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bhu.vas.api.rpc.devices.model.WifiDeviceForGps;
import com.bhu.vas.business.ds.device.dao.WifiDeviceForGpsDao;
import com.smartwork.msip.business.abstractmsd.service.AbstractCoreService;
//EntityCacheableSpliterService
@Service
@Transactional("coreTransactionManager")
public class WifiDeviceForGpsService extends AbstractCoreService<String,WifiDeviceForGps, WifiDeviceForGpsDao>{//EntityCacheableSpliterService<StorePurchasedItemPK,StorePurchasedItem, StorePurchasedItemDao,Integer>{//EntitySpliterService
	
	@Resource
	@Override
	public void setEntityDao(WifiDeviceForGpsDao wifiDeviceForGpsDao) {
		super.setEntityDao(wifiDeviceForGpsDao);
	}
}
