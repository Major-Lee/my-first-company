package com.bhu.vas.api.rpc.wifi.model;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.bhu.vas.api.vto.advertise.AdvertiseVTO;
import com.smartwork.msip.cores.helper.ArithHelper;
import com.smartwork.msip.cores.orm.model.BaseStringModel;

@SuppressWarnings("serial")
public class SsidInfo extends BaseStringModel{
	private String bssid;
	private String ssid;
	private String device;
	private String pwd;

	private double lat;
	private double lon;
	private Date created_at;
	private Date updated_at;
	
	public SsidInfo() {
		super();
	}

	public SsidInfo(String id) {
		super();
		this.id = id;
	}
	
	public String getBssid() {
		return bssid;
	}

	public void setBssid(String bssid) {
		this.bssid = bssid;
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

	public double getLat() {
		return lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public double getLon() {
		return lon;
	}

	public void setLon(double lon) {
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
