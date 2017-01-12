package com.bhu.vas.api.rpc.devices.model;

import java.util.Date;

import com.smartwork.msip.cores.orm.model.BaseStringModel;
/*
 * wifi设备基础信息
 */
@SuppressWarnings("serial")
public class WifiDeviceForGps extends BaseStringModel{
	//地理位置坐标 纬度
	private String lat;
	//地理位置坐标 经度
	private String lon;
	//国家
	private String country;
	//省名
	private String province;
	//城市名
	private String city;
	//区县名
	private String district;
	//街道名
	private String street;
	//结构化地址 如北京市海淀区后八家路
	private String formatted_address;
	//通过ip得到的坐标和地址
	private int loc_method = 0;// = false;
	//记录创建时间，不一定是设备第一次上线时间
	private Date created_at;
	
	@Override
	public void preInsert() {
		if (this.created_at == null)
			this.created_at = new Date();
		if (this.province == null)
			this.province = "其他";
		if (this.city == null)
			this.city = "其他";
		if (this.district == null)
			this.district = "其他";
		super.preInsert();
	}
	
	@Override
	public void preUpdate() {
		if (this.province == null)
			this.province = "其他";
		if (this.city == null)
			this.city = "其他";
		if (this.district == null)
			this.district = "其他";
		super.preUpdate();
	}

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

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getDistrict() {
		return district;
	}

	public void setDistrict(String district) {
		this.district = district;
	}

	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	public String getFormatted_address() {
		return formatted_address;
	}

	public void setFormatted_address(String formatted_address) {
		this.formatted_address = formatted_address;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}


	public Date getCreated_at() {
		return created_at;
	}

	public void setCreated_at(Date created_at) {
		this.created_at = created_at;
	}

	public int getLoc_method() {
		return loc_method;
	}

	public void setLoc_method(int loc_method) {
		this.loc_method = loc_method;
	}

}