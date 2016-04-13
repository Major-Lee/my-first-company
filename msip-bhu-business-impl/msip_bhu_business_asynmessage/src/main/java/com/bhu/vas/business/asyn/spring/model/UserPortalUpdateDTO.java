package com.bhu.vas.business.asyn.spring.model;

import com.bhu.vas.business.asyn.spring.builder.ActionDTO;
import com.bhu.vas.business.asyn.spring.builder.ActionMessageType;

public class UserPortalUpdateDTO extends ActionDTO {
	public static String PortalUpdate_User = "User";
	public static String PortalUpdate_SNK = "Snk";
	
	//update type 
	private String type;
	
	private String nick;
	private String mobileno;
	private String avatar;
	
	//共享网络类型
	private String snk;
	//模板
	private String tpl;
	//限速
	private int users_rate;
	public String getTpl() {
		return tpl;
	}
	public void setTpl(String tpl) {
		this.tpl = tpl;
	}
	
	public String getSnk() {
		return snk;
	}
	public void setSnk(String snk) {
		this.snk = snk;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getNick() {
		return nick;
	}
	public void setNick(String nick) {
		this.nick = nick;
	}
	public String getMobileno() {
		return mobileno;
	}
	public void setMobileno(String mobileno) {
		this.mobileno = mobileno;
	}
	public String getAvatar() {
		return avatar;
	}
	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}
	public int getUsers_rate() {
		return users_rate;
	}
	public void setUsers_rate(int users_rate) {
		this.users_rate = users_rate;
	}
	@Override
	public String getActionType() {
		return ActionMessageType.UserPortalUpdate.getPrefix();
	}
}
