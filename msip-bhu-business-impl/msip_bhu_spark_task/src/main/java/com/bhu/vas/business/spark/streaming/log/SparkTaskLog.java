package com.bhu.vas.business.spark.streaming.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * spark日志管理
 * @author tangzichao
 *
 */
public class SparkTaskLog {
	private static final Logger wifistasniffer_logger = LoggerFactory.getLogger("wifistasniffer");
	
	public static Logger wifistasniffer(){
		return wifistasniffer_logger;
	}
}
