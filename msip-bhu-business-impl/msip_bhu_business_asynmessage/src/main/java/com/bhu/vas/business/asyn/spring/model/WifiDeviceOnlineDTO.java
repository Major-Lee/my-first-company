package com.bhu.vas.business.asyn.spring.model;

import com.bhu.vas.business.asyn.spring.builder.ActionDTO;
import com.bhu.vas.business.asyn.spring.builder.ActionMessageType;

public class WifiDeviceOnlineDTO extends ActionDTO {
	
	private long login_ts;//wifi设备本次登录的时间
	private long last_login_at;//wifi设备上次的登录时间
	private boolean newWifi;//是否是新增wifi设备
	private boolean needLocationQuery;//是否需要发送查询地理位置指令
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

	public long getLast_login_at() {
		return last_login_at;
	}

	public void setLast_login_at(long last_login_at) {
		this.last_login_at = last_login_at;
	}

	public boolean isNewWifi() {
		return newWifi;
	}

	public void setNewWifi(boolean newWifi) {
		this.newWifi = newWifi;
	}

	public boolean isNeedLocationQuery() {
		return needLocationQuery;
	}

	public void setNeedLocationQuery(boolean needLocationQuery) {
		this.needLocationQuery = needLocationQuery;
	}	
	
	
}
