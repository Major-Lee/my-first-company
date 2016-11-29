package com.bhu.vas.api.vto.advertise;

@SuppressWarnings("serial")
public class AdvertiseBillsVTO implements java.io.Serializable{
	private double expect;
	private double actual;
	private double balance;
	
	public double getExpect() {
		return expect;
	}
	public void setExpect(double string) {
		this.expect = string;
	}
	public double getActual() {
		return actual;
	}
	public void setActual(double actual) {
		this.actual = actual;
	}
	public double getBalance() {
		return balance;
	}
	public void setBalance(double balance) {
		this.balance = balance;
	}
}
