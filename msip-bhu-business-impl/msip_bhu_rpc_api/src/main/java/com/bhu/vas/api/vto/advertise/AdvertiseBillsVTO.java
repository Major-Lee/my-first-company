package com.bhu.vas.api.vto.advertise;

import com.smartwork.msip.cores.helper.ArithHelper;

@SuppressWarnings("serial")
public class AdvertiseBillsVTO implements java.io.Serializable{
	private String expect;
	private String actual;
	private String balance;
	
	public String getExpect() {
		return expect;
	}
	public void setExpect(float expect) {
		this.expect = ArithHelper.getCuttedCurrency(expect+"");
	}

	public String getActual() {
		return actual;
	}
	public void setActual(float actual) {
		this.actual = ArithHelper.getCuttedCurrency(actual+"");
	}
	public String getBalance() {
		return balance;
	}
	public void setBalance(float balance) {
		this.balance = ArithHelper.getCuttedCurrency(balance+"");
	}
}
