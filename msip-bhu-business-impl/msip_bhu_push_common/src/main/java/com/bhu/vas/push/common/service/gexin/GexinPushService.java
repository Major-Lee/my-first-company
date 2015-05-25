package com.bhu.vas.push.common.service.gexin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import com.bhu.vas.push.common.dto.PushMsg;
import com.bhu.vas.push.common.service.PushService;
import com.gexin.rp.sdk.base.IIGtPush;
import com.gexin.rp.sdk.base.IPushResult;
import com.gexin.rp.sdk.base.impl.SingleMessage;
import com.gexin.rp.sdk.base.impl.Target;
import com.gexin.rp.sdk.http.IGtPush;
import com.gexin.rp.sdk.template.TransmissionTemplate;
import com.smartwork.msip.business.runtimeconf.RuntimeConfiguration;


public class GexinPushService implements PushService{
	private static final Logger logger = LoggerFactory.getLogger(GexinPushService.class);
	private static final IIGtPush push = new IGtPush(RuntimeConfiguration.GexinPushXmApi, RuntimeConfiguration.GexinPushXmAppKey, 
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
	@Override
	public boolean pushTransmission(PushMsg pushMsg) {
		try{
			if(validatePushTransmission(pushMsg)){
		        TransmissionTemplate template = new TransmissionTemplate();
		        template.setAppId(RuntimeConfiguration.GexinPushXmAppID); //应用APPID
		        template.setAppkey(RuntimeConfiguration.GexinPushXmAppKey); //应用APPKEY
		        // 收到消息是否立即启动应用，1为立即启动，2则广播等待客户端自启动
				template.setTransmissionType(2);
				template.setTransmissionContent(pushMsg.getPaylod());
		        
		        SingleMessage message = new SingleMessage();
		        message.setOffline(true);
		        //离线有效时间，单位为毫秒，可选
		        message.setOfflineExpireTime(RuntimeConfiguration.GexinPushXmTimelive);
		        message.setData(template);
		        //可选。判断是否客户端是否wifi环境下推送，1为在WIFI环境下，0为不限制网络环境。
		        //message.setPushNetWorkType(0); 
		        
		        Target target = new Target();
		        target.setAppId(RuntimeConfiguration.GexinPushXmAppID);
		        target.setClientId(pushMsg.getDt());
		        //用户别名推送，cid和用户别名只能2者选其一
		        //target.setAlias(alias);
		        IPushResult ret = push.pushMessageToSingle(message, target);
		        if(ret != null){
		        	String retResponse = ret.getResponse().toString();
		        	System.out.println(retResponse);
		        }
		        return true;
			}
		}catch(Exception ex){
			ex.printStackTrace();
			logger.error("Gexin Push Transmission Excetion : " + ex.getMessage(), ex);
		}
		return false;
	}
	
	/**
	 * 验证透传消息
	 * @param pushMsg
	 * @return
	 */
	public boolean validatePushTransmission(PushMsg pushMsg){
		if(pushMsg == null) {
			logger.info("Gexin Validate Push Transmission is empty");
			return false;
		}
		if(StringUtils.isEmpty(pushMsg.getD()) || StringUtils.isEmpty(pushMsg.getDt())
				|| StringUtils.isEmpty(pushMsg.getPaylod())){
			logger.info("Gexin Validate Push Transmission ilegal");
			return false;
		}
		return true;
	}

}
