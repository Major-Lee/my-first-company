package com.bhu.vas.business.asyn.spring.model;

import com.bhu.vas.business.asyn.spring.builder.ActionMessageType;

public class WifiDeviceSettingQueryDTO extends WifiCmdsNotifyDTO {

	private int refresh_status;
	
	public int getRefresh_status() {
		return refresh_status;
	}

	public void setRefresh_status(int refresh_status) {
		this.refresh_status = refresh_status;
	}

	@Override
	public String getActionType() {
		return ActionMessageType.WifiDeviceSettingQuery.getPrefix();
	}

}
