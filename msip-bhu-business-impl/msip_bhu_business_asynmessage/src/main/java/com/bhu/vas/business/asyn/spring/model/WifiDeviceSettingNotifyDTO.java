package com.bhu.vas.business.asyn.spring.model;

import java.util.List;

import com.bhu.vas.business.asyn.spring.builder.ActionDTO;
import com.bhu.vas.business.asyn.spring.builder.ActionMessageType;

public class WifiDeviceSettingNotifyDTO extends ActionDTO {
	private List<String> vapnames;

	public List<String> getVapnames() {
		return vapnames;
	}

	public void setVapnames(List<String> vapnames) {
		this.vapnames = vapnames;
	}

	@Override
	public String getActionType() {
		return ActionMessageType.WifiDeviceSettingNotify.getPrefix();
	}

}
