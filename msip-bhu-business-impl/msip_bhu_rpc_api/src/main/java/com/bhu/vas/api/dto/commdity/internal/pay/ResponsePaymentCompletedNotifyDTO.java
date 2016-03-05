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
	private long payment_ts;
	
	public String getOrderid() {
		return orderid;
	}
	public void setOrderid(String orderid) {
		this.orderid = orderid;
	}
	public long getPayment_ts() {
		return payment_ts;
	}
	public void setPayment_ts(long payment_ts) {
		this.payment_ts = payment_ts;
	}
	
}

