package com.bhu.vas.business.ds.charging.service;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bhu.vas.api.rpc.charging.model.WifiDeviceBatchImport;
import com.bhu.vas.business.bucache.redis.serviceimpl.unique.SequenceService;
import com.bhu.vas.business.ds.charging.dao.WifiDeviceBatchImportDao;
import com.smartwork.msip.business.abstractmsd.service.AbstractCoreService;

@Service
@Transactional("coreTransactionManager")
public class WifiDeviceBatchImportService extends AbstractCoreService<String,WifiDeviceBatchImport,WifiDeviceBatchImportDao>{
	
	@Resource
	@Override
	public void setEntityDao(WifiDeviceBatchImportDao wifiDeviceBatchImportDao) {
		super.setEntityDao(wifiDeviceBatchImportDao);
	}
	
	@Override
	public WifiDeviceBatchImport insert(WifiDeviceBatchImport entity) {
		if(entity.getId() == null)
			SequenceService.getInstance().onCreateSequenceKey(entity, false);
		return super.insert(entity);
	}
}
