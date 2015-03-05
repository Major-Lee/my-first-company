package com.smartwork.im.message;

import com.smartwork.msip.cores.helper.JsonHelper;

public class TestMessage {
	public static void main(String[] argv){
		
		//String ss = "null";
		Message message = MessageBuilder.builderCliSignonRequestMessage("user8","user1", "1234");
		System.out.println(JsonHelper.getJSONString(message));
		String json1 = JsonHelper.getJSONString(message);
		
		Message message1 = JsonHelper.getDTO(json1, Message.class);
		System.out.println(message1.getFrom());
		String json = "{\"mt\":\"CLSQ\",\"to\":[\"null\"],\"payload\":\"{\'pwd\':\'111111\',\'user\':\'zhoubl\'}\",\"from\":\"zhoubl\"}";
		//String msg = "{\"from\":\"user8\",\"to\":[null],\"mt\":\"ClSQ\",\"payload\":\"{\"user\":\"user8\",\"pwd\":\"1234\"}\"}"
		//String json = "{\"mt\":\"CLSQ\",\"to\":\"[null]\",\"from\":\"user8\",\"payload\":\"1234\"}";
		//String json = "{\"from\":\"user8\",\"to\":[null],\"mt\":\"ClSQ\",\"payload\":\"{\'user\':\'user8\',\'pwd\':\'1234\'}\"}";
		System.out.println(json);
		Message message2 = JsonHelper.getDTO(json, Message.class);
		System.out.println(message2.getFrom());
		System.out.println(message2.getTo());
	}
}
