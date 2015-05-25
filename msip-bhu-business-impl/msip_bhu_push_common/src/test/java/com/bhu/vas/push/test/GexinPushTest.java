package com.bhu.vas.push.test;

import com.bhu.vas.push.common.dto.PushMsg;
import com.bhu.vas.push.common.service.PushService;
import com.bhu.vas.push.common.service.gexin.GexinPushService;

public class GexinPushTest {
	
	public static void main(String[] args){
		PushService pushService = GexinPushService.getInstance();
		PushMsg pushMsg = new PushMsg();
		pushMsg.setD("R");
		pushMsg.setDt("dadsadsad");
		pushMsg.setPaylod("test");
		pushService.pushTransmission(pushMsg);
	}
}
