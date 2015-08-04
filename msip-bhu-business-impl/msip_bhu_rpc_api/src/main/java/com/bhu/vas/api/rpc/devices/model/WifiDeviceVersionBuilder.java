package com.bhu.vas.api.rpc.devices.model;

import java.util.Date;

import com.smartwork.msip.cores.orm.model.BaseStringModel;

@SuppressWarnings("serial")
public class WifiDeviceVersionBuilder extends BaseStringModel{
	
	public static final String VersionBuilder_Normal = "Normal";
	public static final String VersionBuilder_FirstGray = "FirstGray";
	public static final String VersionBuilder_SecondGray = "SecondGray";
	public static final String VersionBuilder_ThirdGray = "ThirdGray";
	
	
	//固件版本号及builder号全称 eg:AP303P07V1.2.16Build7913
	private String d_firmware_name;
	//固件版本号 eg:V1.2.16
	private String d_versions;
	//固件build号 eg：7913
	private String d_builderno;
	//固件文件下载url
	private String firmware_upgrade_url;
	//针对设备版本比对，force_device_update=true，则如果小于d_firmware_name，必定下发升级指令
	private boolean force_device_update = false;
	
	//针对app的版本号，如果在app_version 在(app_min_version,app_max_version)区间内,并且force_app_update=true 则提示app更新
	private boolean force_app_update = false;
	private String app_min_version;
	private String app_max_version;


	private Date created_at;
	
	@Override
	public void preInsert() {
		if (this.created_at == null)
			this.created_at = new Date();
		super.preInsert();
	}

	public String getD_firmware_name() {
		return d_firmware_name;
	}

	public void setD_firmware_name(String d_firmware_name) {
		this.d_firmware_name = d_firmware_name;
	}

	public String getD_versions() {
		return d_versions;
	}

	public void setD_versions(String d_versions) {
		this.d_versions = d_versions;
	}

	public String getD_builderno() {
		return d_builderno;
	}

	public void setD_builderno(String d_builderno) {
		this.d_builderno = d_builderno;
	}

	public String getFirmware_upgrade_url() {
		return firmware_upgrade_url;
	}

	public void setFirmware_upgrade_url(String firmware_upgrade_url) {
		this.firmware_upgrade_url = firmware_upgrade_url;
	}

	public boolean isForce_device_update() {
		return force_device_update;
	}

	public void setForce_device_update(boolean force_device_update) {
		this.force_device_update = force_device_update;
	}

	public boolean isForce_app_update() {
		return force_app_update;
	}

	public void setForce_app_update(boolean force_app_update) {
		this.force_app_update = force_app_update;
	}

	public String getApp_min_version() {
		return app_min_version;
	}

	public void setApp_min_version(String app_min_version) {
		this.app_min_version = app_min_version;
	}

	public String getApp_max_version() {
		return app_max_version;
	}

	public void setApp_max_version(String app_max_version) {
		this.app_max_version = app_max_version;
	}

	public Date getCreated_at() {
		return created_at;
	}

	public void setCreated_at(Date created_at) {
		this.created_at = created_at;
	}
	
}
