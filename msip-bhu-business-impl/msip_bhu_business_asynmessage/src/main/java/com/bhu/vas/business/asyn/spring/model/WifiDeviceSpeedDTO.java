package com.bhu.vas.business.asyn.spring.model;

import com.bhu.vas.business.asyn.spring.builder.ActionDTO;
import com.bhu.vas.business.asyn.spring.builder.ActionMessageType;

public class WifiDeviceSpeedDTO extends ActionDTO {

	@Override
	public String getActionType() {
		return ActionMessageType.WifiDeviceSpeed.getPrefix();
	}
}
