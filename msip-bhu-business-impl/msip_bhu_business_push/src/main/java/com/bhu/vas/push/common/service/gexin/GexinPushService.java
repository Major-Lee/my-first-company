package com.bhu.vas.push.common.service.gexin;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import com.bhu.vas.api.rpc.user.model.PushMode;
import com.bhu.vas.push.common.dto.PushBasicMsg;
import com.bhu.vas.push.common.dto.PushMsg;
import com.gexin.rp.sdk.base.IPushResult;
import com.gexin.rp.sdk.base.impl.SingleMessage;
import com.gexin.rp.sdk.base.impl.Target;
import com.gexin.rp.sdk.http.IGtPush;
import com.gexin.rp.sdk.template.NotificationTemplate;
import com.gexin.rp.sdk.template.TransmissionTemplate;
import com.smartwork.msip.business.runtimeconf.RuntimeConfiguration;


public class GexinPushService{
	private static final Logger logger = LoggerFactory.getLogger(GexinPushService.class);
	
	private static final IGtPush official_push = new IGtPush(RuntimeConfiguration.GexinPushXmApi, RuntimeConfiguration.GexinPushXmAppKey, 
			RuntimeConfiguration.GexinPushXmMasterSecret);
	private static final IGtPush development_push = new IGtPush(RuntimeConfiguration.GexinPushXmApi, RuntimeConfiguration.GexinPushXmAppKey, 
			RuntimeConfiguration.GexinPushXmMasterSecret);
	
	private static class ServiceHolder{ 
		private static GexinPushService instance =new GexinPushService(); 
	}
	/**
	 * 获取工厂单例
	 * @return
	 */
	public static GexinPushService getInstance() { 
		return ServiceHolder.instance; 
	}
	
	private GexinPushService(){

	}
	
	/**
	 * 发送透传push消息
	 * @param pushMsg
	 */
	public boolean pushTransmission(PushMsg pushMsg) {
		try{
			if(validatePushMsg(pushMsg)){
		        TransmissionTemplate template = new TransmissionTemplate();
		        template.setAppId(RuntimeConfiguration.GexinPushXmAppID); //应用APPID
		        template.setAppkey(RuntimeConfiguration.GexinPushXmAppKey); //应用APPKEY
		        // 收到消息是否立即启动应用，1为立即启动，2则广播等待客户端自启动
				template.setTransmissionType(2);
				template.setTransmissionContent(pushMsg.getPaylod());
		        
		        SingleMessage message = new SingleMessage();
	            //message.setOffline(true); //用户当前不在线时，是否离线存储，可选，默认不存储
		        //离线有效时间，单位为毫秒，可选
		        //message.setOfflineExpireTime(RuntimeConfiguration.GexinPushXmTimelive);
		        message.setData(template);
		        //可选。判断是否客户端是否wifi环境下推送，1为在WIFI环境下，0为不限制网络环境。
		        message.setPushNetWorkType(0); 
		        
		        Target target = new Target();
		        target.setAppId(RuntimeConfiguration.GexinPushXmAppID);
		        target.setClientId(pushMsg.getDt());
		        //用户别名推送，cid和用户别名只能2者选其一
		        //target.setAlias(alias);
		        IPushResult ret = pushMessageToSingle(pushMsg.getPt(), message, target);
		        return parseResult(ret);
			}
		}catch(Exception ex){
			ex.printStackTrace();
			logger.error("Gexin Push Transmission Excetion : " + ex.getMessage(), ex);
		}
		return false;
	}
	
	
	public boolean pushNotification(PushMsg pushMsg) {
		try{
			if(validatePushMsg(pushMsg)){
				NotificationTemplate template = new NotificationTemplate();
		        template.setAppId(RuntimeConfiguration.GexinPushXmAppID); //应用APPID
		        template.setAppkey(RuntimeConfiguration.GexinPushXmAppKey); //应用APPKEY
		        // 收到消息是否立即启动应用，1为立即启动，2则广播等待客户端自启动
				template.setTransmissionType(1);
				template.setTransmissionContent(pushMsg.getPaylod());
				template.setTitle(pushMsg.getTitle());
		        template.setText(pushMsg.getText());
	            template.setLogo(pushMsg.getLogo()); // 通知图标，需要客户端开发时嵌入
	            //template.setIsRing(false); // 收到通知是否响铃，可选，默认响铃
	            //template.setIsVibrate(false); // 收到通知是否震动，可选，默认振动
	            // template.setIsClearable(true); // 通知是否可清除，可选，默认可清除
		        
		        SingleMessage message = new SingleMessage();
	            //message.setOffline(true); //用户当前不在线时，是否离线存储，可选，默认不存储
		        //离线有效时间，单位为毫秒，可选
		       // message.setOfflineExpireTime(RuntimeConfiguration.GexinPushXmTimelive);
		        message.setData(template);
		        //可选。判断是否客户端是否wifi环境下推送，1为在WIFI环境下，0为不限制网络环境。
		        message.setPushNetWorkType(0); 
		        
		        Target target = new Target();
		        target.setAppId(RuntimeConfiguration.GexinPushXmAppID);
		        target.setClientId(pushMsg.getDt());
		        //用户别名推送，cid和用户别名只能2者选其一
		        //target.setAlias(alias);
		        IPushResult ret = pushMessageToSingle(pushMsg.getPt(), message, target);
		        return parseResult(ret);
			}
			
		}catch(Exception ex){
			ex.printStackTrace();
			logger.error("Gexin Push Transmission Excetion : " + ex.getMessage(), ex);
		}
		return false;
	}
	
