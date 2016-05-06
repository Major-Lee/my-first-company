package com.bhu.vas.api.dto.charging;

public class HandsetOnlineAction extends ChargingAction{
	private String hmac;
	private String hname;
	private String hip;
	public String getHmac() {
		return hmac;
	}

	public void setHmac(String hmac) {
		this.hmac = hmac;
	}

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

	@Override
	public String getAct() {
		return ActionBuilder.ActionMode.HandsetOnline.getPrefix();
	}
	
}
