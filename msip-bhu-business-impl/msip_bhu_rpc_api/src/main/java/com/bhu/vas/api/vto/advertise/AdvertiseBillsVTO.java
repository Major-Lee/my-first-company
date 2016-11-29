package com.bhu.vas.api.vto.advertise;

import java.math.BigDecimal;

@SuppressWarnings("serial")
public class AdvertiseBillsVTO implements java.io.Serializable{
	private float expect;
	private float actual;
	private float balance;
	
	public float getExpect() {
		return expect;
	}
	public void setExpect(float expect) {
		this.expect = cutDecimal(expect);
	}
	public float getActual() {
		return actual;
	}
	public void setActual(float actual) {
		this.actual = cutDecimal(actual);
	}
	public float getBalance() {
		return balance;
	}
	public void setBalance(float balance) {
		this.balance = cutDecimal(balance);
	}
	
	private float cutDecimal (float f){
		 BigDecimal   b  =   new BigDecimal(f);  
		 return b.setScale(2, BigDecimal.ROUND_DOWN).floatValue();  
	}
}
