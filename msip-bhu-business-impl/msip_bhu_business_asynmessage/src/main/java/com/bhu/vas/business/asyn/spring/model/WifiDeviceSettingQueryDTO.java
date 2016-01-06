package com.bhu.vas.business.asyn.spring.model;

import com.bhu.vas.business.asyn.spring.builder.ActionMessageType;

public class WifiDeviceSettingQueryDTO extends WifiCmdsNotifyDTO {

	private boolean deviceURouter;
	private int refresh_status;
	
	public int getRefresh_status() {
		return refresh_status;
	}

	public void setRefresh_status(int refresh_status) {
		this.refresh_status = refresh_status;
	}

	public boolean isDeviceURouter() {
		return deviceURouter;
	}

	public void setDeviceURouter(boolean deviceURouter) {
		this.deviceURouter = deviceURouter;
	}

	@Override
	public String getActionType() {
		return ActionMessageType.WifiDeviceSettingQuery.getPrefix();
	}

}
