package com.bhu.vas.business.logger;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.smartwork.msip.cores.helper.JsonHelper;
/**
 * 移动设备接入wifi设备的流水log
 * @author tangzichao
 *
 */
public class BusinessWifiHandsetRelationFlowLogger {
	private static final Logger logger = LoggerFactory.getLogger(BusinessWifiHandsetRelationFlowLogger.class);

	
	public static void doFlowMessageLog(String wifiId, String handsetId, long login_ts){
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("wifiId", wifiId);
		map.put("handsetId", handsetId);
		map.put("login_ts", login_ts);
		logger.info(JsonHelper.getJSONString(map));
	}
}
