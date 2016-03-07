package com.bhu.vas.api.dto.commdity.internal.pay;


/**
 * 支付系统支付完成的消息DTO
 * @author tangzichao
 *
 */
@SuppressWarnings("serial")
public class ResponsePaymentCompletedNotifyDTO extends ResponsePaymentDTO{
	//订单id
	private String orderid;
	//订单支付成功时间
	private long paymented_ts;
	
	public String getOrderid() {
		return orderid;
	}
	public void setOrderid(String orderid) {
		this.orderid = orderid;
	}
	public long getPaymented_ts() {
		return paymented_ts;
	}
	public void setPaymented_ts(long paymented_ts) {
		this.paymented_ts = paymented_ts;
	}
}

