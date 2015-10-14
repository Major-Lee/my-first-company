package com.bhu.vas.business.asyn.spring.model;

import com.bhu.vas.business.asyn.spring.builder.ActionDTO;
import com.bhu.vas.business.asyn.spring.builder.ActionMessageType;

public class UserRegisteredDTO extends ActionDTO {
	private String mobileno;
	private String channel;
	private String d;
	private String remoteip;

	public String getD() {
		return d;
	}

	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

	public void setD(String d) {
		this.d = d;
	}

	@Override
	public String getActionType() {
		return ActionMessageType.USERREGISTERED.getPrefix();
	}

	public String getRemoteip() {
		return remoteip;
	}

	public void setRemoteip(String remoteip) {
		this.remoteip = remoteip;
	}

	public String getMobileno() {
		return mobileno;
	}

	public void setMobileno(String mobileno) {
		this.mobileno = mobileno;
	}
	
}
