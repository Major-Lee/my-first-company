package com.bhu.vas.api.vto.agent;

import java.io.Serializable;

public class UserActivityVTO implements Serializable{
	private int bind_num;
	private double income;
	private String rate;
	private int status;
	public int getBind_num() {
		return bind_num;
	}
	public void setBind_num(int bind_num) {
		this.bind_num = bind_num;
	}
	public double getIncome() {
		return income;
	}
	public void setIncome(double income) {
		this.income = income;
	}
	public String getRate() {
		return rate;
	}
	public void setRate(String rate) {
		this.rate = rate;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	
}
