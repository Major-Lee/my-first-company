package com.bhu.vas.api.user.model;

import java.util.Date;

import org.apache.commons.lang.StringUtils;

import com.smartwork.msip.cores.orm.model.BaseStringModel;
@SuppressWarnings("serial")
public class User extends BaseStringModel{
	
	private int score;
	private String reg_coordination;
	private String lastlogin_coordination;
	private String regdevice;
	private String regip;
	private int usertype;
	
	private Date created_at;
	public User() {
		super();
	}
	public User(String userid){//,String email){
		super();
		this.id = userid;
		//this.email = email;
	}
	/*public User(String email, String plainpwd) {
		super();
		this.email = email;
		this.plainpwd = plainpwd;
	}*/	

	@Override
	public void preInsert() {
		if (this.created_at == null)
			this.created_at = new Date();
		super.preInsert();
	}
	
	
	public String getRegdevice() {
		if(StringUtils.isEmpty(regdevice)) return DeviceEnum.PC.getSname();
		return regdevice;
	}
	public void setRegdevice(String regdevice) {
		this.regdevice = regdevice;
	}
	public int getScore() {
		return score;
	}
	public void setScore(int score) {
		this.score = score;
	}
	public String getReg_coordination() {
		return reg_coordination;
	}
	public void setReg_coordination(String reg_coordination) {
		this.reg_coordination = reg_coordination;
	}
	public String getLastlogin_coordination() {
		return lastlogin_coordination;
	}
	public void setLastlogin_coordination(String lastlogin_coordination) {
		this.lastlogin_coordination = lastlogin_coordination;
	}
	public String getRegip() {
		return regip;
	}
	public void setRegip(String regip) {
		this.regip = regip;
	}
	public int getUsertype() {
		return usertype;
	}
	public void setUsertype(int usertype) {
		this.usertype = usertype;
	}
	public Date getCreated_at() {
		return created_at;
	}
	public void setCreated_at(Date created_at) {
		this.created_at = created_at;
	}
}
