package com.bhu.vas.api.vto.device;

import java.util.List;

/**
 * id、nick、mobileno、avatar、设备访客相关限速
 * @author Edmond
 *
 */
@SuppressWarnings("serial")
public class UserSnkPortalVTO implements java.io.Serializable{
	private int id;
	private String nick;
	private String mobileno;
	private String avatar;
	private List<SnkPortalVTO> snks;
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
	public List<SnkPortalVTO> getSnks() {
		return snks;
	}
	public void setSnks(List<SnkPortalVTO> snks) {
		this.snks = snks;
	}
	
}
