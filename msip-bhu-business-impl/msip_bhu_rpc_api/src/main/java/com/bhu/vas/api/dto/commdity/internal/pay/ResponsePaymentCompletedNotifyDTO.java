package com.bhu.vas.api.dto.commdity.internal.pay;


/**
 * 支付系统支付完成的消息DTO
 * @author tangzichao
 *
 */
@SuppressWarnings("serial")
public class ResponsePaymentCompletedNotifyDTO extends ResponsePaymentDTO{
	//订单id
	private String orderId;
	//订单支付成功时间
	private long paymented_ts;
	
	public String getOrderId() {
		return orderId;
	}
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	public long getPaymented_ts() {
		return paymented_ts;
	}
	public void setPaymented_ts(long paymented_ts) {
		this.paymented_ts = paymented_ts;
	}
}

