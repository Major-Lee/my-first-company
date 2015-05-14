package com.bhu.vas.business.asyn.spring.model;

import com.bhu.vas.business.asyn.spring.builder.ActionDTO;
import com.bhu.vas.business.asyn.spring.builder.ActionMessageType;

public class UserSignedonDTO extends ActionDTO {
	private String remoteip;
	//Device
	private String d;
	
	public String getRemoteip() {
		return remoteip;
	}


	public void setRemoteip(String remoteip) {
		this.remoteip = remoteip;
	}

	public String getD() {
		return d;
	}


	public void setD(String d) {
		this.d = d;
	}

	@Override
	public String getActionType() {
		return ActionMessageType.USERSIGNEDON.getPrefix();
	}
}
