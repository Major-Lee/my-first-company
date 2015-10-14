package com.bhu.vas.api.dto.search;

import java.io.Serializable;


@SuppressWarnings("serial")
public class WifiDeviceSearchDTO implements Serializable{
	//wifi设备mac
	private String id;
	//设备sn
	private String sn;
	//规格化地址信息
	private String address;
	//工作模式
	private String workmodel;
	//配置模式
	private String configmodel;
	//原始版本号
	private String origswver;
	//设备类型
	private String devicetype;
	//wifi设备是否在线
	private int online;
	//设备所在分组ids
	private String groups;
	//接入的移动设备数量
	private int count;
	//wifi设备的注册时间
	private long register_at;
	//纬度
	private double lat;
	//经度
	private double lon;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getSn() {
		return sn;
	}
	public void setSn(String sn) {
		this.sn = sn;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
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
	public String getConfigmodel() {
		return configmodel;
	}
	public void setConfigmodel(String configmodel) {
		this.configmodel = configmodel;
	}
	public String getOrigswver() {
		return origswver;
	}
	public void setOrigswver(String origswver) {
		this.origswver = origswver;
	}
	public String getWorkmodel() {
		return workmodel;
	}
	public void setWorkmodel(String workmodel) {
		this.workmodel = workmodel;
	}
	public String getDevicetype() {
		return devicetype;
	}
	public void setDevicetype(String devicetype) {
		this.devicetype = devicetype;
	}
	public int getOnline() {
		return online;
	}
	public void setOnline(int online) {
		this.online = online;
	}
	public String getGroups() {
		return groups;
	}
	public void setGroups(String groups) {
		this.groups = groups;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public long getRegister_at() {
		return register_at;
	}
	public void setRegister_at(long register_at) {
		this.register_at = register_at;
	}
	
}
