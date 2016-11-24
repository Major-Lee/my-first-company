package com.bhu.vas.api.dto.commdity;

import com.bhu.vas.api.vto.advertise.AdCommdityVTO;

@SuppressWarnings("serial")
public class HotPlayOrderVTO implements java.io.Serializable{
	//订单号
	private String orderid;
	//金额
	private String amount;
	//appid
	private Integer appid;
	private AdCommdityVTO adCommdityVTO;
	public AdCommdityVTO getAdCommdityVTO() {
		return adCommdityVTO;
	}
	public void setAdCommdityVTO(AdCommdityVTO adCommdityVTO) {
		this.adCommdityVTO = adCommdityVTO;
	}
	public String getOrderid() {
		return orderid;
	}
	public void setOrderid(String orderid) {
		this.orderid = orderid;
	}
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
}
