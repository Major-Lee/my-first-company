package com.bhu.vas.api.vto.bill;

import java.util.List;

/**
 * 
 * @author Edmond
 *
 */
@SuppressWarnings("serial")
public class UserBillVTO implements java.io.Serializable {
	
	private String startTime;
	private String endTtime;
	private String totalBeginIncome; //期初
	private String totalEndIncome; //期末
	private String totalMonthCount; //交易数
	private String totalMonthIncome; //交易数
	private String totalWithdrawPast; //往期已提现
	private String totalWithdrawApply; //本次提现数
	private String totalCash; //钱包余额
	private String totalBalance; //误差

	
	private List<UserBillMonthVTO> monthBill;


	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTtime() {
		return endTtime;
	}

	public void setEndTtime(String endTtime) {
		this.endTtime = endTtime;
	}

	public String getTotalBeginIncome() {
		return totalBeginIncome;
	}

	public void setTotalBeginIncome(String totalBeginIncome) {
		this.totalBeginIncome = totalBeginIncome;
	}

	public String getTotalEndIncome() {
		return totalEndIncome;
	}

	public void setTotalEndIncome(String totalEndIncome) {
		this.totalEndIncome = totalEndIncome;
	}

	public String getTotalMonthCount() {
		return totalMonthCount;
	}

	public void setTotalMonthCount(String totalMonthCount) {
		this.totalMonthCount = totalMonthCount;
	}

	public String getTotalMonthIncome() {
		return totalMonthIncome;
	}

	public void setTotalMonthIncome(String totalMonthIncome) {
		this.totalMonthIncome = totalMonthIncome;
	}

	public String getTotalWithdrawPast() {
		return totalWithdrawPast;
	}

	public void setTotalWithdrawPast(String totalWithdrawPast) {
		this.totalWithdrawPast = totalWithdrawPast;
	}

	public String getTotalWithdrawApply() {
		return totalWithdrawApply;
	}

	public void setTotalWithdrawApply(String totalWithdrawApply) {
		this.totalWithdrawApply = totalWithdrawApply;
	}

	public String getTotalCash() {
		return totalCash;
	}

	public void setTotalCash(String totalCash) {
		this.totalCash = totalCash;
	}

	public String getTotalBalance() {
		return totalBalance;
	}

	public void setTotalBalance(String totalBalance) {
		this.totalBalance = totalBalance;
	}

	public List<UserBillMonthVTO> getMonthBill() {
		return monthBill;
	}

	public void setMonthBill(List<UserBillMonthVTO> monthBill) {
		this.monthBill = monthBill;
	}
}
