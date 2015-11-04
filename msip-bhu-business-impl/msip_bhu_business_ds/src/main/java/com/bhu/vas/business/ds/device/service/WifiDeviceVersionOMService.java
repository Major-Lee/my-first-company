package com.bhu.vas.business.ds.device.service;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bhu.vas.api.rpc.devices.model.WifiDeviceVersionOM;
import com.bhu.vas.business.ds.device.dao.WifiDeviceVersionOMDao;
import com.smartwork.msip.business.abstractmsd.service.AbstractCoreService;
//EntityCacheableSpliterService
@Service
@Transactional("coreTransactionManager")
public class WifiDeviceVersionOMService extends AbstractCoreService<String,WifiDeviceVersionOM, WifiDeviceVersionOMDao>{//EntityCacheableSpliterService<StorePurchasedItemPK,StorePurchasedItem, StorePurchasedItemDao,Integer>{//EntitySpliterService
	
	@Resource
	@Override
	public void setEntityDao(WifiDeviceVersionOMDao wifiDeviceVersionOMDao) {
		super.setEntityDao(wifiDeviceVersionOMDao);
	}

}
