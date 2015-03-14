package com.bhu.vas.business.device.service;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bhu.vas.api.rpc.devices.model.HandsetDevice;
import com.bhu.vas.business.device.dao.HandsetDeviceDao;
import com.smartwork.msip.business.abstractmsd.service.AbstractCoreService;
//EntityCacheableSpliterService
@Service
@Transactional("coreTransactionManager")
public class HandsetDeviceService extends AbstractCoreService<String,HandsetDevice, HandsetDeviceDao>{//EntityCacheableSpliterService<StorePurchasedItemPK,StorePurchasedItem, StorePurchasedItemDao,Integer>{//EntitySpliterService
	
	@Resource
	@Override
	public void setEntityDao(HandsetDeviceDao handsetDeviceDao) {
		super.setEntityDao(handsetDeviceDao);
	}
	
	public void udpateOnlineByWifiMac(String wifi_mac){
		
	}
	
}
