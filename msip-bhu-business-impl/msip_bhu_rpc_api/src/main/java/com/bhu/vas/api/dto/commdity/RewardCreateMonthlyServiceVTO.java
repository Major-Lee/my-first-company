package com.bhu.vas.api.dto.commdity;

@SuppressWarnings("serial")
public class RewardCreateMonthlyServiceVTO implements java.io.Serializable{
	//订单号
	private String orderid;
	//金额
	private String amount;
	//appid
	private Integer appid;
	public String getAmount() {
		return amount;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}
	public Integer getAppid() {
		return appid;
	}
	public void setAppid(Integer appid) {
		this.appid = appid;
	}
	public String getOrderid() {
		return orderid;
	}
	public void setOrderid(String orderid) {
		this.orderid = orderid;
	}
	
}
