package com.bhu.vas.api.dto;

import java.io.Serializable;
/*
 * wifi设备基础信息RPC DTO
 */
@SuppressWarnings("serial")
public class WifiDeviceDTO implements Serializable{
	private String mac;
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
	
	private String join_reason;

	public String getMac() {
		return mac;
	}

	public void setMac(String mac) {
		this.mac = mac;
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

	public void setWork_mode(String work_mode) {
		this.work_mode = work_mode;
	}

	public String getJoin_reason() {
		return join_reason;
	}

	public void setJoin_reason(String join_reason) {
		this.join_reason = join_reason;
	}
	
}