package com.bhu.vas.business.ds.device.service;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bhu.vas.api.rpc.devices.model.UserDevicesSharedNetworks;
import com.bhu.vas.business.ds.device.dao.UserDevicesSharedNetworksDao;
import com.smartwork.msip.business.abstractmsd.service.AbstractCoreService;

@Service
@Transactional("coreTransactionManager")
public class UserDevicesSharedNetworksService extends AbstractCoreService<Integer,UserDevicesSharedNetworks,UserDevicesSharedNetworksDao>{
	
	@Resource
	@Override
	public void setEntityDao(UserDevicesSharedNetworksDao userDevicesSharedNetworksDao) {
		super.setEntityDao(userDevicesSharedNetworksDao);
	}
}
