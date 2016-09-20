package com.bhu.vas.api.rpc.charging.model;

import java.util.Date;

import com.smartwork.msip.cores.orm.model.BaseIntModel;
@SuppressWarnings("serial")
public class UserIncomeMonthRank extends BaseIntModel{
	private int uid;
	//用户排名
	private int rank;
	//用户收入
	private String income;
	//用户前月排名
	private int beforeRank;
	//用户前月收入
	private String beforeIncome;
	
	private Date created_at;

	public int getUid() {
		return uid;
	}

	public void setUid(int uid) {
		this.uid = uid;
	}

	public int getRank() {
		return rank;
	}

	public void setRank(int rank) {
		this.rank = rank;
	}

	public String getIncome() {
		return income;
	}

	public void setIncome(String income) {
		this.income = income;
	}

	public int getBeforeRank() {
		return beforeRank;
	}

	public void setBeforeRank(int beforeRank) {
		this.beforeRank = beforeRank;
	}

	public String getBeforeIncome() {
		return beforeIncome;
	}

	public void setBeforeIncome(String beforeIncome) {
		this.beforeIncome = beforeIncome;
	}

	public Date getCreated_at() {
		return created_at;
	}

	public void setCreated_at(Date created_at) {
		this.created_at = created_at;
	}
	
}
