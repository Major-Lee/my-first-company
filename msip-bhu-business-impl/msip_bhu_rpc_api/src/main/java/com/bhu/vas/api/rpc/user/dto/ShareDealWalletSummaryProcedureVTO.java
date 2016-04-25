package com.bhu.vas.api.rpc.user.dto;


@SuppressWarnings("serial")
public class ShareDealWalletSummaryProcedureVTO implements java.io.Serializable{
	private int userid;
	private String today_date;
	private double today_cash;
	private int today_nums;
	
	private String yesterday_date;
	private double yesterday_cash;
	private int yesterday_nums;
	
	private double total_cash;
	private int total_nums;
	
	private int ods;//online devices
	public int getUserid() {
		return userid;
	}

	public void setUserid(int userid) {
		this.userid = userid;
	}

	public double getToday_cash() {
		return today_cash;
	}

	public void setToday_cash(double today_cash) {
		this.today_cash = today_cash;
	}

	public int getToday_nums() {
		return today_nums;
	}

	public void setToday_nums(int today_nums) {
		this.today_nums = today_nums;
	}

	public double getYesterday_cash() {
		return yesterday_cash;
	}

	public void setYesterday_cash(double yesterday_cash) {
		this.yesterday_cash = yesterday_cash;
	}

	public int getYesterday_nums() {
		return yesterday_nums;
	}

	public void setYesterday_nums(int yesterday_nums) {
		this.yesterday_nums = yesterday_nums;
	}

	public double getTotal_cash() {
		return total_cash;
	}

	public void setTotal_cash(double total_cash) {
		this.total_cash = total_cash;
	}

	public int getTotal_nums() {
		return total_nums;
	}

	public void setTotal_nums(int total_nums) {
		this.total_nums = total_nums;
	}

	public int getOds() {
		return ods;
	}

	public void setOds(int ods) {
		this.ods = ods;
	}

	public String getToday_date() {
		return today_date;
	}

	public void setToday_date(String today_date) {
		this.today_date = today_date;
	}

	public String getYesterday_date() {
		return yesterday_date;
	}

	public void setYesterday_date(String yesterday_date) {
		this.yesterday_date = yesterday_date;
	}
}
