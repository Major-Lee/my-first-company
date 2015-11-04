package com.bhu.vas.api.rpc.devices.model;

import java.util.Date;

import com.smartwork.msip.cores.orm.model.BaseStringModel;
/*
 * wifi设备 灰度管理表
 * 对于目前的灰度定义，一个设备只能属于一个灰度
 */
@SuppressWarnings("serial")
public class WifiDeviceGray extends BaseStringModel{
	//id为设备mac地址
	//灰度
	private int gray;	
	private Date created_at;
	
	@Override
	public void preInsert() {
		if (this.created_at == null)
			this.created_at = new Date();
		super.preInsert();
	}

	public int getGray() {
		return gray;
	}

	public void setGray(int gray) {
		this.gray = gray;
	}

	public Date getCreated_at() {
		return created_at;
	}

	public void setCreated_at(Date created_at) {
		this.created_at = created_at;
	}
	
}