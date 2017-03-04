package com.bhu.vas.api.rpc.wifi.model;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.bhu.vas.api.vto.advertise.AdvertiseVTO;
import com.smartwork.msip.cores.helper.ArithHelper;
import com.smartwork.msip.cores.orm.model.BaseStringModel;

@SuppressWarnings("serial")
public class SsidInfo extends BaseStringModel{
	private String ssid;
	private String mode;
	private String device;
	private String pwd;

	private Double lat;
	private Double lon;
	private Date created_at;
	private Date updated_at;
	
	public SsidInfo() {
		super();
	}

	public SsidInfo(String id) {
		super();
		this.id = id;
	}
	
	public String getSsid() {
		return ssid;
	}

	public void setSsid(String ssid) {
		this.ssid = ssid;
	}

	public String getDevice() {
		return device;
	}

	public void setDevice(String device) {
		this.device = device;
	}

	public String getPwd() {
		return pwd;
	}

	public void setPwd(String pwd) {
		this.pwd = pwd;
	}

	public Double getLat() {
		return lat;
	}

	public void setLat(Double lat) {
		this.lat = lat;
	}

	public Double getLon() {
		return lon;
	}

	public void setLon(Double lon) {
		this.lon = lon;
	}

	public Date getCreated_at() {
		return created_at;
	}

	public void setCreated_at(Date created_at) {
		this.created_at = created_at;
	}

	public Date getUpdated_at() {
		return updated_at;
	}

	public void setUpdated_at(Date updated_at) {
		this.updated_at = updated_at;
	}
	

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	@Override
	public void preInsert() {
		if (this.created_at == null)
			this.created_at = new Date();
		super.preInsert();
	}
	
	@Override
	public void preUpdate() {
		if (this.updated_at == null)
			this.updated_at = new Date();
		super.preUpdate();
	}
}
