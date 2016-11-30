package com.bhu.vas.api.vto.advertise;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

@SuppressWarnings("serial")
public class AdvertiseBillsVTO implements java.io.Serializable{
	private String expect;
	private String actual;
	private String balance;
	
	public String getExpect() {
		return expect;
	}
	public void setExpect(float expect) {
		this.expect = cutDecimal(expect);
	}
	public String getActual() {
		return actual;
	}
	public void setActual(float actual) {
		this.actual = cutDecimal(actual);
	}
	public String getBalance() {
		return balance;
	}
	public void setBalance(float balance) {
		this.balance = cutDecimal(balance);
	}
	
	private String cutDecimal (float f){
		DecimalFormat formater = new DecimalFormat();
		formater.setMaximumFractionDigits(2);
		formater.setGroupingSize(0);
		formater.setRoundingMode(RoundingMode.FLOOR);
		return formater.format(f);
	}
}
