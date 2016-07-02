package com.bhu.vas.api.dto.commdity.internal.pay;

import com.bhu.vas.api.helper.PaymentNotifyType;


/**
 * 支付系统支付完成的消息DTO
 * @author tangzichao
 *
 */
@SuppressWarnings("serial")
public class ResponseWithdrawCompletedNotifyDTO extends ResponsePaymentDTO implements ResponsePaymentNotifyTypeDTO{
	
	//订单id
	private String orderid;
	//订单支付成功时间 yyyy-MM-dd HH:mm:ss
	private String paymented_ds;
	
	
	public String getOrderid() {
		return orderid;
	}
	public void setOrderid(String orderid) {
		this.orderid = orderid;
	}
	public String getPaymented_ds() {
		return paymented_ds;
	}
	public void setPaymented_ds(String paymented_ds) {
		this.paymented_ds = paymented_ds;
	}
	
	@Override
	public String getPaymentNotifyType() {
		return PaymentNotifyType.NormalPaymentNotify.getPrefix();
	}
}
