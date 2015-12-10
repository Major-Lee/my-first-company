package com.bhu.vas.business.asyn.spring.model;

import java.util.List;

import com.bhu.vas.business.asyn.spring.builder.ActionDTO;
import com.bhu.vas.business.asyn.spring.builder.ActionMessageType;

public class WifiDevicesModuleStyleChangedDTO extends ActionDTO {
	private String style;
	private List<String> macs;

	public String getStyle() {
		return style;
	}

	public void setStyle(String style) {
		this.style = style;
	}

	public List<String> getMacs() {
		return macs;
	}

	public void setMacs(List<String> macs) {
		this.macs = macs;
	}


	@Override
	public String getActionType() {
		return ActionMessageType.WifiDevicesModuleStyleChanged.getPrefix();
	}
}
