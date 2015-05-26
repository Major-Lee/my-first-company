package com.bhu.vas.push.common.dto;

import com.bhu.vas.api.rpc.user.model.PushMode;

public class PushBasicMsg {
	private Integer uid;
	//app device token
	private String dt;
	//app device type
	private String d;
	//push payload 
	private String paylod;
	//push type
	private String pt = PushMode.Official.getType();
	
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
	public String getPaylod() {
		return paylod;
	}
	public void setPaylod(String paylod) {
		this.paylod = paylod;
	}
	public String getPt() {
		return pt;
	}
	public void setPt(String pt) {
		this.pt = pt;
	}
}
