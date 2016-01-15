package com.bhu.vas.business.asyn.spring.model;

import com.bhu.vas.business.asyn.spring.builder.ActionDTO;
import com.bhu.vas.business.asyn.spring.builder.ActionMessageType;

public class WifiDeviceOnlineDTO extends ActionDTO {
	private String join_reason;//设备加入原因类型
	private long login_ts;//wifi设备本次登录的时间
	private long last_login_at;//wifi设备上次的登录时间
	private boolean newWifi;//是否是新增wifi设备
	private boolean wanIpChanged;//wanip变更
	private boolean needLocationQuery;//是否需要发送查询地理位置指令
	private boolean workModeChanged;//设备工作模式是否切换
	/*//旧的workmode
	private String o_wmode;
	//新的workmode
	private String n_wmode;*/
	private String dhcpcStatusQuery_interface;//是否需要发送查询dhcp状态信息
	@Override
	public String getActionType() {
		return ActionMessageType.WifiDeviceOnline.getPrefix();
	}

	public String getJoin_reason() {
		return join_reason;
	}

	public void setJoin_reason(String join_reason) {
		this.join_reason = join_reason;
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

	public String getDhcpcStatusQuery_interface() {
		return dhcpcStatusQuery_interface;
	}

	public void setDhcpcStatusQuery_interface(String dhcpcStatusQuery_interface) {
		this.dhcpcStatusQuery_interface = dhcpcStatusQuery_interface;
	}

	public boolean isWanIpChanged() {
		return wanIpChanged;
	}

	public void setWanIpChanged(boolean wanIpChanged) {
		this.wanIpChanged = wanIpChanged;
	}

	public boolean isWorkModeChanged() {
		return workModeChanged;
	}

	public void setWorkModeChanged(boolean workModeChanged) {
		this.workModeChanged = workModeChanged;
	}

	/*public String getO_wmode() {
		return o_wmode;
	}

	public void setO_wmode(String o_wmode) {
		this.o_wmode = o_wmode;
	}

	public String getN_wmode() {
		return n_wmode;
	}

	public void setN_wmode(String n_wmode) {
		this.n_wmode = n_wmode;
	}*/
	
}
