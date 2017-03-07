package com.bhu.vas.api.rpc.charging.model;

import com.smartwork.msip.cores.orm.model.BaseIntModel;

@SuppressWarnings("serial")
public class StatisticFincialIncome extends BaseIntModel{
	private String dayid;
	private String total_income;
	private String bhu_income;
	private String user_income;
	private String withdraw_past;
	private String total_cash;
	
	public String getDayid() {
		return dayid;
	}
	public void setDayid(String dayid) {
		this.dayid = dayid;
	}
	public String getTotal_income() {
		return total_income;
	}
	public void setTotal_income(String total_income) {
		this.total_income = total_income;
	}
	public String getBhu_income() {
		return bhu_income;
	}
	public void setBhu_income(String bhu_income) {
		this.bhu_income = bhu_income;
	}
	public String getUser_income() {
		return user_income;
	}
	public void setUser_income(String user_income) {
		this.user_income = user_income;
	}
	public String getWithdraw_past() {
		return withdraw_past;
	}
	public void setWithdraw_past(String withdraw_past) {
		this.withdraw_past = withdraw_past;
	}
	public String getTotal_cash() {
		return total_cash;
	}
	public void setTotal_cash(String total_cash) {
		this.total_cash = total_cash;
	}
}
