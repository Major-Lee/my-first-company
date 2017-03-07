package com.bhu.vas.api.dto.user;

import com.bhu.vas.api.rpc.user.dto.UserInnerExchangeDTO;

@SuppressWarnings("serial")
public class UserIdentityAuthVTO implements java.io.Serializable{
	private String mobileno;
	private boolean isAuthorize;
	private Integer uid;
	private UserInnerExchangeDTO user;
	
	
	public String getMobileno() {
		return mobileno;
	}
	public void setMobileno(String mobileno) {
		this.mobileno = mobileno;
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
	public UserInnerExchangeDTO getUser() {
		return user;
	}
	public void setUser(UserInnerExchangeDTO user) {
		this.user = user;
	}
}