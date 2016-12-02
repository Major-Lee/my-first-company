package com.bhu.vas.api.rpc.message.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

@SuppressWarnings("serial")
public class TimPushTextMsgContentDTO implements java.io.Serializable{
	@JsonProperty("Text")
	private String text;

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
	
	public static TimPushTextMsgContentDTO buildTimPushTextMsgContentDTO(String text){
		TimPushTextMsgContentDTO dto = new TimPushTextMsgContentDTO();
		dto.setText(text);
		return dto;
	}
}
