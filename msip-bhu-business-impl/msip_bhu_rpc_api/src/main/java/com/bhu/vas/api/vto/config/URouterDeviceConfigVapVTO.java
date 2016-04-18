package com.bhu.vas.api.vto.config;

import java.io.Serializable;

@SuppressWarnings("serial")
public class URouterDeviceConfigVapVTO implements Serializable{
	//vap名称
	private String vap_name;
	//vap ssid
	private String vap_ssid;
	//vap 密码 加密方式
	private String vap_auth;
	//vap 隐藏ssid
	private String vap_hide_ssid;
	//vap 密码
	private String vap_pwd;
	
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
	public String getVap_pwd() {
		return vap_pwd;
	}
	public void setVap_pwd(String vap_pwd) {
		this.vap_pwd = vap_pwd;
	}
	public String getVap_hide_ssid() {
		return vap_hide_ssid;
	}
	public void setVap_hide_ssid(String vap_hide_ssid) {
		this.vap_hide_ssid = vap_hide_ssid;
	}
}
