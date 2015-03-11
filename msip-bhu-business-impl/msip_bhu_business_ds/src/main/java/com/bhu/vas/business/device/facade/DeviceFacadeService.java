package com.bhu.vas.business.device.facade;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.bhu.vas.business.device.service.WifiDeviceService;

@Service
public class DeviceFacadeService {
	@Resource
	private WifiDeviceService wifiDeviceService;
}
