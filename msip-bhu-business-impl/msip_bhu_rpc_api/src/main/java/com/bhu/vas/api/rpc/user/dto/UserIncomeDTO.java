package com.bhu.vas.api.rpc.user.dto;

import java.util.List;
@SuppressWarnings("serial")
public class UserIncomeDTO implements java.io.Serializable{
	
	//钱包余额
	private String walletMoney;
	//虎钻
	private String vcurrency;
	//活动返现
	private String activityBackCash;
	//历史总收益
	private double totalIncome;
	//提现信息
	private String withdraw;
	//打赏页面uv
	private String rewardUV;
	//订单数
	private String totalOrderNum;
	//打赏成功数
	private String rewardSuccessNum;
	//订单成功率
	private String orderSuccessRate;
	//用户交易信息
	private List<UserTransInfoDTO> userTransInfoList;
	public String getWalletMoney() {
		return walletMoney;
	}
	public void setWalletMoney(String walletMoney) {
		this.walletMoney = walletMoney;
	}
	public String getVcurrency() {
		return vcurrency;
	}
	public void setVcurrency(String vcurrency) {
		this.vcurrency = vcurrency;
	}
	public String getActivityBackCash() {
		return activityBackCash;
	}
	public void setActivityBackCash(String activityBackCash) {
		this.activityBackCash = activityBackCash;
	}
	
	public double getTotalIncome() {
		return totalIncome;
	}
	public void setTotalIncome(double totalIncome) {
		this.totalIncome = totalIncome;
	}
	public String getWithdraw() {
		return withdraw;
	}
	public void setWithdraw(String withdraw) {
		this.withdraw = withdraw;
	}
	public String getRewardUV() {
		return rewardUV;
	}
	public void setRewardUV(String rewardUV) {
		this.rewardUV = rewardUV;
	}
	public String getTotalOrderNum() {
		return totalOrderNum;
	}
	public void setTotalOrderNum(String totalOrderNum) {
		this.totalOrderNum = totalOrderNum;
	}
	public String getRewardSuccessNum() {
		return rewardSuccessNum;
	}
	public void setRewardSuccessNum(String rewardSuccessNum) {
		this.rewardSuccessNum = rewardSuccessNum;
	}
	public String getOrderSuccessRate() {
		return orderSuccessRate;
	}
	public void setOrderSuccessRate(String orderSuccessRate) {
		this.orderSuccessRate = orderSuccessRate;
	}
	public List<UserTransInfoDTO> getUserTransInfoList() {
		return userTransInfoList;
	}
	public void setUserTransInfoList(List<UserTransInfoDTO> userTransInfoList) {
		this.userTransInfoList = userTransInfoList;
	}
	
}
