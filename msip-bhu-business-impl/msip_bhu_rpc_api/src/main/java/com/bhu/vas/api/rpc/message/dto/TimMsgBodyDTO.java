package com.bhu.vas.api.rpc.message.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

@SuppressWarnings("serial")
public class TimMsgBodyDTO<T> implements java.io.Serializable{
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
	
	public static <T> TimMsgBodyDTO<T> buildTimMsgBodyDTO(String msgType, T msgContent){
		
		TimMsgBodyDTO<T> dto = new TimMsgBodyDTO<T>();
		dto.setMsgType(msgType);
		dto.setMsgContent(msgContent);
		return dto;
	}
	
	
}
