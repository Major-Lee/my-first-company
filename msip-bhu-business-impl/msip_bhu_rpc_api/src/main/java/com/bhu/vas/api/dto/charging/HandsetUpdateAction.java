package com.bhu.vas.api.dto.charging;

public class HandsetUpdateAction extends ChargingAction{
	private String hmac;
	private String hname;
	private String hip;
	private String mac;

	public String getHname() {
		return hname;
	}

	public void setHname(String hname) {
		this.hname = hname;
	}

	public String getHip() {
		return hip;
	}

	public void setHip(String hip) {
		this.hip = hip;
	}

	public String getMac() {
		return mac;
	}

	public void setMac(String mac) {
		this.mac = mac;
	}
   
	
	public String getHmac() {
		return hmac;
	}

	public void setHmac(String hmac) {
		this.hmac = hmac;
	}

	@Override
	public String getAct() {
		return ActionBuilder.ActionMode.HandsetUpdate.getPrefix();
	}

}
