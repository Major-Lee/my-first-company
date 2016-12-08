package com.bhu.vas.api.rpc.message.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

@SuppressWarnings("serial")
public class TimUserBasicDTO implements java.io.Serializable{
	@JsonProperty("Identifier")
	private String identifier;
	
	@JsonProperty("Nick")
	@JsonInclude(Include.NON_NULL)
	private String nick;
	
	@JsonProperty("FaceUrl")
	@JsonInclude(Include.NON_NULL)
	private String faceUrl;
	
	public String getIdentifier() {
		return identifier;
	}
	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}
	public String getNick() {
		return nick;
	}
	public void setNick(String nick) {
		this.nick = nick;
	}
	public String getFaceUrl() {
		return faceUrl;
	}
	public void setFaceUrl(String faceUrl) {
		this.faceUrl = faceUrl;
	}
}
