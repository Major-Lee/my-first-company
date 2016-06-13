package com.bhu.vas.api.dto.commdity.internal.pay;

import java.util.Date;

import com.bhu.vas.api.helper.PaymentNotifyType;
import com.bhu.vas.api.rpc.commdity.model.Order;


/**
 * 短信验证完成的消息DTO
 * @author tangzichao
 *
 */
@SuppressWarnings("serial")
public class ResponseSMSValidateCompletedNotifyDTO extends ResponsePaymentDTO implements ResponsePaymentNotifyType{
	//订单id
	private String orderid;
	//订单支付成功时间 
	private Date paymented_ds;
	
	public String getOrderid() {
		return orderid;
	}
	public void setOrderid(String orderid) {
		this.orderid = orderid;
	}
	public Date getPaymented_ds() {
		return paymented_ds;
	}
	public void setPaymented_ds(Date paymented_ds) {
		this.paymented_ds = paymented_ds;
	}
	
	public static ResponseSMSValidateCompletedNotifyDTO builder(Order order){
		if(order == null) return null;
		ResponseSMSValidateCompletedNotifyDTO dto = new ResponseSMSValidateCompletedNotifyDTO();
		dto.setSuccess(true);
		dto.setOrderid(order.getId());
		dto.setPaymented_ds(order.getPaymented_at());
		return dto;
	}
	@Override
	public String getPaymentNotifyType() {
		return PaymentNotifyType.SMSPaymentNotify.getPrefix();
	}
}

