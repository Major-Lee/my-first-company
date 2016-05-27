package com.bhu.vas.api.rpc.user.dto;

import com.smartwork.msip.cores.helper.ArithHelper;


@SuppressWarnings("serial")
public class ShareDealDailyUserSummaryProcedureVTO implements java.io.Serializable{
	private int userid;
	private String cdate;
	
	private double total_cash;
	private int total_nums;
	
	public int getUserid() {
		return userid;
	}

	public void setUserid(int userid) {
		this.userid = userid;
	}

	public double getTotal_cash() {
		if(total_cash > 0.00d){
			return ArithHelper.round(total_cash, 2);
		}else{
			return 0.00d;
		}
		//return total_cash;
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

	public String getCdate() {
		return cdate;
	}

	public void setCdate(String cdate) {
		this.cdate = cdate;
	}

}
