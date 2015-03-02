package com.wecite.toplines.business.logger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.smartwork.msip.cores.helper.JsonHelper;
import com.wecite.toplines.business.asyn.web.builder.ActionDTO;
import com.wecite.toplines.business.asyn.web.builder.ActionMessageFactoryBuilder;
import com.wecite.toplines.business.asyn.web.builder.DeliverMessage;
import com.wecite.toplines.business.asyn.web.builder.DeliverMessageFactoryBuilder;
import com.wecite.toplines.business.asyn.web.builder.DeliverMessageType;

public class BusinessStatisticsLogger {
	private static final Logger logger = LoggerFactory.getLogger(BusinessStatisticsLogger.class);
	/*
	public static void doSignedLog(int uid,long signedontime,long signedofftime){
		DeliverMessage message = DeliverMessageFactoryBuilder.buildDeliverMessage(DeliverMessageType.AC.getPrefix(), uid, ActionMessageFactoryBuilder.toJsonHasPrefix(new UserSignedDTO(uid,signedontime,signedofftime)));
		logger.info(DeliverMessageFactoryBuilder.toJson(message));
	}*/
	
	/*public static void doSnsShareLog(int uid,String snsto,int mid,String sharemark,long sharetime){
		DeliverMessage message = DeliverMessageFactoryBuilder.buildDeliverMessage(DeliverMessageType.AC.getPrefix(), uid, ActionMessageFactoryBuilder.toJsonHasPrefix(new UserSnsShareDTO(uid,snsto,mid,sharemark,sharetime)));
		logger.info(DeliverMessageFactoryBuilder.toJson(message));
	}*/
	
	public static void doActionMessageLog(String messagejson){
		//DeliverMessage message = DeliverMessageFactoryBuilder.buildDeliverMessage(DeliverMessageType.AC.getPrefix(), uid, ActionMessageFactoryBuilder.toJsonHasPrefix(new UserSnsShareDTO(uid,snsto,mid,sharemark,sharetime)));
		//System.out.println("+++++++++++++++ doActionMessageLog : " + json);
		logger.info(messagejson);
	}
	
	public static void doActionMessageLog(ActionDTO dto){
		DeliverMessage message = DeliverMessageFactoryBuilder.buildDeliverMessage(DeliverMessageType.AC.getPrefix(), dto.getUid(), ActionMessageFactoryBuilder.toJsonHasPrefix(dto));
		//System.out.println("+++++++++++++++ doActionMessageLog : " + json);
		logger.info(JsonHelper.getJSONString(message,false));
	}
	
	public static void main(String[] argv){
		//BusinessStatisticsLogger.doSignedLog(100097, System.currentTimeMillis(), System.currentTimeMillis());
		//BusinessStatisticsLogger.doSnsShareLog(100097, "qq",200345,"分享了。。。", System.currentTimeMillis());
	}
}
