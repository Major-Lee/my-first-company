package com.bhu.vas.api.rpc.user.dto;

import java.math.BigDecimal;

@SuppressWarnings("serial")
public class ShareDealWalletSummaryProcedureVTO implements java.io.Serializable{
	private int userid;
	private String today_date;
	private float today_cash;
	private int today_nums;
	
	private String yesterday_date;
	private float yesterday_cash;
	private int yesterday_nums;
	
	private float total_cash;
	private int total_nums;
	
	private int ods;//online devices
	public int getUserid() {
		return userid;
	}

	public void setUserid(int userid) {
		this.userid = userid;
	}

	public float getToday_cash() {
		if(today_cash > 0.00f){
			return formatFloat(today_cash);
		}else{
			return 0.00f;
		}
	}

	public void setToday_cash(float today_cash) {
		this.today_cash = today_cash;
	}

	public int getToday_nums() {
		return today_nums;
	}

	public void setToday_nums(int today_nums) {
		this.today_nums = today_nums;
	}

	public float getYesterday_cash() {
		if(yesterday_cash > 0.00f){			
			return formatFloat(yesterday_cash);
		}else{
			return 0.00f;
		}
		//return yesterday_cash;
	}

	public void setYesterday_cash(float yesterday_cash) {
		this.yesterday_cash = yesterday_cash;
	}

	public int getYesterday_nums() {
		return yesterday_nums;
	}

	public void setYesterday_nums(int yesterday_nums) {
		this.yesterday_nums = yesterday_nums;
	}

	public float getTotal_cash() {
		if(total_cash > 0.00f){			
			return formatFloat(total_cash);
		}else{
			return 0.00f;
		}
		//return total_cash;
	}

	public void setTotal_cash(float total_cash) {
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
	
	private float formatFloat(float num){
        BigDecimal b = new BigDecimal(num);
	    BigDecimal one = new BigDecimal("1");
		
		return b.divide(one,2,BigDecimal.ROUND_HALF_UP).floatValue();
	}
	
	public static void main(String[] args) {
		BigDecimal b = new BigDecimal(0.1455d);
		 BigDecimal one = new BigDecimal("1");
		 
		float f = b.divide(one,2,BigDecimal.ROUND_HALF_UP).floatValue();
		System.out.println(f);
	}
	
}
