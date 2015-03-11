package com.bhu.vas.business.task.service;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bhu.vas.api.rpc.task.model.WifiDeviceDownTask;
import com.bhu.vas.business.task.dao.WifiDeviceDownTaskDao;
import com.smartwork.msip.business.abstractmsd.service.AbstractCoreService;
//EntityCacheableSpliterService
@Service
@Transactional("coreTransactionManager")
public class WifiDeviceDownTaskService extends AbstractCoreService<String,WifiDeviceDownTask, WifiDeviceDownTaskDao>{//EntityCacheableSpliterService<StorePurchasedItemPK,StorePurchasedItem, StorePurchasedItemDao,Integer>{//EntitySpliterService
	
	@Resource
	@Override
	public void setEntityDao(WifiDeviceDownTaskDao wifiDeviceDownTaskDao) {
		super.setEntityDao(wifiDeviceDownTaskDao);
	}

}
