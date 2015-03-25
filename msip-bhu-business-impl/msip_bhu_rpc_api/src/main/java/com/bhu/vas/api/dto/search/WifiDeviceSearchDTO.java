package com.bhu.vas.api.dto.search;

import java.io.Serializable;


@SuppressWarnings("serial")
public class WifiDeviceSearchDTO implements Serializable{
	//wifi设备mac
	private String id;
	//规格化地址信息
	private String address;
	//工作模式
	private String workmodel;
	//设备类型
	private String devicetype;
	//wifi设备是否在线
	private int online;
	//接入的移动设备数量
	private int count;
	//wifi设备的注册时间
	private long register_at;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
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
