package com.bhu.vas.api.rpc.user.dto;
/**
 * OAuth用户数据DTO
 * @author Edmond Lee
 *
 */
@SuppressWarnings("serial")
public class UserOAuthStateDTO implements java.io.Serializable{
	private String auid;
	private String nick;
	private String avatar;
	private String identify;
	
	public String getAuid() {
		return auid;
	}
	public void setAuid(String auid) {
		this.auid = auid;
	}
	public String getNick() {
		return nick;
	}
	public void setNick(String nick) {
		this.nick = nick;
	}
	public String getAvatar() {
		return avatar;
	}
	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}
	public String getIdentify() {
		return identify;
	}
	public void setIdentify(String identify) {
		this.identify = identify;
	}
}
