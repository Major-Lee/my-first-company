package com.bhu.vas.api.dto.charging;

public class HandsetAuthorizeAction extends ChargingAction{
	private String hmac;
	private String vapname;
	private String authorized;
	
	public String getHmac() {
		return hmac;
	}
	public void setHmac(String hmac) {
		this.hmac = hmac;
	}
	
	public String getAuthorized() {
		return authorized;
	}
	public void setAuthorized(String authorized) {
		this.authorized = authorized;
	}
	@Override
	public String getAct() {
		return ActionBuilder.ActionMode.HandsetAuthorize.getPrefix();
	}
	public String getVapname() {
		return vapname;
	}
	public void setVapname(String vapname) {
		this.vapname = vapname;
	}
	
	
	
}
