package com.bhu.vas.business.ds.devicegroup.service;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bhu.vas.api.rpc.devicegroup.model.WifiDeviceBackendTask;
import com.bhu.vas.business.ds.devicegroup.dao.WifiDeviceBackendTaskDao;
import com.smartwork.msip.business.abstractmsd.service.AbstractCoreService;


@Service
@Transactional("coreTransactionManager")
public class WifiDeviceBackendTaskService extends AbstractCoreService<Long, WifiDeviceBackendTask, WifiDeviceBackendTaskDao> {
	
	@Resource
	@Override
	public void setEntityDao(WifiDeviceBackendTaskDao wifiDeviceBackendTaskDao) {
		super.setEntityDao(wifiDeviceBackendTaskDao);
	}
	
}
