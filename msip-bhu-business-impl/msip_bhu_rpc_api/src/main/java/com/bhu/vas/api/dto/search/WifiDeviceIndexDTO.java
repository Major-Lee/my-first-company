package com.bhu.vas.api.dto.search;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.util.StringUtils;

import com.smartwork.msip.cores.helper.StringHelper;


public class WifiDeviceIndexDTO {
	public static final int Online_Status = 1;
	public static final int offline_Status = 0;
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
	//工作模式
	private String workmodel;
	//配置模式
	private String configmodel;
	//软件版本号
	private String origswver;
	//设备类型
	private String devicetype;
	//wifi设备是否在线
	private int online;
	//设备所属群组ids
	private List<Long> groupids;
	//设备是否为新版本设备
	private int nvd;
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
	public List<Long> getGroupids() {
		return groupids;
	}
	public void setGroupids(List<Long> groupids) {
		this.groupids = groupids;
	}
	public String getGroups(){
		if(groupids != null && !groupids.isEmpty()){
			StringBuffer groups = new StringBuffer();
			for(Long groupid : groupids){
				if(groups.length() > 0) groups.append(StringHelper.WHITESPACE_STRING_GAP);
				groups.append(String.valueOf(groupid));
			}
			return groups.toString();
		}
		return null;
	}
	public int getNvd() {
		return nvd;
	}
	public void setNvd(int nvd) {
		this.nvd = nvd;
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
