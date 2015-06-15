package com.bhu.vas.api.vto.config;

import java.io.Serializable;

/**
 * 返回urouter的终端别名
 * @author tangzichao
 *
 */
@SuppressWarnings("serial")
public class URouterDeviceConfigMMVTO implements Serializable{
	//终端mac
	private String mac;
	//终端别名
	private String n;
	
	public URouterDeviceConfigMMVTO(){
		
	}
	
	public URouterDeviceConfigMMVTO(String mac, String n){
		this.mac = mac;
		this.n = n;
	}
	
	public String getMac() {
		return mac;
	}
	public void setMac(String mac) {
		this.mac = mac;
	}
	public String getN() {
		return n;
	}
	public void setN(String n) {
		this.n = n;
	}
}
