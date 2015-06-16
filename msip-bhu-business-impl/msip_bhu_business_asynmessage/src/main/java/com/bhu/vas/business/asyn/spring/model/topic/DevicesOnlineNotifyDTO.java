package com.bhu.vas.business.asyn.spring.model.topic;

import java.util.List;

import com.bhu.vas.business.asyn.spring.builder.ActionDTO;
import com.bhu.vas.business.asyn.spring.builder.ActionMessageType;

public class DevicesOnlineNotifyDTO extends ActionDTO {
	private String ctx;
	private List<String> macs;
	@Override
	public String getActionType() {
		return ActionMessageType.TOPICDevicesOnlineNotify.getPrefix();
	}

	public String getCtx() {
		return ctx;
	}

	public void setCtx(String ctx) {
		this.ctx = ctx;
	}

	public List<String> getMacs() {
		return macs;
	}

	public void setMacs(List<String> macs) {
		this.macs = macs;
	}

}
