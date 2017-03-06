package com.bhu.vas.api.vto.bill;

/**
 * 
 * @author Edmond
 *
 */
@SuppressWarnings("serial")
public class UserBillMonthVTO implements java.io.Serializable {
	private String date;
	private String beginIncome; //期初
	private String endIncome; //期末
	private String monthCount; //交易数
	private String monthIncome; //交易数
	private String withdrawPast; //往期已提现
	private String withdrawApply; //本次提现数
	private String cash; //钱包余额
	private String balance; //误差
	
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getBeginIncome() {
		return beginIncome;
	}
	public void setBeginIncome(String beginIncome) {
		this.beginIncome = beginIncome;
	}
	public String getEndIncome() {
		return endIncome;
	}
	public void setEndIncome(String endIncome) {
		this.endIncome = endIncome;
	}
	public String getMonthCount() {
		return monthCount;
	}
	public void setMonthCount(String monthCount) {
		this.monthCount = monthCount;
	}
	public String getMonthIncome() {
		return monthIncome;
	}
	public void setMonthIncome(String monthIncome) {
		this.monthIncome = monthIncome;
	}
	public String getWithdrawPast() {
		return withdrawPast;
	}
	public void setWithdrawPast(String withdrawPast) {
		this.withdrawPast = withdrawPast;
	}
	public String getWithdrawApply() {
		return withdrawApply;
	}
	public void setWithdrawApply(String withdrawApply) {
		this.withdrawApply = withdrawApply;
	}
	public String getCash() {
		return cash;
	}
	public void setCash(String cash) {
		this.cash = cash;
	}
	public String getBalance() {
		return balance;
	}
	public void setBalance(String balance) {
		this.balance = balance;
	}
}
