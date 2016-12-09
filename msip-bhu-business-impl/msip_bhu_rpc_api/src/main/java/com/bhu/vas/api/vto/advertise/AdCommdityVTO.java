package com.bhu.vas.api.vto.advertise;

import java.util.Date;

import com.smartwork.msip.cores.helper.ArithHelper;

@SuppressWarnings("serial")
public class AdCommdityVTO implements java.io.Serializable{
	private String cash;
	private int type;
	private Date created_at;
	private Date nowDate = new Date();
	
	public String getCash() {
		return cash;
	}
	public void setCash(String cash) {
		this.cash = ArithHelper.getCuttedCurrency(cash);
	}
	
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
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
