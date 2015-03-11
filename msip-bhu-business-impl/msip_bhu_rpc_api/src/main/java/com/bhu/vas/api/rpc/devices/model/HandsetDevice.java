package com.bhu.vas.api.rpc.devices.model;

import java.util.Date;

import com.smartwork.msip.cores.orm.model.BaseStringModel;
/*
 * 移动设备基础信息
 */
@SuppressWarnings("serial")
public class HandsetDevice extends BaseStringModel{
	//手机号码
	private String mobileno;
	//最后一次连入的wifi设备id
	private String last_wifi_id;
	//最后一次连入的wifi设备的时间
	private Date last_login_at;
	private Date created_at;
	
	
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

	public String getMobileno() {
		return mobileno;
	}

	public void setMobileno(String mobileno) {
		this.mobileno = mobileno;
	}

	public String getLast_wifi_id() {
		return last_wifi_id;
	}

	public void setLast_wifi_id(String last_wifi_id) {
		this.last_wifi_id = last_wifi_id;
	}

	public Date getLast_login_at() {
		return last_login_at;
	}

	public void setLast_login_at(Date last_login_at) {
		this.last_login_at = last_login_at;
	}

	public Date getCreated_at() {
		return created_at;
	}

	public void setCreated_at(Date created_at) {
		this.created_at = created_at;
	}
}