	public boolean pushTransmission4ios(PushMsg pushMsg){
		try{
			if(validatePushMsg(pushMsg)){
				TransmissionTemplate template = new TransmissionTemplate();
		        template.setAppId(RuntimeConfiguration.GexinPushXmAppID); //应用APPID
		        template.setAppkey(RuntimeConfiguration.GexinPushXmAppKey); //应用APPKEY
			    // 透传消息设置，1为强制启动应用，客户端接收到消息后就会立即启动应用；2为等待应用启动。
			    template.setTransmissionType(2);
			    template.setTransmissionContent(pushMsg.getPaylod());
			 
			    /*iOS 推送需要对该字段进行设置具体参数详见iOS模板说明*/
			    template.setPushInfo(pushMsg.getTitle(), pushMsg.getBadge(), pushMsg.getText(), pushMsg.getSound(), 
			    		pushMsg.getPaylod(), "", "", "1");
			    /*template.setPushInfo("actionLocKey", 4, "message", "sound", 
			    "payload", "locKey", "locArgs", "launchImage","ContentAvailable");*/
		        
		        SingleMessage message = new SingleMessage();
	            message.setOffline(true); //用户当前不在线时，是否离线存储，可选，默认不存储
		        //离线有效时间，单位为毫秒，可选
		        message.setOfflineExpireTime(RuntimeConfiguration.GexinPushXmTimelive);
		        message.setData(template);
		        //可选。判断是否客户端是否wifi环境下推送，1为在WIFI环境下，0为不限制网络环境。
		        message.setPushNetWorkType(0); 
		        
		        Target target = new Target();
		        target.setAppId(RuntimeConfiguration.GexinPushXmAppID);
		        target.setClientId(pushMsg.getDt());
		        //用户别名推送，cid和用户别名只能2者选其一
		        //target.setAlias(alias);
		        IPushResult ret = pushMessageToSingle(pushMsg.getPt(), message, target);
		        return parseResult(ret);
			}
			
		}catch(Exception ex){
			ex.printStackTrace();
			logger.error("Gexin Push Transmission Excetion : " + ex.getMessage(), ex);
		}
		return false;
	}
	
	public boolean pushNotification4ios(PushMsg pushMsg){
		try{
			if(validatePushMsg(pushMsg)){
				TransmissionTemplate template = new TransmissionTemplate();
		        template.setAppId(RuntimeConfiguration.GexinPushXmAppID); //应用APPID
		        template.setAppkey(RuntimeConfiguration.GexinPushXmAppKey); //应用APPKEY
			    // 透传消息设置，1为强制启动应用，客户端接收到消息后就会立即启动应用；2为等待应用启动。
			    template.setTransmissionType(2);
			    template.setTransmissionContent(pushMsg.getPaylod());
			 
			    /*iOS 推送需要对该字段进行设置具体参数详见iOS模板说明*/
			    template.setPushInfo(pushMsg.getTitle(), pushMsg.getBadge(), pushMsg.getText(), pushMsg.getSound(), 
			    		pushMsg.getPaylod(), "", "", "");
			    /*template.setPushInfo("actionLocKey", 4, "message", "sound", 
			    "payload", "locKey", "locArgs", "launchImage","ContentAvailable");*/
		        
		        SingleMessage message = new SingleMessage();
	            message.setOffline(true); //用户当前不在线时，是否离线存储，可选，默认不存储
		        //离线有效时间，单位为毫秒，可选
		        message.setOfflineExpireTime(RuntimeConfiguration.GexinPushXmTimelive);
		        message.setData(template);
		        //可选。判断是否客户端是否wifi环境下推送，1为在WIFI环境下，0为不限制网络环境。
		        message.setPushNetWorkType(0); 
		        
		        Target target = new Target();
		        target.setAppId(RuntimeConfiguration.GexinPushXmAppID);
		        target.setClientId(pushMsg.getDt());
		        //用户别名推送，cid和用户别名只能2者选其一
		        //target.setAlias(alias);
		        IPushResult ret = pushMessageToSingle(pushMsg.getPt(), message, target);
		        return parseResult(ret);
			}
			
		}catch(Exception ex){
			ex.printStackTrace();
			logger.error("Gexin Push Transmission Excetion : " + ex.getMessage(), ex);
		}
		return false;
	}
	
	protected IPushResult pushMessageToSingle(String pt, SingleMessage message, Target target){
        IPushResult ret = null;
        if(PushMode.isOfficial(pt)){
        	ret = official_push.pushMessageToSingle(message, target);
        }else{
        	ret = development_push.pushMessageToSingle(message, target);
        }
        return ret;
	}
	
	/**
	 * 验证push消息基本数据
	 * @param pushMsg
	 * @return
	 */
	public boolean validatePushMsg(PushBasicMsg pushBasicMsg){
		if(pushBasicMsg == null) {
			logger.info("Gexin Validate Push msg is empty");
			return false;
		}
		if(StringUtils.isEmpty(pushBasicMsg.getD()) || StringUtils.isEmpty(pushBasicMsg.getDt())){
			logger.info("Gexin Validate Push msg ilegal");
			return false;
		}
		return true;
	}
	
	public static final String Ret_ResultOK = "ok";
	
	protected boolean parseResult(IPushResult ret){
		if(ret == null || StringUtils.isEmpty(ret.getResponse())) {
			logger.info("Gexin Response empty");
			return false;
		}
		Map<String, Object> response = ret.getResponse();
		System.out.println(response.toString());
		Object resultObj = response.get("result");
		if(resultObj == null){
			logger.info("Gexin Response result empty");
			return false;
		}
		if(!Ret_ResultOK.equals(resultObj.toString())){
			logger.info("Gexin Response result fail : " + response.get("status"));
			return false;
		}
		return true;
	}

}
