package com.bhu.vas.api.rpc.charging.vto;

@SuppressWarnings("serial")
public class StatisticFincialMonthVTO {
	private String monthid;
	private int utype;
	private String nick;
	private String mobileno;
	private String begin_income;
	private String end_income;
	private String month_count;
	private String month_income;
	private String withdraw_past;
	private String withdraw_apply;
	private String cash;
	public String getMonthid() {
		return monthid;
	}
	public void setMonthid(String monthid) {
		this.monthid = monthid;
	}
	public int getUtype() {
		return utype;
	}
	public void setUtype(int utype) {
		this.utype = utype;
	}
	public String getNick() {
		return nick;
	}
	public void setNick(String nick) {
		this.nick = nick;
	}
	public String getMobileno() {
		return mobileno;
	}
	public void setMobileno(String mobileno) {
		this.mobileno = mobileno;
	}
	public String getBegin_income() {
		return begin_income;
	}
	public void setBegin_income(String begin_income) {
		this.begin_income = begin_income;
	}
	public String getEnd_income() {
		return end_income;
	}
	public void setEnd_income(String end_income) {
		this.end_income = end_income;
	}
	public String getMonth_count() {
		return month_count;
	}
	public void setMonth_count(String month_count) {
		this.month_count = month_count;
	}
	public String getMonth_income() {
		return month_income;
	}
	public void setMonth_income(String month_income) {
		this.month_income = month_income;
	}
	public String getWithdraw_past() {
		return withdraw_past;
	}
	public void setWithdraw_past(String withdraw_past) {
		this.withdraw_past = withdraw_past;
	}
	public String getWithdraw_apply() {
		return withdraw_apply;
	}
	public void setWithdraw_apply(String withdraw_apply) {
		this.withdraw_apply = withdraw_apply;
	}
	public String getCash() {
		return cash;
	}
	public void setCash(String cash) {
		this.cash = cash;
	}
}
