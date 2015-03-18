package com.bhu.vas.business.asyn.spring.model;

import com.bhu.vas.business.asyn.spring.builder.ActionDTO;
import com.bhu.vas.business.asyn.spring.builder.ActionMessageType;

public class WifiDeviceOnlineDTO extends ActionDTO {
	//wifi设备上线的时间
	private long login_ts;


	@Override
	public String getActionType() {
		return ActionMessageType.WifiDeviceOnline.getPrefix();
	}

	public long getLogin_ts() {
		return login_ts;
	}

	public void setLogin_ts(long login_ts) {
		this.login_ts = login_ts;
	}	
}
