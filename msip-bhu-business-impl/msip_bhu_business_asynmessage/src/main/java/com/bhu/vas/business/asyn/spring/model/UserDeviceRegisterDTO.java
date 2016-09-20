package com.bhu.vas.business.asyn.spring.model;

import com.bhu.vas.business.asyn.spring.builder.ActionDTO;
import com.bhu.vas.business.asyn.spring.builder.ActionMessageType;

public class UserDeviceRegisterDTO extends ActionDTO {

	private boolean fromApp;//绑定需求是否来自于app
	
	public boolean isFromApp() {
		return fromApp;
	}

	public void setFromApp(boolean fromApp) {
		this.fromApp = fromApp;
	}

	@Override
	public String getActionType() {
		return ActionMessageType.USERDEVICEREGISTER.getPrefix();
	}
}
