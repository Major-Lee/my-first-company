package com.bhu.vas.api.rpc.devices.model;

import java.util.Date;

import com.smartwork.msip.cores.orm.model.BaseStringModel;
/*
 * wifi设备基础信息
 */
@SuppressWarnings("serial")
public class WifiDevice extends BaseStringModel{
	//原始硬件型号(类型)
	private String hdtype;	
	//原始厂商
	private String orig_vendor;
	//原始设备型号
	private String orig_model;
	//原始硬件版本号
	private String orig_hdver;
	//原始软件版本号
	private String orig_swver;
	//oem厂商
	private String oem_vendor;
	//oem后设备型号
	private String oem_model;
	//oem后硬件版本号
	private String oem_hdver;
	//oem后软件版本号
	private String oem_swver;
	//
	private String sn;
	private String ip;
	//设备连接到网管服务器时所对应的wan ip。由nginx接入服务器填写
	private String wan_ip;
	//设备的配置流水号
	private String config_sequence;
	//版本信息里的build相关信息
	private String build_info;
	//配置模型版本号
	private String config_model_ver;
	//配置模式(取值有：basic, expert, fitap)
	private String config_mode;
	//工作模式, 对应配置模式
	private String work_mode;
	//wifi设备是否在线
	private boolean online;
	//下行流量
	private String rx_bytes;
	//上行流量
	private String tx_bytes;
	//设备下行网速
	private String data_rx_rate;
	//设备上行网速
	private String data_tx_rate;
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
	//网络运营商信息
	private String carrier;
	//百度geocoding create result id
	private String bdid;
	//通过ip得到的坐标和地址
	private boolean ipgen = false;
	//设备运行时长
	private String uptime;
	//最后一次登录时间
	private Date last_reged_at;
	//最后一次登出时间
	private Date last_logout_at;
	private Date created_at;
	
	
	@Override
	public void preInsert() {
		if (this.created_at == null)
			this.created_at = new Date();
		super.preInsert();
	}
	
	@Override
	public void preUpdate() {
		super.preUpdate();
	}

	public String getHdtype() {
		return hdtype;
	}

	public void setHdtype(String hdtype) {
		this.hdtype = hdtype;
	}

	public String getOrig_vendor() {
		return orig_vendor;
	}

	public void setOrig_vendor(String orig_vendor) {
		this.orig_vendor = orig_vendor;
	}

	public String getOrig_model() {
		return orig_model;
	}

	public void setOrig_model(String orig_model) {
		this.orig_model = orig_model;
	}

	public String getOrig_hdver() {
		return orig_hdver;
	}

	public void setOrig_hdver(String orig_hdver) {
		this.orig_hdver = orig_hdver;
	}

	public String getOrig_swver() {
		return orig_swver;
	}

	public void setOrig_swver(String orig_swver) {
		this.orig_swver = orig_swver;
	}

	public String getOem_vendor() {
		return oem_vendor;
	}

	public void setOem_vendor(String oem_vendor) {
		this.oem_vendor = oem_vendor;
	}

	public String getOem_model() {
		return oem_model;
	}

	public void setOem_model(String oem_model) {
		this.oem_model = oem_model;
	}

	public String getOem_hdver() {
		return oem_hdver;
	}

	public void setOem_hdver(String oem_hdver) {
		this.oem_hdver = oem_hdver;
	}

	public String getOem_swver() {
		return oem_swver;
	}

	public void setOem_swver(String oem_swver) {
		this.oem_swver = oem_swver;
	}

	public String getSn() {
		return sn;
	}

	public void setSn(String sn) {
		this.sn = sn;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getWan_ip() {
		return wan_ip;
	}

	public void setWan_ip(String wan_ip) {
		this.wan_ip = wan_ip;
	}

	public String getConfig_sequence() {
		return config_sequence;
	}

	public void setConfig_sequence(String config_sequence) {
		this.config_sequence = config_sequence;
	}

	public String getBuild_info() {
		return build_info;
	}

	public void setBuild_info(String build_info) {
		this.build_info = build_info;
	}

	public String getConfig_model_ver() {
		return config_model_ver;
	}

	public void setConfig_model_ver(String config_model_ver) {
		this.config_model_ver = config_model_ver;
	}

	public String getConfig_mode() {
		return config_mode;
	}

	public void setConfig_mode(String config_mode) {
		this.config_mode = config_mode;
	}

	public String getWork_mode() {
		return work_mode;
	}

	public boolean isOnline() {
		return online;
	}

	public void setOnline(boolean online) {
		this.online = online;
	}
	
	public String getRx_bytes() {
		return rx_bytes;
	}
	
	public void setRx_bytes(String rx_bytes) {
		this.rx_bytes = rx_bytes;
	}

	public String getTx_bytes() {
		return tx_bytes;
	}

	public void setTx_bytes(String tx_bytes) {
		this.tx_bytes = tx_bytes;
	}

	public String getData_rx_rate() {
		return data_rx_rate;
	}

	public void setData_rx_rate(String data_rx_rate) {
		this.data_rx_rate = data_rx_rate;
	}

	public String getData_tx_rate() {
		return data_tx_rate;
	}

	public void setData_tx_rate(String data_tx_rate) {
		this.data_tx_rate = data_tx_rate;
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

	public void setWork_mode(String work_mode) {
		this.work_mode = work_mode;
	}

	public Date getLast_reged_at() {
		return last_reged_at;
	}

	public void setLast_reged_at(Date last_reged_at) {
		this.last_reged_at = last_reged_at;
	}

	public Date getCreated_at() {
		return created_at;
	}

	public void setCreated_at(Date created_at) {
		this.created_at = created_at;
	}

	public String getBdid() {
		return bdid;
	}

	public void setBdid(String bdid) {
		this.bdid = bdid;
	}

	public boolean isIpgen() {
		return ipgen;
	}

	public void setIpgen(boolean ipgen) {
		this.ipgen = ipgen;
	}

	public String getUptime() {
		return uptime;
	}

	public void setUptime(String uptime) {
		this.uptime = uptime;
	}

	public Date getLast_logout_at() {
		return last_logout_at;
	}

	public void setLast_logout_at(Date last_logout_at) {
		this.last_logout_at = last_logout_at;
	}

	public String getCarrier() {
		return carrier;
	}

	public void setCarrier(String carrier) {
		this.carrier = carrier;
	}
	
}