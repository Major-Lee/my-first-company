package com.bhu.vas.api.rpc.message.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.smartwork.msip.cores.helper.JsonHelper;

@SuppressWarnings("serial")
public class TimCustomMsgContentDTO implements java.io.Serializable{
	@JsonProperty("Data")
	private String data;
	
	@JsonProperty("Desc")
	private String desc;
	
	@JsonProperty("Ext")
	private String ext;
	
	@JsonProperty("Sound")
	private String sound;
	
	

	public String getData() {
		return data;
	}



	public void setData(String data) {
		this.data = data;
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



	public static TimCustomMsgContentDTO builder(TimCustomMsgDefaultDataDTO data){
		TimCustomMsgContentDTO dto  = new TimCustomMsgContentDTO();
		dto.setDesc(data.getDesc());
		dto.setData(JsonHelper.getJSONString(data));
		return dto;
	}
}
