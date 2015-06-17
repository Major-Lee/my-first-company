package com.bhu.vas.business.asyn.spring.model.topic;

import com.bhu.vas.business.asyn.spring.builder.ActionMessageType;

public class DeviceOfflineNotifyDTO extends NotifyDTO {
	private String ctx;

	@Override
	public String getNotifyType() {
		return ActionMessageType.TOPICDeviceOfflineNotify.getPrefix();
	}

	public String getCtx() {
		return ctx;
	}

	public void setCtx(String ctx) {
		this.ctx = ctx;
	}
}
