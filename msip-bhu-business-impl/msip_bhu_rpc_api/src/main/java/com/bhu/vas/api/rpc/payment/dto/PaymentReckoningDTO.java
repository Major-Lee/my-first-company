package com.bhu.vas.api.rpc.payment.dto;


/**
 * 提现申请费用消耗
 * @author Edmond
 *
 */
@SuppressWarnings("serial")
public class PaymentReckoningDTO implements java.io.Serializable{
	private String orderId;
	private String reckoningId;
	public String getOrderId() {
		return orderId;
	}
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	public String getReckoningId() {
		return reckoningId;
	}
	public void setReckoningId(String reckoningId) {
		this.reckoningId = reckoningId;
	}
}
