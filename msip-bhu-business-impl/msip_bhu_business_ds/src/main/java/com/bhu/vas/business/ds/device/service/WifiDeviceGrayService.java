package com.bhu.vas.business.ds.device.service;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bhu.vas.api.rpc.devices.model.WifiDeviceGray;
import com.bhu.vas.business.ds.device.dao.WifiDeviceGrayDao;
import com.smartwork.msip.business.abstractmsd.service.AbstractCoreService;
//EntityCacheableSpliterService
@Service
@Transactional("coreTransactionManager")
public class WifiDeviceGrayService extends AbstractCoreService<String,WifiDeviceGray, WifiDeviceGrayDao>{//EntityCacheableSpliterService<StorePurchasedItemPK,StorePurchasedItem, StorePurchasedItemDao,Integer>{//EntitySpliterService
	
	@Resource
	@Override
	public void setEntityDao(WifiDeviceGrayDao wifiDeviceGrayDao) {
		super.setEntityDao(wifiDeviceGrayDao);
	}

}
