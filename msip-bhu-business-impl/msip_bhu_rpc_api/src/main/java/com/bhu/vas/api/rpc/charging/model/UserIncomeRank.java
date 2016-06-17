package com.bhu.vas.api.rpc.charging.model;

import com.smartwork.msip.cores.orm.model.BaseStringModel;

@SuppressWarnings("serial")
public class UserIncomeRank extends BaseStringModel{
	//用户排名
	private int rank;
	//用户收入
	private String income;
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
	
}
