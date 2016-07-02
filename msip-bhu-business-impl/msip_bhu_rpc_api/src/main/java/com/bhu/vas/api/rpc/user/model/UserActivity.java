package com.bhu.vas.api.rpc.user.model;

import java.util.Date;

import com.smartwork.msip.cores.orm.model.BaseIntModel;

@SuppressWarnings("serial")
public class UserActivity extends BaseIntModel{
	private int bind_num;
	private double income;
	private String rate;
	private int status;
	private Date created_at;
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
	public Date getCreated_at() {
		return created_at;
	}
	public void setCreated_at(Date created_at) {
		this.created_at = created_at;
	}
	
}
