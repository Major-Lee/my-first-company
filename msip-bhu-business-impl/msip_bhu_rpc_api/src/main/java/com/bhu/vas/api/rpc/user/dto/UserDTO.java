package com.bhu.vas.api.rpc.user.dto;

@SuppressWarnings("serial")
public class UserDTO implements java.io.Serializable{
//	public static final int UT_Normal = 0;//正常用户
//	public static final int UT_System = 1;//系统用户
	
	private int id;
	private String nick;
	private String avatar;
	private String mobileno;
	
	private int ut;//用户类型
	private boolean ow;//是否是单向好友
	private boolean frdshp;//是否是双向好友
	private long lastlogin_ts; //用户的最后登陆时间
	private boolean zombie;//是否是僵尸用户
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
	public String getAvatar() {
		return avatar;
	}
	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}
	public String getMobileno() {
		return mobileno;
	}
	public void setMobileno(String mobileno) {
		this.mobileno = mobileno;
	}
	public int getUt() {
		return ut;
	}
	public void setUt(int ut) {
		this.ut = ut;
	}
	public boolean isFrdshp() {
		return frdshp;
	}
	public void setFrdshp(boolean frdshp) {
		this.frdshp = frdshp;
	}
	public boolean isOw() {
		return ow;
	}
	public void setOw(boolean ow) {
		this.ow = ow;
	}
	public long getLastlogin_ts() {
		return lastlogin_ts;
	}
	public void setLastlogin_ts(long lastlogin_ts) {
		this.lastlogin_ts = lastlogin_ts;
	}
	public boolean isZombie() {
		return zombie;
	}
	public void setZombie(boolean zombie) {
		this.zombie = zombie;
	}
	
}
