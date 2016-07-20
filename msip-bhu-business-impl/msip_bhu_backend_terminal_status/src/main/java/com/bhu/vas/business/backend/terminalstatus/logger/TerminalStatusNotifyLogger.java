package com.bhu.vas.business.backend.terminalstatus.logger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.smartwork.msip.cores.helper.DateHelper;
import com.smartwork.msip.cores.helper.DateTimeHelper;
/**
 * 通知设备打印日志
 * @author PengYu Zhang
 *
 */
public class TerminalStatusNotifyLogger {
	private static final Logger logger = LoggerFactory.getLogger(TerminalStatusNotifyLogger.class);
	private static final String Terminal_Status_Notify_Logger_Template = "%s";
	//static int  count = 0;
	public static void doTerminalStatusMessageLog(String messagejson){
		//count++;
		//System.out.println(DateTimeHelper.getDateTime()+"   "+count);
		//DeliverMessage message = DeliverMessageFactoryBuilder.buildDeliverMessage(DeliverMessageType.AC.getPrefix(), uid, ActionMessageFactoryBuilder.toJsonHasPrefix(new UserSnsShareDTO(uid,snsto,mid,sharemark,sharetime)));
		logger.info(String.format(Terminal_Status_Notify_Logger_Template, messagejson));
	}
	
	
	public static void main(String[] argv){
		TerminalStatusNotifyLogger.doTerminalStatusMessageLog("qqqqqqqqqqqqqqq");
		//BusinessStatisticsLogger.doSignedLog(100097, System.currentTimeMillis(), System.currentTimeMillis());
		//BusinessStatisticsLogger.doSnsShareLog(100097, "qq",200345,"分享了。。。", System.currentTimeMillis());
	}
}
