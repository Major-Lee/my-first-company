package com.bhu.vas.business.logger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BusinessStatisticsLogger {
	private static final Logger logger = LoggerFactory.getLogger(BusinessStatisticsLogger.class);
	private static final String DynaQueue_Recv_Template = "DynaQueue Recv:%s";
	private static final String Queue_Recv_Template = "Queue Recv:%s";
	private static final String Topic_Recv_Template = "Topic Recv:%s";
	/*
	public static void doSignedLog(int uid,long signedontime,long signedofftime){
		DeliverMessage message = DeliverMessageFactoryBuilder.buildDeliverMessage(DeliverMessageType.AC.getPrefix(), uid, ActionMessageFactoryBuilder.toJsonHasPrefix(new UserSignedDTO(uid,signedontime,signedofftime)));
		logger.info(DeliverMessageFactoryBuilder.toJson(message));
	}*/
	
	/*public static void doSnsShareLog(int uid,String snsto,int mid,String sharemark,long sharetime){
		DeliverMessage message = DeliverMessageFactoryBuilder.buildDeliverMessage(DeliverMessageType.AC.getPrefix(), uid, ActionMessageFactoryBuilder.toJsonHasPrefix(new UserSnsShareDTO(uid,snsto,mid,sharemark,sharetime)));
		logger.info(DeliverMessageFactoryBuilder.toJson(message));
	}*/
	public static void doActionDynaQueueMessageLog(String messagejson){
		//DeliverMessage message = DeliverMessageFactoryBuilder.buildDeliverMessage(DeliverMessageType.AC.getPrefix(), uid, ActionMessageFactoryBuilder.toJsonHasPrefix(new UserSnsShareDTO(uid,snsto,mid,sharemark,sharetime)));
		//System.out.println("+++++++++++++++ doActionMessageLog : " + json);
		logger.info(String.format(DynaQueue_Recv_Template, messagejson));
	}
	
	public static void doActionQueueMessageLog(String messagejson){
		//DeliverMessage message = DeliverMessageFactoryBuilder.buildDeliverMessage(DeliverMessageType.AC.getPrefix(), uid, ActionMessageFactoryBuilder.toJsonHasPrefix(new UserSnsShareDTO(uid,snsto,mid,sharemark,sharetime)));
		//System.out.println("+++++++++++++++ doActionMessageLog : " + json);
		logger.info(String.format(Queue_Recv_Template, messagejson));
	}
	
	public static void doActionTopicMessageLog(String messagejson){
		//DeliverMessage message = DeliverMessageFactoryBuilder.buildDeliverMessage(DeliverMessageType.AC.getPrefix(), uid, ActionMessageFactoryBuilder.toJsonHasPrefix(new UserSnsShareDTO(uid,snsto,mid,sharemark,sharetime)));
		//System.out.println("+++++++++++++++ doActionMessageLog : " + json);
		logger.info(String.format(Topic_Recv_Template, messagejson));
	}
	/*
	public static void doActionMessageLog(ActionDTO dto){
		DeliverMessage message = DeliverMessageFactoryBuilder.buildDeliverMessage(DeliverMessageType.AC.getPrefix(), dto.getUid(), ActionMessageFactoryBuilder.toJsonHasPrefix(dto));
		//System.out.println("+++++++++++++++ doActionMessageLog : " + json);
		logger.info(JsonHelper.getJSONString(message,false));
	}*/
	
	public static void main(String[] argv){
		//BusinessStatisticsLogger.doSignedLog(100097, System.currentTimeMillis(), System.currentTimeMillis());
		//BusinessStatisticsLogger.doSnsShareLog(100097, "qq",200345,"分享了。。。", System.currentTimeMillis());
	}
}
