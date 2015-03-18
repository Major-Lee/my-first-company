package com.bhu.vas.business.ds.device.service;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bhu.vas.api.rpc.devices.model.WifiDeviceCurrentStatus;
import com.bhu.vas.business.ds.device.dao.WifiDeviceCurrentStatusDao;
import com.smartwork.msip.business.abstractmsd.service.AbstractCoreService;
//EntityCacheableSpliterService
@Service
@Transactional("coreTransactionManager")
public class WifiDeviceCurrentStatusService extends AbstractCoreService<String,WifiDeviceCurrentStatus, WifiDeviceCurrentStatusDao>{//EntityCacheableSpliterService<StorePurchasedItemPK,StorePurchasedItem, StorePurchasedItemDao,Integer>{//EntitySpliterService
	
	
	@Resource
	@Override
	public void setEntityDao(WifiDeviceCurrentStatusDao wifiDeviceCurrentStatusDao) {
		super.setEntityDao(wifiDeviceCurrentStatusDao);
	}

}
