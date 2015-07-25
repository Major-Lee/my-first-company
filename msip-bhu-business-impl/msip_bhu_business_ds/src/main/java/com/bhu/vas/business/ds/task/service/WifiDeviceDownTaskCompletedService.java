package com.bhu.vas.business.ds.task.service;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bhu.vas.api.rpc.task.model.WifiDeviceDownTaskCompleted;
import com.bhu.vas.business.ds.task.dao.WifiDeviceDownTaskCompletedDao;
import com.smartwork.msip.business.abstractmsd.service.AbstractCoreService;
//EntityCacheableSpliterService
@Service
@Transactional("coreTransactionManager")
public class WifiDeviceDownTaskCompletedService extends AbstractCoreService<Long,WifiDeviceDownTaskCompleted, WifiDeviceDownTaskCompletedDao>{//EntityCacheableSpliterService<StorePurchasedItemPK,StorePurchasedItem, StorePurchasedItemDao,Integer>{//EntitySpliterService
	
	@Resource
	@Override
	public void setEntityDao(WifiDeviceDownTaskCompletedDao wifiDeviceDownTaskCompletedDao) {
		super.setEntityDao(wifiDeviceDownTaskCompletedDao);
	}

}
