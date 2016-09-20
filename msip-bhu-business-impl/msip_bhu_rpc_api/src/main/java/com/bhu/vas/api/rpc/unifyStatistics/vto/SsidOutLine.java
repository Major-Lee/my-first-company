package com.bhu.vas.api.rpc.unifyStatistics.vto;

import java.io.Serializable;
@SuppressWarnings("serial")
public class SsidOutLine implements Serializable{
	//上线数	
	private int doc;
	//收入
	private String income;
	//占比
	private double rate;
	
	public double getRate() {
		return rate;
	}
	public void setRate(double rate) {
		this.rate = rate;
	}
	public int getDoc() {
		return doc;
	}
	public void setDoc(int doc) {
		this.doc = doc;
	}
	public String getIncome() {
		return income;
	}
	public void setIncome(String income) {
		this.income = income;
	}
	
}
