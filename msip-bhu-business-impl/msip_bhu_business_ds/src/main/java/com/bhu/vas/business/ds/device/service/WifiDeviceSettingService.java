package com.bhu.vas.business.ds.device.service;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bhu.vas.api.rpc.devices.model.WifiDeviceSetting;
import com.bhu.vas.business.ds.device.dao.WifiDeviceSettingDao;
import com.smartwork.msip.business.abstractmsd.service.AbstractCoreService;

@Service
@Transactional("coreTransactionManager")
public class WifiDeviceSettingService extends AbstractCoreService<String,WifiDeviceSetting,WifiDeviceSettingDao>{
	
	@Resource
	@Override
	public void setEntityDao(WifiDeviceSettingDao wifiDeviceSettingDao) {
		super.setEntityDao(wifiDeviceSettingDao);
	}

}
