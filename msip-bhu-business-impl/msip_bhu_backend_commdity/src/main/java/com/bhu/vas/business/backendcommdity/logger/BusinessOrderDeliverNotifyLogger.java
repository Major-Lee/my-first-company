package com.bhu.vas.business.backendcommdity.logger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * 通知portal发货信息日志打印
 * @author tangzichao
 *
 */
public class BusinessOrderDeliverNotifyLogger {
	private static final Logger logger = LoggerFactory.getLogger(BusinessOrderDeliverNotifyLogger.class);
	private static final String Order_DeliverNotifyMessage_Template = "OrderDeliverNotifyMessage[%s]";

	public static void doActionOrderDeliverNotifyMessageLog(String messagejson){
		//DeliverMessage message = DeliverMessageFactoryBuilder.buildDeliverMessage(DeliverMessageType.AC.getPrefix(), uid, ActionMessageFactoryBuilder.toJsonHasPrefix(new UserSnsShareDTO(uid,snsto,mid,sharemark,sharetime)));
		//System.out.println("+++++++++++++++ doActionMessageLog : " + json);
		logger.info(String.format(Order_DeliverNotifyMessage_Template, messagejson));
	}
	
	
	public static void main(String[] argv){
		//BusinessStatisticsLogger.doSignedLog(100097, System.currentTimeMillis(), System.currentTimeMillis());
		//BusinessStatisticsLogger.doSnsShareLog(100097, "qq",200345,"分享了。。。", System.currentTimeMillis());
	}
}
