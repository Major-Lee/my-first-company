package com.bhu.vas.business.ds.task.service;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bhu.vas.api.rpc.task.model.WifiDeviceDownTask;
import com.bhu.vas.business.bucache.redis.serviceimpl.unique.SequenceService;
import com.bhu.vas.business.ds.task.dao.WifiDeviceDownTaskDao;
import com.smartwork.msip.business.abstractmsd.service.AbstractCoreService;
//EntityCacheableSpliterService
@Service
@Transactional("coreTransactionManager")
public class WifiDeviceDownTaskService extends AbstractCoreService<Long,WifiDeviceDownTask, WifiDeviceDownTaskDao>{//EntityCacheableSpliterService<StorePurchasedItemPK,StorePurchasedItem, StorePurchasedItemDao,Integer>{//EntitySpliterService
	
	@Resource
	@Override
	public void setEntityDao(WifiDeviceDownTaskDao wifiDeviceDownTaskDao) {
		super.setEntityDao(wifiDeviceDownTaskDao);
	}
	
	@Override
	public WifiDeviceDownTask insert(WifiDeviceDownTask entity) {
		if(entity.getId() == null)
			SequenceService.getInstance().onCreateSequenceKey(entity, false);
		return super.insert(entity);
	}
	
	
	public static long getNextId(){
		return SequenceService.getInstance().getNextId(WifiDeviceDownTask.class.getName());
	}
}
