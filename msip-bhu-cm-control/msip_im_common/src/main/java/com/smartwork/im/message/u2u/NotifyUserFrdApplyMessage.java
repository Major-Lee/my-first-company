package com.smartwork.im.message.u2u;

/**
 * 用户申请添加好友请求
 * @author Lawliet
 *
 */
public class NotifyUserFrdApplyMessage {
	private String auid;//申请方用户uid
	private String an;//申请方用户昵称
	private String av; //申请方用户头像
	private String amno;//申请方用户手机号
	//private String am;//申请方用户的申请内容
	
	public String getAuid() {
		return auid;
	}
	public void setAuid(String auid) {
		this.auid = auid;
	}
	public String getAn() {
		return an;
	}
	public void setAn(String an) {
		this.an = an;
	}
	public String getAv() {
		return av;
	}
	public void setAv(String av) {
		this.av = av;
	}
	public String getAmno() {
		return amno;
	}
	public void setAmno(String amno) {
		this.amno = amno;
	}
}
