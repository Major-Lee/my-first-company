package com.bhu.vas.business.ds.wifi.facade;


import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.bhu.vas.business.ds.device.service.WifiDeviceService;
import com.bhu.vas.business.ds.wifi.service.SsidInfoService;

@Service
public class SsidInfoFacadeService {
	private final Logger logger = LoggerFactory.getLogger(SsidInfoFacadeService.class);
	
	@Resource
	private SsidInfoService ssidInfoService;
	
	@Resource
	private WifiDeviceService wifiDeviceService;
	

	
}
