package com.bhu.vas.business.device.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bhu.vas.api.rpc.devices.model.HandsetDevice;
import com.bhu.vas.business.device.dao.HandsetDeviceDao;
import com.smartwork.msip.business.abstractmsd.service.AbstractCoreService;
import com.smartwork.msip.cores.orm.support.criteria.CommonCriteria;
//EntityCacheableSpliterService
@Service
@Transactional("coreTransactionManager")
public class HandsetDeviceService extends AbstractCoreService<String,HandsetDevice, HandsetDeviceDao>{//EntityCacheableSpliterService<StorePurchasedItemPK,StorePurchasedItem, StorePurchasedItemDao,Integer>{//EntitySpliterService
	
	@Resource
	@Override
	public void setEntityDao(HandsetDeviceDao handsetDeviceDao) {
		super.setEntityDao(handsetDeviceDao);
	}
	
	/**
	 * 查询指定wifiId下的在线的移动设备
	 * @param wifiId
	 * @return
	 */
	public List<HandsetDevice> findModelByWifiIdAndOnline(String wifiId){
		CommonCriteria mc = new CommonCriteria();
		mc.createCriteria().andColumnEqualTo("online", true)
						   .andColumnEqualTo("last_wifi_id", wifiId);
		return super.findModelByCommonCriteria(mc);
	}
}
