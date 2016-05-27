package com.bhu.vas.api.rpc.payment.vto;


/**
 * 提现申请费用消耗
 * @author Edmond
 *
 */
@SuppressWarnings("serial")
public class PaymentWithdrawVTO implements java.io.Serializable{
	private String orderId;
	private String reckoningId;
	private int status;
	private String pay_time;
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
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public String getPay_time() {
		return pay_time;
	}
	public void setPay_time(String pay_time) {
		this.pay_time = pay_time;
	}
}
