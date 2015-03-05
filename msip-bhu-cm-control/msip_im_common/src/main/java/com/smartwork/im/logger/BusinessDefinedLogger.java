package com.smartwork.im.logger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BusinessDefinedLogger {
	private static final Logger logger = LoggerFactory.getLogger(BusinessDefinedLogger.class);
	protected static final String CID = "C.";
	
	//后台生成的CID
	public static final String CID_ACT_GEN = "GEN";
	//客户端上传生成的CID
	public static final String CID_ACT_UP = "UP";
	private static String CID_Record_template = "%s%s %s";
	
	/**
	 * 用户喜欢壁纸的action
	 * @param uid
	 * @param rid
	 */
	public static void doCidLogger(String act,String cid){
		logger.info(String.format(CID_Record_template, CID,act,cid));
	}
}
