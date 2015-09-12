package com.bhu.vas.business.logger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bhu.vas.api.dto.charging.ActionBuilder;

public class BusinessStatisticsLogger {
	private static final Logger logger = LoggerFactory.getLogger(BusinessStatisticsLogger.class);

	public static void doSimulateActionLog(String mac){
		logger.info(ActionBuilder.toJsonHasPrefix(ActionBuilder.builderDeviceSimulateOnlineAction(mac, System.currentTimeMillis())));
	}
}
