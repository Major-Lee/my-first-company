package com.bhu.vas.api.rpc.user.dto;

import org.apache.commons.lang.StringUtils;

import com.smartwork.msip.cores.helper.StringHelper;

@SuppressWarnings("serial")
public class UserDTO implements java.io.Serializable{
	private int id;
	private int countrycode;
	private String mobileno;
	private String nick;
	
	private String avatar;
	private String sex;
	private String org;
	private String memo;
	//是否是注册  true 注册  false 登录
	private boolean reg = false;
	private int utype;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
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
	public int getCountrycode() {
		return countrycode;
	}
	public void setCountrycode(int countrycode) {
		this.countrycode = countrycode;
	}
	public boolean isReg() {
		return reg;
	}
	public void setReg(boolean reg) {
		this.reg = reg;
	}
	
	public int getUtype() {
		return utype;
	}
	public void setUtype(int utype) {
		this.utype = utype;
	}
	
	public String getAvatar() {
		return avatar;
	}
	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}
	public String getSex() {
		return sex;
	}
	public void setSex(String sex) {
		this.sex = sex;
	}
	
	public String getOrg() {
		return org;
	}
	public void setOrg(String org) {
		this.org = org;
	}
	
	public String getMemo() {
		return memo;
	}
	public void setMemo(String memo) {
		this.memo = memo;
	}
	public UserDTO() {
	}
	public UserDTO(int id, int countrycode, String mobileno, String nick,int utype,
			boolean reg) {
		super();
		this.id = id;
		this.countrycode = countrycode;
		this.mobileno = StringUtils.isEmpty(mobileno)?StringHelper.EMPTY_STRING_GAP:mobileno;
		this.nick = StringUtils.isEmpty(nick)?StringHelper.EMPTY_STRING_GAP:nick;
		this.utype = utype;
		this.reg = reg;
	}
	
}
