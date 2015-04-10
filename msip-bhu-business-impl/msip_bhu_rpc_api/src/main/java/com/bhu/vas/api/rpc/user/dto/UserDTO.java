package com.bhu.vas.api.rpc.user.dto;

@SuppressWarnings("serial")
public class UserDTO implements java.io.Serializable{
//	public static final int UT_Normal = 0;//正常用户
//	public static final int UT_System = 1;//系统用户
	
	private int id;
	private String nick;
	private int countrycode;
	//private String avatar;
	private String mobileno;
	private String atoken;
	private String rtoken;
	//是否是注册  true 注册  false 登录
	private boolean reg = false;
	/*private int ut;//用户类型
	private boolean ow;//是否是单向好友
	private boolean frdshp;//是否是双向好友
	private long lastlogin_ts; //用户的最后登陆时间
	private boolean zombie;//是否是僵尸用户
*/	public int getId() {
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
	/*public String getAvatar() {
		return avatar;
	}
	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}*/
	public String getMobileno() {
		return mobileno;
	}
	public void setMobileno(String mobileno) {
		this.mobileno = mobileno;
	}
	public String getAtoken() {
		return atoken;
	}
	public void setAtoken(String atoken) {
		this.atoken = atoken;
	}
	public int getCountrycode() {
		return countrycode;
	}
	public void setCountrycode(int countrycode) {
		this.countrycode = countrycode;
	}
	public String getRtoken() {
		return rtoken;
	}
	public void setRtoken(String rtoken) {
		this.rtoken = rtoken;
	}
	public boolean isReg() {
		return reg;
	}
	public void setReg(boolean reg) {
		this.reg = reg;
	}
	
}
