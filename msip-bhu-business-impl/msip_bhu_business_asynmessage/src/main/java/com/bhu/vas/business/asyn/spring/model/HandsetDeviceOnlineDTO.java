package com.bhu.vas.business.asyn.spring.model;

import com.bhu.vas.business.asyn.spring.builder.ActionDTO;
import com.bhu.vas.business.asyn.spring.builder.ActionMessageType;

public class HandsetDeviceOnlineDTO extends ActionDTO {
	private String wifiId;//连接的wifi mac
	private long login_ts;//移动设备本次接入时间
	private long last_login_at;//移动设备上次接入时间
	private boolean newHandset;//是否是新增移动设备
	//newHandset4this 是否是第一次连接此设备
	private boolean nh4t;
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

	public boolean isNewHandset() {
		return newHandset;
	}

	public void setNewHandset(boolean newHandset) {
		this.newHandset = newHandset;
	}

	public long getLast_login_at() {
		return last_login_at;
	}

	public void setLast_login_at(long last_login_at) {
		this.last_login_at = last_login_at;
	}

	public boolean isNh4t() {
		return nh4t;
	}

	public void setNh4t(boolean nh4t) {
		this.nh4t = nh4t;
	}

	@Override
	public String getActionType() {
		return ActionMessageType.HandsetDeviceOnline.getPrefix();
	}

}
