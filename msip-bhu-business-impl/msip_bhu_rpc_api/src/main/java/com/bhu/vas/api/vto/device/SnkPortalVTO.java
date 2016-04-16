package com.bhu.vas.api.vto.device;

/**
 * id、nick、mobileno、avatar、设备访客相关限速
 * @author Edmond
 *
 */
@SuppressWarnings("serial")
public class SnkPortalVTO implements java.io.Serializable{
	private String tpl;
	//限速
	private int users_rate;
	public String getTpl() {
		return tpl;
	}
	public void setTpl(String tpl) {
		this.tpl = tpl;
	}
	public int getUsers_rate() {
		return users_rate;
	}
	public void setUsers_rate(int users_rate) {
		this.users_rate = users_rate;
	}
	
}
