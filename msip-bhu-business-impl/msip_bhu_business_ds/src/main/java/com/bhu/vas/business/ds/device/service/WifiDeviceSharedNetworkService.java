package com.bhu.vas.business.ds.device.service;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bhu.vas.api.rpc.devices.model.WifiDeviceSharedNetwork;
import com.bhu.vas.business.ds.device.dao.WifiDeviceSharedNetworkDao;
import com.smartwork.msip.business.abstractmsd.service.AbstractCoreService;

@Service
@Transactional("coreTransactionManager")
public class WifiDeviceSharedNetworkService extends AbstractCoreService<String, WifiDeviceSharedNetwork, WifiDeviceSharedNetworkDao>{
	
	@Resource
	@Override
	public void setEntityDao(WifiDeviceSharedNetworkDao wifiDeviceSharedNetworkDao) {
		super.setEntityDao(wifiDeviceSharedNetworkDao);
	}
}
