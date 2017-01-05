package com.bhu.vas.api.vto.device;

import java.util.List;

@SuppressWarnings("serial")
public class DeviceGEOPointCountVTO implements java.io.Serializable{
	
	private String province;
	private String city;
	private String district;
	private double lat;
	private double lon;
	private List<String> macs;
	private long count;
	private String distance;
	private String cash;
	private String saledCash;
	
	public String getProvince() {
		return province;
	}
	public void setProvince(String province) {
		this.province = province;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getDistrict() {
		return district;
	}
	public void setDistrict(String district) {
		this.district = district;
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
	
	public List<String> getMacs() {
		return null;
	}
	public void setMacs(List<String> macs) {
		this.macs = macs;
	}
	
	public long getCount() {
		return count;
	}
	public void setCount(long count) {
		this.count = count;
	}
	public String getDistance() {
		return distance;
	}
	public void setDistance(String distance) {
		this.distance = distance;
	}
	public String getCash() {
		return cash;
	}
	public void setCash(String cash) {
		this.cash = cash;
	}
	public String getSaledCash() {
		return saledCash;
	}
	public void setSaledCash(String saledCash) {
		this.saledCash = saledCash;
	}
}
