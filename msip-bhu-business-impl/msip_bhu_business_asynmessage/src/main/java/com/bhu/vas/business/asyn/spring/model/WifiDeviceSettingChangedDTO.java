package com.bhu.vas.business.asyn.spring.model;

import com.bhu.vas.business.asyn.spring.builder.ActionMessageType;

public class WifiDeviceSettingChangedDTO extends WifiCmdsNotifyDTO {

	@Override
	public String getActionType() {
		return ActionMessageType.WifiDeviceSettingChanged.getPrefix();
	}

}
