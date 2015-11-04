package com.bhu.vas.api.rpc.devices.model;

import java.util.Date;

import com.smartwork.msip.cores.orm.model.BaseStringModel;
/*
<<<<<<< Updated upstream
 * wifi设备增值模块在线标记表
 * 此表为wifidevice扩展表
 * 初始数据迁移可以通过sql实现
 * 
 * insert into 
=======
 * wifi设备基础信息
>>>>>>> Stashed changes
 */
@SuppressWarnings("serial")
public class WifiDeviceModule extends BaseStringModel{

	//原始软件vapmodule版本号
	private String 	orig_vap_module;
	//wifi设备是否在线
	private boolean online;
	private boolean module_online;
	//最后一次登录时间
	private Date last_module_reged_at;
	//最后一次登出时间
	private Date last_module_logout_at;
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

	public String getOrig_vap_module() {
		return orig_vap_module;
	}

	public void setOrig_vap_module(String orig_vap_module) {
		this.orig_vap_module = orig_vap_module;
	}

	public boolean isOnline() {
		return online;
	}

	public void setOnline(boolean online) {
		this.online = online;
	}

	public boolean isModule_online() {
		return module_online;
	}

	public void setModule_online(boolean module_online) {
		this.module_online = module_online;
	}

	public Date getLast_module_reged_at() {
		return last_module_reged_at;
	}

	public void setLast_module_reged_at(Date last_module_reged_at) {
		this.last_module_reged_at = last_module_reged_at;
	}

	public Date getLast_module_logout_at() {
		return last_module_logout_at;
	}

	public void setLast_module_logout_at(Date last_module_logout_at) {
		this.last_module_logout_at = last_module_logout_at;
	}

	public Date getCreated_at() {
		return created_at;
	}

	public void setCreated_at(Date created_at) {
		this.created_at = created_at;
	}

}