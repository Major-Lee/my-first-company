package com.bhu.vas.business.ds.device.facade;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.bhu.vas.business.ds.device.service.WifiDeviceGroupRelationService;
import com.bhu.vas.business.ds.device.service.WifiDeviceGroupService;
import com.bhu.vas.business.ds.device.service.WifiDeviceService;

@Service
public class DeviceUpgradeFacadeService {
	@Resource
	private WifiDeviceService wifiDeviceService;
	
	@Resource
	private WifiDeviceGroupService wifiDeviceGroupService;

	@Resource
	private WifiDeviceGroupRelationService wifiDeviceGroupRelationService;
}
