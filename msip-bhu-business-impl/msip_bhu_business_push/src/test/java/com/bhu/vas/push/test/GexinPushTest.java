package com.bhu.vas.push.test;

import com.bhu.vas.push.common.dto.PushMsg;
import com.bhu.vas.push.common.service.gexin.GexinPushService;

public class GexinPushTest {
	//public static final String dt = "4ecf03c19d9a87d477360f43966e89ad"; //xiong
	public static final String dt = "1ea6a3bd6d4c7829c46fb5069bf9cd87"; //lawliet
	//public static final String dt = "a0fa58123b6c4735159ba718cdf5d88c"; //ios
	
	public static void main(String[] args){
		//pushNotificationMsg();
		pushTransmissionMsg();
		//pushAPNSMsg();
	}
	
	public static void pushTransmissionMsg(){
		GexinPushService pushService = GexinPushService.getInstance();
		PushMsg pushMsg = new PushMsg();
		pushMsg.setD("R");
		pushMsg.setDt(dt);
		pushMsg.setPaylod("test");
		System.out.println(pushService.pushTransmission(pushMsg));
	}
	
	public static void pushNotificationMsg(){
		GexinPushService pushService = GexinPushService.getInstance();
		PushMsg pushMsg = new PushMsg();
		pushMsg.setD("R");
		pushMsg.setDt(dt);
		pushMsg.setTitle("title");
		pushMsg.setText("text");
		System.out.println(pushService.pushNotification(pushMsg));
	}
	
	public static void pushAPNSMsg(){
		GexinPushService pushService = GexinPushService.getInstance();
		PushMsg pushMsg = new PushMsg();
		pushMsg.setD("R");
		pushMsg.setDt(dt);
		pushMsg.setTitle("title");
		pushMsg.setText("text");
		pushMsg.setPaylod("payload");
		System.out.println(pushService.pushNotification4ios(pushMsg));
	}
}
