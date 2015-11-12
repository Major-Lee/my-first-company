package com.bhu.vas.business.asyn.spring.model;

import com.bhu.vas.business.asyn.spring.builder.ActionDTO;
import com.bhu.vas.business.asyn.spring.builder.ActionMessageType;

public class WifiDeviceModuleOnlineDTO extends ActionDTO {
	private String orig_vap_module;
	public String getOrig_vap_module() {
		return orig_vap_module;
	}

	public void setOrig_vap_module(String orig_vap_module) {
		this.orig_vap_module = orig_vap_module;
	}

	@Override
	public String getActionType() {
		return ActionMessageType.WifiDeviceModuleOnline.getPrefix();
	}
	
}
