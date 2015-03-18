package com.bhu.vas.business.logger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * 移动设备接入wifi设备的流水log
 * @author tangzichao
 *
 */
public class BusinessWifiHandsetRelationFlowLogger {
	private static final Logger logger = LoggerFactory.getLogger(BusinessWifiHandsetRelationFlowLogger.class);

	
	public static void doFlowMessageLog(String messagejson){
		logger.info(messagejson);
	}
}
