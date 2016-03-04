package com.bhu.vas.api.rpc.user.dto;

import java.util.List;

import com.bhu.vas.api.vto.wallet.UserWalletDetailVTO;
import com.smartwork.msip.business.token.UserTokenDTO;

@SuppressWarnings("serial")
public class UserInnerExchangeDTO implements java.io.Serializable{
	private UserDTO user;
	private UserTokenDTO token;
	private UserWalletDetailVTO wallet;
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
	
	public UserWalletDetailVTO getWallet() {
		return wallet;
	}
	public void setWallet(UserWalletDetailVTO wallet) {
		this.wallet = wallet;
	}
	public static UserInnerExchangeDTO build(UserDTO user,UserTokenDTO token){
		return build(user,token);
	}
	public static UserInnerExchangeDTO build(UserDTO user,UserTokenDTO token,UserWalletDetailVTO wallet){
		UserInnerExchangeDTO ret = new UserInnerExchangeDTO();
		ret.setUser(user);
		ret.setToken(token);
		ret.setWallet(wallet);
		return ret;
	}
}
