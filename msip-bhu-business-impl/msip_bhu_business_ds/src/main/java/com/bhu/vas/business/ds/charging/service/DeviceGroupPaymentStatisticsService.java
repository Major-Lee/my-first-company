package com.bhu.vas.business.ds.charging.service;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bhu.vas.api.rpc.charging.model.DeviceGroupPaymentStatistics;
import com.bhu.vas.business.ds.charging.dao.DeviceGroupPaymentStatisticsDao;
import com.smartwork.msip.business.abstractmsd.service.AbstractCoreService;

@Service
@Transactional("coreTransactionManager")
public class DeviceGroupPaymentStatisticsService extends AbstractCoreService<String, DeviceGroupPaymentStatistics, DeviceGroupPaymentStatisticsDao>{//EntityCacheableSpliterService<StorePurchasedItemPK,StorePurchasedItem, StorePurchasedItemDao,Integer>{//EntitySpliterService
	@Resource
	@Override
	public void setEntityDao(DeviceGroupPaymentStatisticsDao deviceGroupPaymentStatisticsDao) {
		super.setEntityDao(deviceGroupPaymentStatisticsDao);
	}

}
