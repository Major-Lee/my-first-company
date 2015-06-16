package com.bhu.vas.business.asyn.spring.model;

import com.bhu.vas.business.asyn.spring.builder.ActionDTO;
import com.bhu.vas.business.asyn.spring.builder.ActionMessageType;

public class WifiDeviceSettingChangedDTO extends ActionDTO {
	//是否需要初始化urouter的默认黑名单
	private boolean init_default_acl;

	public boolean isInit_default_acl() {
		return init_default_acl;
	}

	public void setInit_default_acl(boolean init_default_acl) {
		this.init_default_acl = init_default_acl;
	}

	@Override
	public String getActionType() {
		return ActionMessageType.WifiDeviceSettingChanged.getPrefix();
	}

}
