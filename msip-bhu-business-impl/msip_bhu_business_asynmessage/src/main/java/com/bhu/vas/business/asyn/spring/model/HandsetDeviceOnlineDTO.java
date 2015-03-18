package com.bhu.vas.business.asyn.spring.model;

import com.bhu.vas.business.asyn.spring.builder.ActionDTO;
import com.bhu.vas.business.asyn.spring.builder.ActionMessageType;

public class HandsetDeviceOnlineDTO extends ActionDTO {
	private String wifiId;//连接的wifi mac
	private long login_ts;//移动设备接入时间
	
	public String getWifiId() {
		return wifiId;
	}

	public void setWifiId(String wifiId) {
		this.wifiId = wifiId;
	}

	public long getLogin_ts() {
		return login_ts;
	}

	public void setLogin_ts(long login_ts) {
		this.login_ts = login_ts;
	}

	@Override
	public String getActionType() {
		return ActionMessageType.HandsetDeviceOnline.getPrefix();
	}

}
