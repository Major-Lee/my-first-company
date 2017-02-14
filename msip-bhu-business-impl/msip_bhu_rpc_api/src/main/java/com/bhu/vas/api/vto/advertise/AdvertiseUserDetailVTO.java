package com.bhu.vas.api.vto.advertise;

import java.util.Map;

@SuppressWarnings("serial")
public class AdvertiseUserDetailVTO implements java.io.Serializable{
	
	private Map<String, String> tips;
	private double balance;

	public Map<String, String> getTips() {
		return tips;
	}
	public void setTips(Map<String, String> tips) {
		this.tips = tips;
	}
	public double getBalance() {
		return balance;
	}
	public void setBalance(double balance) {
		this.balance = balance;
	}
}
