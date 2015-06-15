package com.bhu.vas.api.vto.config;

import java.io.Serializable;
import java.util.List;

@SuppressWarnings("serial")
public class URouterDeviceConfigVTO implements Serializable{
	//vap名称
	private String vap_name;
	//vap ssid
	private String vap_ssid;
	//vap 密码 加密方式
	private String vap_auth;
	//信号强度
	private String power;
	//admin管理密码
	private String admin_pwd;
	//vap 密码
	private String vap_pwd;
	//黑名单列表macs
	private List<String> block_macs;
	//终端限速列表
	private List<URouterDeviceConfigRateControlVTO> rcs;
	//终端别名列表
	private List<URouterDeviceConfigMMVTO> mms;
	
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
	public String getPower() {
		return power;
	}
	public void setPower(String power) {
		this.power = power;
	}
	public String getAdmin_pwd() {
		return admin_pwd;
	}
	public void setAdmin_pwd(String admin_pwd) {
		this.admin_pwd = admin_pwd;
	}
	public String getVap_pwd() {
		return vap_pwd;
	}
	public void setVap_pwd(String vap_pwd) {
		this.vap_pwd = vap_pwd;
	}
	public List<String> getBlock_macs() {
		return block_macs;
	}
	public void setBlock_macs(List<String> block_macs) {
		this.block_macs = block_macs;
	}
	public List<URouterDeviceConfigRateControlVTO> getRcs() {
		return rcs;
	}
	public void setRcs(List<URouterDeviceConfigRateControlVTO> rcs) {
		this.rcs = rcs;
	}
	public List<URouterDeviceConfigMMVTO> getMms() {
		return mms;
	}
	public void setMms(List<URouterDeviceConfigMMVTO> mms) {
		this.mms = mms;
	}

}
