package com.bhu.vas.api.dto.ret;

import java.io.Serializable;

import org.springframework.util.StringUtils;

@SuppressWarnings("serial")
public class LocationDTO implements Serializable{
	//纬度
	private String lat;
	//经度
	private String lon;
	
	public String getLat() {
		return lat;
	}
	public void setLat(String lat) {
		this.lat = lat;
	}
	public String getLon() {
		return lon;
	}
	public void setLon(String lon) {
		this.lon = lon;
	}
	public boolean validate(){
		if(StringUtils.isEmpty(lat) || StringUtils.isEmpty(lon)) return false;
		return true;
	}
}
