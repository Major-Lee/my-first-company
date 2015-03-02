package com.bhu.vas.api.user.model;

import java.util.Date;

import com.smartwork.msip.cores.orm.model.BaseStringModel;
/**
 * 用来保存用户的当前有效的设备
 * @author lawliet
 *
 */
@SuppressWarnings("serial")
public class UserDevice extends BaseStringModel{
	private String devicetoken;//客户端设备token
	private String devicetype;//客户端设备类型
	private String pushtype;//客户端push类型
	private String client_system_v;//客户端系统版本
	private String client_app_v;//客户端应用版本
	private String unittype;//设备型号
	private Date created_at;
	public UserDevice() {
		super();
	}

	@Override
	public void preInsert() {
		if (this.created_at == null)
			this.created_at = new Date();
		super.preInsert();
	}
	
	@Override
	public void preUpdate() {
		super.preUpdate();
	}
	
	public Date getCreated_at() {
		return created_at;
	}
	
	public void setCreated_at(Date created_at) {
		this.created_at = created_at;
	}

	public String getDevicetoken() {
		return devicetoken;
	}

	public void setDevicetoken(String devicetoken) {
		this.devicetoken = devicetoken;
	}

	public String getDevicetype() {
		return devicetype;
	}

	public void setDevicetype(String devicetype) {
		this.devicetype = devicetype;
	}

	public String getPushtype() {
		return pushtype;
	}

	public void setPushtype(String pushtype) {
		this.pushtype = pushtype;
	}

	public String getClient_system_v() {
		return client_system_v;
	}

	public void setClient_system_v(String client_system_v) {
		this.client_system_v = client_system_v;
	}

	public String getClient_app_v() {
		return client_app_v;
	}

	public void setClient_app_v(String client_app_v) {
		this.client_app_v = client_app_v;
	}

	public String getUnittype() {
		return unittype;
	}

	public void setUnittype(String unittype) {
		this.unittype = unittype;
	}
}
