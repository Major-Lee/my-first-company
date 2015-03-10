package com.bhu.vas.business.device.service;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bhu.vas.api.rpc.devices.model.Device;
import com.bhu.vas.business.device.dao.DeviceDao;
import com.bhu.vas.business.sequence.service.SequenceService;
import com.smartwork.msip.business.abstractmsd.service.AbstractCoreService;
//EntityCacheableSpliterService
@Service
@Transactional("coreTransactionManager")
public class DeviceService extends AbstractCoreService<String,Device, DeviceDao>{//EntityCacheableSpliterService<StorePurchasedItemPK,StorePurchasedItem, StorePurchasedItemDao,Integer>{//EntitySpliterService
	
	@Resource
	private SequenceService sequenceService;
	
	@Resource
	@Override
	public void setEntityDao(DeviceDao deviceDao) {
		super.setEntityDao(deviceDao);
	}

}
