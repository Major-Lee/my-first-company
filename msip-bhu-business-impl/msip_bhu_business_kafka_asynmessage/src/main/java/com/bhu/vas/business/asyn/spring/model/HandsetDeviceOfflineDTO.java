package com.bhu.vas.business.asyn.spring.model;

import com.bhu.vas.business.asyn.spring.builder.ActionDTO;
import com.bhu.vas.business.asyn.spring.builder.ActionMessageType;

public class HandsetDeviceOfflineDTO extends ActionDTO {
	private String wifiId;//连接的wifi mac
	private String uptime;//移动设备的连接状态的持续时间
	
	public String getWifiId() {
		return wifiId;
	}

	public void setWifiId(String wifiId) {
		this.wifiId = wifiId;
	}

	public String getUptime() {
		return uptime;
	}

	public void setUptime(String uptime) {
		this.uptime = uptime;
	}

	@Override
	public String getActionType() {
		return ActionMessageType.HandsetDeviceOffline.getPrefix();
	}

}
