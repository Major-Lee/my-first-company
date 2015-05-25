package com.bhu.vas.push.common.dto;

public class PushMsg {
	private Integer uid;
	//app device token
	private String dt;
	//app device type
	private String d;
	//push show content
	private String show;
	//push payload 
	private String paylod;
	//push sound
	private String sound;
	
	public Integer getUid() {
		return uid;
	}
	public void setUid(Integer uid) {
		this.uid = uid;
	}
	public String getDt() {
		return dt;
	}
	public void setDt(String dt) {
		this.dt = dt;
	}
	public String getD() {
		return d;
	}
	public void setD(String d) {
		this.d = d;
	}
	public String getShow() {
		return show;
	}
	public void setShow(String show) {
		this.show = show;
	}
	public String getPaylod() {
		return paylod;
	}
	public void setPaylod(String paylod) {
		this.paylod = paylod;
	}
	public String getSound() {
		return sound;
	}
	public void setSound(String sound) {
		this.sound = sound;
	}
}
