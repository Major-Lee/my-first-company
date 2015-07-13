package com.bhu.vas.business.asyn.spring.model;

import com.bhu.vas.business.asyn.spring.builder.ActionDTO;
import com.bhu.vas.business.asyn.spring.builder.ActionMessageType;

public class WifiDeviceSpeedFetchDTO extends ActionDTO {
	//测速类型
	private int type;
	
	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	@Override
	public String getActionType() {
		return ActionMessageType.WifiDeviceSpeedFetch.getPrefix();
	}
}
