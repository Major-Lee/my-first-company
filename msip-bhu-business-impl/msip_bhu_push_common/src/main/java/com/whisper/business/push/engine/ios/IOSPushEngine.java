package com.whisper.business.push.engine.ios;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.notnoop.apns.APNS;
import com.notnoop.apns.ApnsDelegateAdapter;
import com.notnoop.apns.ApnsNotification;
import com.notnoop.apns.ApnsService;
import com.notnoop.apns.ApnsServiceBuilder;
import com.notnoop.apns.DeliveryError;
import com.notnoop.apns.PayloadBuilder;
import com.smartwork.msip.business.dto.push.PushDTO;
import com.smartwork.msip.business.dto.push.PushMessageDTO;
import com.smartwork.msip.business.runtimeconf.RuntimeConfiguration;
import com.smartwork.msip.cores.helper.StringHelper;
import com.whisper.api.user.model.PushEnum;


public class IOSPushEngine {
	private static final Logger logger = LoggerFactory.getLogger(IOSPushEngine.class);
	private static class ServiceHolder{ 
		private static IOSPushEngine instance =new IOSPushEngine(); 
	}
	/**
	 * 获取工厂单例
	 * @return
	 */
	public static IOSPushEngine getInstance() { 
		return ServiceHolder.instance; 
	}
	
//	private String keystore;
//	private String password;
//	private int threads;
//	/*private String newkeystore;
//	private String newpassword;*/
//	private boolean production;
	
	private ApnsService storeNotificationServer;
	private ApnsService productionNotificationServer;
	
	//private AppleNotificationServer notificationNewServer;
	private IOSPushEngine(){
		initNotificationServers();
	}
	/**
	 * 初始化所有的pushservice连接
	 */
	public void initNotificationServers(){
		initStoreNotificationServer();
		initProductionNotificationServer();
	}
	
	public ApnsService buildNotificationServer(String key, String password){
		//建立与Apple服务器连接 
		ApnsServiceBuilder builder = APNS.newService();
		builder.withCert(key, password)
	    	//.withSandboxDestination()
	    	.withDelegate(new IosApnsDelegateAdapter())
	    	.asPool(RuntimeConfiguration.IosPushThreads);
		
		if(RuntimeConfiguration.IosPushProduction){
			builder.withProductionDestination();
		}else{
			builder.withSandboxDestination();
		}
		return builder.build();
	}
	
	public void initStoreNotificationServer(){
		storeNotificationServer = this.buildNotificationServer(RuntimeConfiguration.IosPushKeystore, RuntimeConfiguration.IosPushPassword);
	}
	
	public void initProductionNotificationServer(){
		productionNotificationServer = this.buildNotificationServer(RuntimeConfiguration.IosPushKeyproduction, RuntimeConfiguration.IosPushPassword);
	}
	
	public void doPush(PushDTO pushDto){
		try{
			if(StringHelper.isEmpty(pushDto.getDt())) return;
			
			logger.info(String.format("start ios push[%s] [%s]", pushDto.getDt(), pushDto.getPt()));
			
			//PushMessageDTO push_message_dto = pushDto.getMessage();
			//if(push_message_dto == null) return;
			
			//String business_type = push_message_dto.getT();
			//String push_message = null;
			
			//聊天消息发送支持后台运行的push消息
			/*if(PushActionMessageType.Push_NotifyUserChatMediaActionMessage.getDisplay().equals(business_type)){
				push_message = buildSecretMessage(push_message_dto);
			}
			else{
				push_message = buildMessage(push_message_dto);
			}*/
			
			String  push_message = buildMessage(pushDto);
			System.out.println(push_message);
			if(push_message != null){
				//ApnsNotification apnsNotification = getAppService(pushDto.getPt()).push(pushDto.getDt(), push_message);
				this.getAppService(pushDto.getPt()).push(pushDto.getDt(), push_message);
			}
//		    StringBuffer sendIdentifiers = new StringBuffer();
//		    for(ApnsNotification apnsNotification : apnsNotifications){
//		    	sendIdentifiers.append(apnsNotification.getIdentifier());
//		    	sendIdentifiers.append(StringHelper.COMMA_STRING_GAP);
//		    }
		    //System.out.println(apnsNotification.getIdentifier());
		    logger.info(String.format("end ios push[%s] [%s]", pushDto.getDt(), pushDto.getPt()));
		}catch(Exception ex){
			ex.printStackTrace(System.out);
		}
	}
	/**
	 * 根据push type选择service进行发送
	 * @param pt
	 * @return
	 */
	public ApnsService getAppService(String pt){
		if(PushEnum.isStore(pt)){
			return storeNotificationServer;
		}else{
			return productionNotificationServer;
		}
	}
	
//	public String buildMessage(PushMessageDTO push_message_dto){
//	    return APNS.newPayload()
//	    		.badge(1)
//	    		.sound("default")
//	    		//.customField("secret", business_payload)
//	    		.alertTitle("shijiehexin")
//	    		.alertBody(push_message_dto.getShow())
//	    		.alertContentAvailable(1)
////	    		.customField("t", push_message_dto.getT())
//	    		.build();
//	}
	
	public String buildMessage(PushDTO pushDto){
		PushMessageDTO push_message_dto = pushDto.getMessage();
		if(push_message_dto == null) return null;
		
		PayloadBuilder APNSBuilder = APNS.newPayload();
		APNSBuilder.badge(pushDto.getCurrent_count())
			//.sound("default")
			.sound(push_message_dto.getSound())
			.alertTitle("shijiehexin")
			.alertBody(push_message_dto.getShow());
		
		if(push_message_dto.getPayload() != null){
			APNSBuilder.customField("payload", push_message_dto.getPayload());
		}
		return APNSBuilder.build();
		
/*	    return APNS.newPayload()
	    		.badge(pushDto.getCurrent_count())
	    		//.sound("default")
	    		.sound("push.caf")
	    		.customField("payload", push_message_dto.getPayload())
	    		.alertTitle("shijiehexin")
	    		.alertBody(push_message_dto.getShow());
//	    		.customField("t", push_message_dto.getT())
	    		.build();*/
	}
	
//	public String buildMessage(PushMessageDTO push_message_dto){
//	    return APNS.newPayload()
//	    		.badge(1)
//	    		.sound("default")
//	    		//.customField("secret", business_payload)
//	    		.alertTitle("shijiehexin")
//	    		.alertBody(push_message_dto.getShow())
////	    		.customField("t", push_message_dto.getT())
//	    		.build();
//	}
//	
//	public String buildSecretMessage(PushMessageDTO push_message_dto){
//	    return APNS.newPayload()
//	    		.badge(1)
//	    		//.sound("default")
//	    		.sound("push.caf")
//	    		//.customField("secret", business_payload)
//	    		.alertTitle("shijiehexin")
//	    		.alertBody(push_message_dto.getShow())
//	    		.alertContentAvailable(1)
////	    		.customField("t", push_message_dto.getT())
//	    		.build();
//	}
	
	class IosApnsDelegateAdapter extends ApnsDelegateAdapter{
	    public void messageSent(ApnsNotification message, boolean resent) {
	    	System.out.println("messageSent : " + message.getIdentifier());
	    }

	    public void messageSendFailed(ApnsNotification message, Throwable e) {
	    	System.out.println("messageSendFailed : " + message.getIdentifier() + " : " + e.getMessage());
	    }

	    public void connectionClosed(DeliveryError e, int messageIdentifier) {
	    }

	    public void cacheLengthExceeded(int newCacheLength) {
	    }

	    public void notificationsResent(int resendCount) {
	    }
	}
}
