package com.bhu.vas.api.rpc.devices.model;

import java.util.Date;

import com.smartwork.msip.cores.orm.model.BaseIntModel;
/*
 * wifi设备的组
 */
@SuppressWarnings("serial")
public class WifiDeviceGroup extends BaseIntModel{
	//组的名称
	private String name;
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getCreated_at() {
		return created_at;
	}

	public void setCreated_at(Date created_at) {
		this.created_at = created_at;
	}

	
}