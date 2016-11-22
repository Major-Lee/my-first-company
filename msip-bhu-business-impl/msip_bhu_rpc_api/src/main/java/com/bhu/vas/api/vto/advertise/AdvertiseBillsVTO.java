package com.bhu.vas.api.vto.advertise;

public class AdvertiseBillsVTO {
	private String expect;
	private int actual;
	private int balance;
	
	public String getExpect() {
		return expect;
	}
	public void setExpect(String string) {
		this.expect = string;
	}
	public int getActual() {
		return actual;
	}
	public void setActual(int actual) {
		this.actual = actual;
	}
	public int getBalance() {
		return balance;
	}
	public void setBalance(int balance) {
		this.balance = balance;
	}
}
