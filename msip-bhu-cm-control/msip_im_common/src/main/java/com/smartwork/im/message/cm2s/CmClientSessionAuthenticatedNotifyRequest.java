package com.smartwork.im.message.cm2s;

/**
 * 用户client连接被验证通过后
 * @author Edmond
 *
 */
public class CmClientSessionAuthenticatedNotifyRequest {
	private String user;
	private String ip;
	private String pwd;
	private String mark;
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public String getPwd() {
		return pwd;
	}
	public void setPwd(String pwd) {
		this.pwd = pwd;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public String getMark() {
		return mark;
	}
	public void setMark(String mark) {
		this.mark = mark;
	}
	
	
}
