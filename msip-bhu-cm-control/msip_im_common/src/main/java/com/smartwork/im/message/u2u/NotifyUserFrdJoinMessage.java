package com.smartwork.im.message.u2u;

/**
 * 新增好友消息
 * @author Lawliet
 *
 */
public class NotifyUserFrdJoinMessage {
	private String frdid;//好友uid
	private String fn;//好友昵称
	private String fv; //好友头像
	private String fmn;//好友手机号
	//好友匹配方式
	private String from;
	private String adn;//如果是第三方匹配的好友, 保存用户在第三方的名称
	//成为好友的时间
	private long frd_ts;
	
	public String getFrdid() {
		return frdid;
	}
	public void setFrdid(String frdid) {
		this.frdid = frdid;
	}
	public String getFn() {
		return fn;
	}
	public void setFn(String fn) {
		this.fn = fn;
	}
	public String getFv() {
		return fv;
	}
	public void setFv(String fv) {
		this.fv = fv;
	}
	public String getFmn() {
		return fmn;
	}
	public void setFmn(String fmn) {
		this.fmn = fmn;
	}
	public String getAdn() {
		return adn;
	}
	public void setAdn(String adn) {
		this.adn = adn;
	}
	public String getFrom() {
		return from;
	}
	public void setFrom(String from) {
		this.from = from;
	}
	public long getFrd_ts() {
		return frd_ts;
	}
	public void setFrd_ts(long frd_ts) {
		this.frd_ts = frd_ts;
	}
}
