package com.bhu.vas.business.asyn.spring.model;

import com.bhu.vas.business.asyn.spring.builder.ActionDTO;
import com.bhu.vas.business.asyn.spring.builder.ActionMessageType;

public class WifiDeviceOfflineDTO extends ActionDTO {

	@Override
	public String getActionType() {
		return ActionMessageType.WifiDeviceOffline.getPrefix();
	}

}
