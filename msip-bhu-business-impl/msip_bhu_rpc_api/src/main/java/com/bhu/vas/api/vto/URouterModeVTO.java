package com.bhu.vas.api.vto;

import java.io.Serializable;

/**
 * urouter的上网方式vto
 * @author tangzichao
 *
 */
@SuppressWarnings("serial")
public class URouterModeVTO implements Serializable{
	//上网模式
    private String mode;
    //pppoe 用户名
    private String p_un;
    //pppoe 密码
    private String p_pwd;
    //设备ip
    private String ip;
    //设备子网掩码
    private String netmask;
    //设备网关
    private String gateway;
    //dns
    private String dns;
    
	public String getMode() {
		return mode;
	}
	public void setMode(String mode) {
		this.mode = mode;
	}
	public String getP_un() {
		return p_un;
	}
	public void setP_un(String p_un) {
		this.p_un = p_un;
	}
	public String getP_pwd() {
		return p_pwd;
	}
	public void setP_pwd(String p_pwd) {
		this.p_pwd = p_pwd;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public String getNetmask() {
		return netmask;
	}
	public void setNetmask(String netmask) {
		this.netmask = netmask;
	}
	public String getGateway() {
		return gateway;
	}
	public void setGateway(String gateway) {
		this.gateway = gateway;
	}
	public String getDns() {
		return dns;
	}
	public void setDns(String dns) {
		this.dns = dns;
	}
}
