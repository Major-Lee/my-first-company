package com.bhu.vas.api.rpc.portrait.model;

import java.util.Date;

import com.smartwork.msip.cores.orm.model.BaseStringModel;

@SuppressWarnings("serial")
public class HandsetPortrait  extends BaseStringModel{
	
	private String mobile;
	private String browserVersion;
	private String browserType;
	private String browserManufacturer;
	private String osType;
	private String osManufacturer;
	private int type;
	private Date created_at;
	
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getBrowserVersion() {
		return browserVersion;
	}
	public void setBrowserVersion(String browserVersion) {
		this.browserVersion = browserVersion;
	}
	public String getBrowserType() {
		return browserType;
	}
	public void setBrowserType(String browserType) {
		this.browserType = browserType;
	}
	public String getBrowserManufacturer() {
		return browserManufacturer;
	}
	public void setBrowserManufacturer(String browserManufacturer) {
		this.browserManufacturer = browserManufacturer;
	}
	public String getOsType() {
		return osType;
	}
	public void setOsType(String osType) {
		this.osType = osType;
	}
	public String getOsManufacturer() {
		return osManufacturer;
	}
	public void setOsManufacturer(String osManufacturer) {
		this.osManufacturer = osManufacturer;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public Date getCreated_at() {
		return created_at;
	}
	public void setCreated_at(Date created_at) {
		this.created_at = created_at;
	}
	
}
