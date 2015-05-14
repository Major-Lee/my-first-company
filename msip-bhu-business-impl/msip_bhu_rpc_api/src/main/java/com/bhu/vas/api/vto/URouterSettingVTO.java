package com.bhu.vas.api.vto;

import java.io.Serializable;

/**
 * urouter的设置页面vto
 * @author tangzichao
 *
 */
@SuppressWarnings("serial")
public class URouterSettingVTO implements Serializable{
	//mac 
	private String mac;
	//oem后软件版本号
	private String oem_swver;
	//oem后硬件版本号
	private String oem_hdver;
	//是否在线
	private boolean ol;
	//本次运行时长 单位毫秒
	private String uptime;
	//是否被绑定
	//private boolean binded;
	//vap名称
	private String vap_name;
	//vap ssid
	private String vap_ssid;
	//vap 密码 加密方式
	private String vap_auth;
	//上网方式
	private String mode;
	
	public String getMac() {
		return mac;
	}
	public void setMac(String mac) {
		this.mac = mac;
	}
	public String getOem_swver() {
		return oem_swver;
	}
	public void setOem_swver(String oem_swver) {
		this.oem_swver = oem_swver;
	}
	public String getOem_hdver() {
		return oem_hdver;
	}
	public void setOem_hdver(String oem_hdver) {
		this.oem_hdver = oem_hdver;
	}
	public boolean isOl() {
		return ol;
	}
	public void setOl(boolean ol) {
		this.ol = ol;
	}
	public String getUptime() {
		return uptime;
	}
	public void setUptime(String uptime) {
		this.uptime = uptime;
	}
//	public boolean isBinded() {
//		return binded;
//	}
//	public void setBinded(boolean binded) {
//		this.binded = binded;
//	}
	public String getVap_name() {
		return vap_name;
	}
	public void setVap_name(String vap_name) {
		this.vap_name = vap_name;
	}
	public String getVap_ssid() {
		return vap_ssid;
	}
	public void setVap_ssid(String vap_ssid) {
		this.vap_ssid = vap_ssid;
	}
	public String getVap_auth() {
		return vap_auth;
	}
	public void setVap_auth(String vap_auth) {
		this.vap_auth = vap_auth;
	}
	public String getMode() {
		return mode;
	}
	public void setMode(String mode) {
		this.mode = mode;
	}
}
