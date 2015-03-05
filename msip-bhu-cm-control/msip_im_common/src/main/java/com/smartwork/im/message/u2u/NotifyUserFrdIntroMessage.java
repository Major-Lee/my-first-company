package com.smartwork.im.message.u2u;

/**
 * 用户通过好友请求后，介绍给对方好友的消息
 * @author Lawliet
 *
 */
public class NotifyUserFrdIntroMessage {
	private String iuid;//介绍的用户uid
	private String in;//介绍的用户昵称
	private String iv; //介绍的用户头像
	private String imno;//介绍的用户手机号
	
	public String getIuid() {
		return iuid;
	}
	public void setIuid(String iuid) {
		this.iuid = iuid;
	}
	public String getIn() {
		return in;
	}
	public void setIn(String in) {
		this.in = in;
	}
	public String getIv() {
		return iv;
	}
	public void setIv(String iv) {
		this.iv = iv;
	}
	public String getImno() {
		return imno;
	}
	public void setImno(String imno) {
		this.imno = imno;
	}
}
