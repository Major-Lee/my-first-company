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
	
	//针对ios app的版本号，如果在app_version 在小于min_ios_version,并且force_app_update=true 则提示ios app更新
	private boolean force_ios_app_update = false;
	private String min_ios_version;
	//针对adr app的版本号，如果在app_version 在小于min_adr_version,并且force_app_update=true 则提示adr app更新
	private boolean force_adr_app_update = false;
	private String min_adr_version;
	
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


	public boolean isForce_ios_app_update() {
		return force_ios_app_update;
	}

	public void setForce_ios_app_update(boolean force_ios_app_update) {
		this.force_ios_app_update = force_ios_app_update;
	}

	public String getMin_ios_version() {
		return min_ios_version;
	}

	public void setMin_ios_version(String min_ios_version) {
		this.min_ios_version = min_ios_version;
	}

	public boolean isForce_adr_app_update() {
		return force_adr_app_update;
	}

	public void setForce_adr_app_update(boolean force_adr_app_update) {
		this.force_adr_app_update = force_adr_app_update;
	}

	public String getMin_adr_version() {
		return min_adr_version;
	}

	public void setMin_adr_version(String min_adr_version) {
		this.min_adr_version = min_adr_version;
	}

	public Date getCreated_at() {
		return created_at;
	}

	public void setCreated_at(Date created_at) {
		this.created_at = created_at;
	}
	
}
