package com.bhu.vas.business.task.service;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bhu.vas.api.rpc.task.model.WifiDeviceCompletedDownTask;
import com.bhu.vas.business.task.dao.WifiDeviceCompletedDownTaskDao;
import com.smartwork.msip.business.abstractmsd.service.AbstractCoreService;
//EntityCacheableSpliterService
@Service
@Transactional("coreTransactionManager")
public class WifiDeviceCompletedDownTaskService extends AbstractCoreService<String,WifiDeviceCompletedDownTask, WifiDeviceCompletedDownTaskDao>{//EntityCacheableSpliterService<StorePurchasedItemPK,StorePurchasedItem, StorePurchasedItemDao,Integer>{//EntitySpliterService
	
	@Resource
	@Override
	public void setEntityDao(WifiDeviceCompletedDownTaskDao wifiDeviceCompletedDownTaskDao) {
		super.setEntityDao(wifiDeviceCompletedDownTaskDao);
	}

}
