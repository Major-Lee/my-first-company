package com.bhu.vas.api.dto.commdity.internal.pay;

import java.util.Date;

import com.bhu.vas.api.helper.PaymentNotifyType;
import com.bhu.vas.api.rpc.commdity.model.Order;
import com.smartwork.msip.cores.helper.DateTimeHelper;


/**
 * 支付系统支付完成的消息DTO
 * @author tangzichao
 *
 */
@SuppressWarnings("serial")
public class ResponsePaymentCompletedNotifyDTO extends ResponsePaymentDTO implements ResponsePaymentNotifyTypeDTO{
	//订单id
	private String orderid;
	//订单支付成功时间 yyyy-MM-dd HH:mm:ss
	private String paymented_ds;
	//支付方式
	private String payment_type;
	//支付代理方式
	private String payment_proxy_type;
	
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
	public String getPayment_type() {
		return payment_type;
	}
	public void setPayment_type(String payment_type) {
		this.payment_type = payment_type;
	}
	public String getPayment_proxy_type() {
		return payment_proxy_type;
	}
	public void setPayment_proxy_type(String payment_proxy_type) {
		this.payment_proxy_type = payment_proxy_type;
	}
	
	public static ResponsePaymentCompletedNotifyDTO builder(Order order){
		ResponsePaymentCompletedNotifyDTO dto = new ResponsePaymentCompletedNotifyDTO();
		dto.setSuccess(true);
		dto.setOrderid(order.getId());
		dto.setPayment_type(order.getPayment_type());
		dto.setPayment_proxy_type(order.getPayment_proxy_type());
		dto.setPaymented_ds(DateTimeHelper.formatDate(new Date(), DateTimeHelper.DefalutFormatPattern));
		return dto;
	}
	@Override
	public String getPaymentNotifyType() {
		return PaymentNotifyType.NormalPaymentNotify.getPrefix();
	}
}

