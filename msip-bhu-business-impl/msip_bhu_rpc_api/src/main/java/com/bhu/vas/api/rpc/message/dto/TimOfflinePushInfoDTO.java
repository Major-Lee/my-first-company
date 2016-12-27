package com.bhu.vas.api.rpc.message.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@SuppressWarnings("serial")
public class TimOfflinePushInfoDTO implements java.io.Serializable{
	@JsonProperty("PushFlag")
	private int pushFlag;
	@JsonProperty("Desc")
	@JsonInclude(Include.NON_NULL)
	private String desc;
	@JsonProperty("Ext")
	@JsonInclude(Include.NON_NULL)
	private String ext;
	@JsonProperty("Sound")
	@JsonInclude(Include.NON_NULL)
	private String sound;
	public int getPushFlag() {
		return pushFlag;
	}
	public void setPushFlag(int pushFlag) {
		this.pushFlag = pushFlag;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	public String getExt() {
		return ext;
	}
	public void setExt(String ext) {
		this.ext = ext;
	}
	public String getSound() {
		return sound;
	}
	public void setSound(String sound) {
		this.sound = sound;
	}
	
	public static TimOfflinePushInfoDTO builder(String ext){
		TimOfflinePushInfoDTO dto = new TimOfflinePushInfoDTO();
		dto.setExt(ext);
		return dto;
	}
	
}
