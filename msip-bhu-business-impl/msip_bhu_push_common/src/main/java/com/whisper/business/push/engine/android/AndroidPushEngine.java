package com.whisper.business.push.engine.android;

import java.util.ArrayList;
import java.util.List;

import com.gexin.rp.sdk.template.TransmissionTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gexin.rp.sdk.base.IIGtPush;
import com.gexin.rp.sdk.base.IPushResult;
import com.gexin.rp.sdk.base.impl.ListMessage;
import com.gexin.rp.sdk.base.impl.Target;
import com.gexin.rp.sdk.http.IGtPush;
import com.gexin.rp.sdk.template.NotificationTemplate;
import com.smartwork.msip.business.dto.push.PushDTO;
import com.smartwork.msip.business.dto.push.PushMessageDTO;
import com.smartwork.msip.business.runtimeconf.RuntimeConfiguration;
import com.smartwork.msip.cores.helper.StringHelper;


public class AndroidPushEngine {
	private static final Logger logger = LoggerFactory.getLogger(AndroidPushEngine.class);
	private static final IIGtPush push = new IGtPush(RuntimeConfiguration.AndroidPushXmApi, RuntimeConfiguration.AndroidPushXmAppKey, 
			RuntimeConfiguration.AndroidPushXmMasterSecret);
	
	private static class ServiceHolder{ 
		private static AndroidPushEngine instance =new AndroidPushEngine(); 
	}
	/**
	 * 获取工厂单例
	 * @return
	 */
	public static AndroidPushEngine getInstance() { 
		return ServiceHolder.instance; 
	}
	
	private AndroidPushEngine(){

	}
	
	public void doPush(PushDTO pushDto){
		if(StringHelper.isEmpty(pushDto.getDt())) return;
		
		logger.info(String.format("start android push[%s]", pushDto.getDt()));

		PushMessageDTO push_message_dto = pushDto.getMessage();
		if(push_message_dto == null) return;

        if (pushDto.getMessage().isNewFormat()) {
            pushPayload(pushDto);
        } else {
            pushNotification(pushDto);
        }

        logger.info(String.format("end android push[%s]", pushDto.getDt()));
	}

    private void pushPayload(PushDTO pushDto) {
        try {
            PushMessageDTO push_message_dto = pushDto.getMessage();
            ListMessage message = new ListMessage(); //通知模版：支持TransmissionTemplate、LinkTemplate、NotificationTemplate，此处以NotificationTemplate为例
            TransmissionTemplate template = new TransmissionTemplate();
            template.setAppId(RuntimeConfiguration.AndroidPushXmAppID); //应用APPID
            template.setAppkey(RuntimeConfiguration.AndroidPushXmAppKey); //应用APPKEY

            if(push_message_dto.getPayload() != null){
                template.setTransmissionContent(push_message_dto.getPayload());
            }
            template.setTransmissionType(2); // 收到消息是否立即启动应用，1为立即启动，2则广播等待客户端自启动

            message.setData(template);
            message.setOffline(true); //用户当前不在线时，是否离线存储，可选，默认不存储
            message.setOfflineExpireTime(RuntimeConfiguration.AndroidPushXmTimelive); //离线有效时间，单位为毫秒，可选
            // 接收者
            List<Target> targets = new ArrayList<Target>();
            Target target = new Target();
            target.setAppId(RuntimeConfiguration.AndroidPushXmAppID); //接收者安装的应用的APPID
            target.setClientId(pushDto.getDt()); //接收者的ClientID
            targets.add(target);

            String contentId = push.getContentId(message);
            IPushResult ret = push.pushMessageToList(contentId, targets);

            System.out.println("gtpush payload response : " + ret.getResponse().toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void pushNotification(PushDTO pushDto) {
        try {
            PushMessageDTO push_message_dto = pushDto.getMessage();
            ListMessage message = new ListMessage(); //通知模版：支持TransmissionTemplate、LinkTemplate、NotificationTemplate，此处以NotificationTemplate为例
            NotificationTemplate template = new NotificationTemplate();
            //TransmissionTemplate template = new TransmissionTemplate();
            template.setAppId(RuntimeConfiguration.AndroidPushXmAppID); //应用APPID
            template.setAppkey(RuntimeConfiguration.AndroidPushXmAppKey); //应用APPKEY
            template.setTitle("blink"); // 通知标题
            template.setText(push_message_dto.getShow());//通知内容
            if(push_message_dto.getPayload() != null){
                template.setTransmissionContent(push_message_dto.getPayload());
            }
            template.setTransmissionType(1); // 收到消息是否立即启动应用，1为立即启动，2则广播等待客户端自启动
            //template.setLogo("push.png"); // 通知图标，需要客户端开发时嵌入
            //template.setIsRing(false); // 收到通知是否响铃，可选，默认响铃
            //template.setIsVibrate(false); // 收到通知是否震动，可选，默认振动
            // template.setIsClearable(true); // 通知是否可清除，可选，默认可清除
            message.setData(template);
            message.setOffline(true); //用户当前不在线时，是否离线存储，可选，默认不存储
            message.setOfflineExpireTime(RuntimeConfiguration.AndroidPushXmTimelive); //离线有效时间，单位为毫秒，可选
            // 接收者
            List<Target> targets = new ArrayList<Target>();
//			for(String device_token : device_tokens){
//				Target target = new Target();
//				target.setAppId(RuntimeConfiguration.AndroidPushXmAppID); //接收者安装的应用的APPID
//				target.setClientId(device_token); //接收者的ClientID
//				targets.add(target);
//			}
            Target target = new Target();
            target.setAppId(RuntimeConfiguration.AndroidPushXmAppID); //接收者安装的应用的APPID
            target.setClientId(pushDto.getDt()); //接收者的ClientID
            targets.add(target);

            String contentId = push.getContentId(message);
            IPushResult ret = push.pushMessageToList(contentId, targets);

            System.out.println("gtpush response : " + ret.getResponse().toString());
         } catch (Exception e) {
            e.printStackTrace();
         }
    }

}
