package com.bhu.vas.api.dto.user;

@SuppressWarnings("serial")
public class UserIdentityAuthVTO implements java.io.Serializable{
	private String mobileno;
	private String created_at;
	private boolean isAuthorize;
	private Integer uid;
	private String nick;
	
	
	public String getMobileno() {
		return mobileno;
	}
	public void setMobileno(String mobileno) {
		this.mobileno = mobileno;
	}
	public String getCreated_at() {
		return created_at;
	}
	public void setCreated_at(String created_at) {
		this.created_at = created_at;
	}
	public boolean isAuthorize() {
		return isAuthorize;
	}
	public void setAuthorize(boolean isAuthorize) {
		this.isAuthorize = isAuthorize;
	}
	public Integer getUid() {
		return uid;
	}
	public void setUid(Integer uid) {
		this.uid = uid;
	}
	public String getNick() {
		return nick;
	}
	public void setNick(String nick) {
		this.nick = nick;
	}
}