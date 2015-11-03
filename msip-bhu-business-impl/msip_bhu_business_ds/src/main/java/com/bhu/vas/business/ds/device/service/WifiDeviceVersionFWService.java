package com.bhu.vas.business.ds.device.service;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bhu.vas.api.rpc.devices.model.WifiDeviceVersionFW;
import com.bhu.vas.business.ds.device.dao.WifiDeviceVersionFWDao;
import com.smartwork.msip.business.abstractmsd.service.AbstractCoreService;
//EntityCacheableSpliterService
@Service
@Transactional("coreTransactionManager")
public class WifiDeviceVersionFWService extends AbstractCoreService<String,WifiDeviceVersionFW, WifiDeviceVersionFWDao>{//EntityCacheableSpliterService<StorePurchasedItemPK,StorePurchasedItem, StorePurchasedItemDao,Integer>{//EntitySpliterService
	
	@Resource
	@Override
	public void setEntityDao(WifiDeviceVersionFWDao wifiDeviceVersionFWDao) {
		super.setEntityDao(wifiDeviceVersionFWDao);
	}

}
