package com.bhu.vas.api.rpc.message.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

@SuppressWarnings("serial")
public class TimPushMsgBodyDTO<T> implements java.io.Serializable{
	@JsonProperty("MsgType")
	private String msgType;
	
	@JsonProperty("MsgContent")
	private T msgContent;
	
	public String getMsgType() {
		return msgType;
	}
	public void setMsgType(String msgType) {
		this.msgType = msgType;
	}
	public T getMsgContent() {
		return msgContent;
	}
	public void setMsgContent(T msgContent) {
		this.msgContent = msgContent;
	}
	
	public static <T> TimPushMsgBodyDTO<T> buildTimPushMsgBodyDTO(String msgType, T msgContent){
		
		TimPushMsgBodyDTO<T> dto = new TimPushMsgBodyDTO<T>();
		dto.setMsgType(msgType);
		dto.setMsgContent(msgContent);
		return dto;
	}
	
	
}
