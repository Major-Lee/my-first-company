package com.bhu.vas.business.ds.charging.service;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bhu.vas.api.rpc.charging.model.WifiDeviceSharedealConfigs;
import com.bhu.vas.business.ds.charging.dao.WifiDeviceSharedealConfigsDao;
import com.smartwork.msip.business.abstractmsd.service.AbstractCoreService;

@Service
@Transactional("coreTransactionManager")
public class WifiDeviceSharedealConfigsService extends AbstractCoreService<String,WifiDeviceSharedealConfigs,WifiDeviceSharedealConfigsDao>{
	
	@Resource
	@Override
	public void setEntityDao(WifiDeviceSharedealConfigsDao wifiDeviceSharedealConfigsDao) {
		super.setEntityDao(wifiDeviceSharedealConfigsDao);
	}
	
}
