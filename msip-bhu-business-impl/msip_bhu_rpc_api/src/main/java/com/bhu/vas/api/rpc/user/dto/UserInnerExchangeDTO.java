package com.bhu.vas.api.rpc.user.dto;

import java.util.List;

import com.smartwork.msip.business.token.UserTokenDTO;

@SuppressWarnings("serial")
public class UserInnerExchangeDTO implements java.io.Serializable{
	private UserDTO user;
	private UserTokenDTO token;
	private List<UserOAuthStateDTO> oauths;
	
	public UserDTO getUser() {
		return user;
	}
	public void setUser(UserDTO user) {
		this.user = user;
	}
	public UserTokenDTO getToken() {
		return token;
	}
	public void setToken(UserTokenDTO token) {
		this.token = token;
	}
	public List<UserOAuthStateDTO> getOauths() {
		return oauths;
	}
	public void setOauths(List<UserOAuthStateDTO> oauths) {
		this.oauths = oauths;
	}
	
	public static UserInnerExchangeDTO build(UserDTO user,UserTokenDTO token){
		UserInnerExchangeDTO ret = new UserInnerExchangeDTO();
		ret.setUser(user);
		ret.setToken(token);
		return ret;
	}
}
