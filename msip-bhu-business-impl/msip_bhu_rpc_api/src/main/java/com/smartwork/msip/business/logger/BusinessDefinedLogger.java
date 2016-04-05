package com.smartwork.msip.business.logger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BusinessDefinedLogger {
	private static final Logger logger = LoggerFactory.getLogger(BusinessDefinedLogger.class);

	public static void doInfoLog(String content){
		logger.info(content);
	}
}
