package com.bhu.vas.api.vto.advertise;

@SuppressWarnings("serial")
public class AdvertiseBillsVTO implements java.io.Serializable{
	private float expect;
	private float actual;
	private float balance;
	
	public float getExpect() {
		return expect;
	}
	public void setExpect(float string) {
		this.expect = string;
	}
	public float getActual() {
		return actual;
	}
	public void setActual(float actual) {
		this.actual = actual;
	}
	public float getBalance() {
		return balance;
	}
	public void setBalance(float balance) {
		this.balance = balance;
	}
}
