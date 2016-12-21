package com.bhu.vas.api.rpc.message.dto;

import com.smartwork.msip.cores.helper.JsonHelper;

@SuppressWarnings("serial")
public class MessageSystemFailTaskDTO implements java.io.Serializable{
	private String url;
	private String message;
	
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	
	public static String builder(String url, String message){
		MessageSystemFailTaskDTO dto = new MessageSystemFailTaskDTO();
		dto.setUrl(url);
		dto.setMessage(message);
		return JsonHelper.getJSONString(dto);
	}
	
}
