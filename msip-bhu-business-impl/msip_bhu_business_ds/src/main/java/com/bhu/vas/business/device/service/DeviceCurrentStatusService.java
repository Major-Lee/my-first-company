package com.bhu.vas.business.device.service;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bhu.vas.api.rpc.devices.model.DeviceCurrentStatus;
import com.bhu.vas.business.device.dao.DeviceCurrentStatusDao;
import com.bhu.vas.business.sequence.service.SequenceService;
import com.smartwork.msip.business.abstractmsd.service.AbstractCoreService;
//EntityCacheableSpliterService
@Service
@Transactional("coreTransactionManager")
public class DeviceCurrentStatusService extends AbstractCoreService<String,DeviceCurrentStatus, DeviceCurrentStatusDao>{//EntityCacheableSpliterService<StorePurchasedItemPK,StorePurchasedItem, StorePurchasedItemDao,Integer>{//EntitySpliterService
	
	@Resource
	private SequenceService sequenceService;
	
	@Resource
	@Override
	public void setEntityDao(DeviceCurrentStatusDao deviceCurrentStatusDao) {
		super.setEntityDao(deviceCurrentStatusDao);
	}

}
