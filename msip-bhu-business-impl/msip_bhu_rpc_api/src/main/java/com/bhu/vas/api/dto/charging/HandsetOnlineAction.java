package com.bhu.vas.api.dto.charging;

public class HandsetOnlineAction extends ChargingAction{
	private String hmac;

	public String getHmac() {
		return hmac;
	}

	public void setHmac(String hmac) {
		this.hmac = hmac;
	}

	@Override
	public String getAct() {
		return ActionBuilder.ActionMode.HandsetOnline.getPrefix();
	}
	
}
