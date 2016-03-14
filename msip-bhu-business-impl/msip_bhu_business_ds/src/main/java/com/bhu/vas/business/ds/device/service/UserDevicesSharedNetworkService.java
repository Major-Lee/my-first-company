package com.bhu.vas.business.ds.device.service;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bhu.vas.api.rpc.devices.model.UserDevicesSharedNetwork;
import com.bhu.vas.business.ds.device.dao.UserDevicesSharedNetworkDao;
import com.smartwork.msip.business.abstractmsd.service.AbstractCoreService;

@Service
@Transactional("coreTransactionManager")
public class UserDevicesSharedNetworkService extends AbstractCoreService<Integer,UserDevicesSharedNetwork,UserDevicesSharedNetworkDao>{
	
	@Resource
	@Override
	public void setEntityDao(UserDevicesSharedNetworkDao userDevicesSharedNetworkDao) {
		super.setEntityDao(userDevicesSharedNetworkDao);
	}
}
