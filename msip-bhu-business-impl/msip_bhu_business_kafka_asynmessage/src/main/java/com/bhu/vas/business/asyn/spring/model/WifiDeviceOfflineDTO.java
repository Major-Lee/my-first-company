package com.bhu.vas.business.asyn.spring.model;

import com.bhu.vas.business.asyn.spring.builder.ActionDTO;
import com.bhu.vas.business.asyn.spring.builder.ActionMessageType;

public class WifiDeviceOfflineDTO extends ActionDTO {
	
	private long last_login_at;//wifi设备上次登录时间
	
	@Override
	public String getActionType() {
		return ActionMessageType.WifiDeviceOffline.getPrefix();
	}

	public long getLast_login_at() {
		return last_login_at;
	}

	public void setLast_login_at(long last_login_at) {
		this.last_login_at = last_login_at;
	}

}
