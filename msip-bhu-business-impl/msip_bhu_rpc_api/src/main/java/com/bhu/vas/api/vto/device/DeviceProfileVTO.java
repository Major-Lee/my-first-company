package com.bhu.vas.api.vto.device;

/**
 * id、nick、mobileno、avatar、设备访客相关限速
 * @author Edmond
 *
 */
@SuppressWarnings("serial")
public class DeviceProfileVTO implements java.io.Serializable{
	private int id;
	private String nick;
	private String mobileno;
	private String avatar;
	private String mac;
	//限速
	private int users_rate;
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
	public String getMac() {
		return mac;
	}
	public void setMac(String mac) {
		this.mac = mac;
	}
}
