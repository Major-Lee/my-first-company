package com.bhu.vas.business.search.service.device.dto;

import java.util.HashSet;
import java.util.Set;

import org.springframework.util.StringUtils;

import com.smartwork.msip.cores.helper.StringHelper;


public class WifiDeviceIndexDTO {
	//wifi设备mac
	private String wifiId;
	//经度
	private String lat;
	//纬度
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
	//规格化地址信息
	private String format_address;
	//wifi设备是否在线
	private int online;
	//接入的移动设备数量
	private int count;
	//wifi设备的注册时间
	private long register_at;
	
	public String getWifiId() {
		return wifiId;
	}
	public void setWifiId(String wifiId) {
		this.wifiId = wifiId;
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
	public double[] getPoint(){
		if(StringUtils.isEmpty(lat) || StringUtils.isEmpty(lon)) return null;
		return new double[]{Double.parseDouble(lat), Double.parseDouble(lon)};
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
	public String getStreet() {
		return street;
	}
	public void setStreet(String street) {
		this.street = street;
	}
	/**
	 * format : 中国 北京市 朝阳区 黄寺大街 中国北京市朝阳区黄寺大街
	 * 方便用于分词
	 * @return
	 */
	public String getAddress(){
		if(StringUtils.isEmpty(country)) return null;
		
		Set<String> address_set = new HashSet<String>();
		address_set.add(country);
		address_set.add(province);
		address_set.add(city);
		address_set.add(district);
		address_set.add(street);
		address_set.add(format_address);
		
		StringBuffer address_sb = new StringBuffer();
		for(String add : address_set){
			if(address_sb.length() > 0) address_sb.append(StringHelper.WHITESPACE_STRING_GAP);
			address_sb.append(add);
		}
		return address_sb.toString();
	}
	public String getFormat_address() {
		return format_address;
	}
	public void setFormat_address(String format_address) {
		this.format_address = format_address;
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
