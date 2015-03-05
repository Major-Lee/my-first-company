package com.smartwork.im.message.cm2s;

/**
 * 用户client连接被验证通过后
 * @author Edmond
 *
 */
public class CmClientSessionClosedNotifyRequest {
	private String user;
	private String ip;
	private String mark;
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
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
