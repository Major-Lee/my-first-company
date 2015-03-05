package com.smartwork.multiplexer.spi;

import com.smartwork.im.message.Message;
import com.smartwork.im.message.MessageType;
import com.smartwork.msip.cores.helper.JsonHelper;


public class MessageDeliverFailedProcessor4Cm {
	/**
	 * 1.Deliverer to use when a stanza received from a client failed to be forwarded to the server.
	 * 2.Sends notification to the main server that delivery of a stanza to a client has failed.
	 * 处理方式，存入to的离线消息空间中
	 * @param stanza
	 */
	public static void handler(String stanza){
		Message message = JsonHelper.getDTO(stanza, Message.class);
		MessageType mtype = MessageType.getByDisplay(message.getMt());
    	if(mtype == null) {
    		System.out.println("unsupported message type:"+ stanza);
    		return;
    	}
    	
    	
    	
	}
}
