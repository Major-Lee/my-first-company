package com.bhu.vas.api.dto.commdity;


/**
 * 订单dto
 * @author tangzichao
 *
 */
@SuppressWarnings("serial")
public class OrderDTO implements java.io.Serializable{
	//订单id
	private String orderid;
	//订单金额
	private String amount;
	
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
}

