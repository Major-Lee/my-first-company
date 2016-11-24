package com.bhu.vas.api.vto.advertise;

import java.util.Date;

@SuppressWarnings("serial")
public class AdCommdityVTO implements java.io.Serializable{
	private String cash;
	private Date created_at;
	private Date nowDate = new Date();
	
	public String getCash() {
		return cash;
	}
	public void setCash(String cash) {
		this.cash = cash;
	}
	public Date getCreated_at() {
		return created_at;
	}
	public void setCreated_at(Date created_at) {
		this.created_at = created_at;
	}
	public Date getNowDate() {
		return nowDate;
	}
}
