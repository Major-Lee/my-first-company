package com.bhu.vas.business.ds.device.service;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bhu.vas.api.rpc.devices.model.WifiDeviceGrayVersion;
import com.bhu.vas.business.ds.device.dao.WifiDeviceGrayVersionDao;
import com.smartwork.msip.business.abstractmsd.service.AbstractCoreService;
//EntityCacheableSpliterService
@Service
@Transactional("coreTransactionManager")
public class WifiDeviceGrayVersionService extends AbstractCoreService<Integer,WifiDeviceGrayVersion, WifiDeviceGrayVersionDao>{//EntityCacheableSpliterService<StorePurchasedItemPK,StorePurchasedItem, StorePurchasedItemDao,Integer>{//EntitySpliterService
	
	@Resource
	@Override
	public void setEntityDao(WifiDeviceGrayVersionDao wifiDeviceGrayVersionDao) {
		super.setEntityDao(wifiDeviceGrayVersionDao);
	}

}
