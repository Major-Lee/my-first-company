package com.whisper.web.test;

public class IpModel {
	/*
	String ip = request.getHeader("x-forwarded-for");
	if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
		ip = request.getHeader("Proxy-Client-IP");
	}
	if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
		ip = request.getHeader("WL-Proxy-Client-IP");
	}
	if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
		ip = request.getRemoteAddr();
	}
	return ip;*/
	
	private String x_forwarded_for;
	private String proxy_Client_IP;
	private String wl_Proxy_Client_IP;
	private String remoteAddr;
	private String serverAddr;
	public String getX_forwarded_for() {
		return x_forwarded_for;
	}
	public void setX_forwarded_for(String x_forwarded_for) {
		this.x_forwarded_for = x_forwarded_for;
	}
	public String getProxy_Client_IP() {
		return proxy_Client_IP;
	}
	public void setProxy_Client_IP(String proxy_Client_IP) {
		this.proxy_Client_IP = proxy_Client_IP;
	}
	public String getWl_Proxy_Client_IP() {
		return wl_Proxy_Client_IP;
	}
	public void setWl_Proxy_Client_IP(String wl_Proxy_Client_IP) {
		this.wl_Proxy_Client_IP = wl_Proxy_Client_IP;
	}
	public String getRemoteAddr() {
		return remoteAddr;
	}
	public void setRemoteAddr(String remoteAddr) {
		this.remoteAddr = remoteAddr;
	}
	public String getServerAddr() {
		return serverAddr;
	}
	public void setServerAddr(String serverAddr) {
		this.serverAddr = serverAddr;
	}
	
	
}
