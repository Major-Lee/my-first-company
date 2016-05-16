package com.bhu.vas.api.rpc.payment.dto;


/**
 * 提现申请费用消耗
 * @author Edmond
 *
 */
public class PaymentOrderDTO {
	private String ordierId;
	private String tid;
	public String getOrdierid() {
		return ordierId;
	}
	public void setOrdierid(String ordierid) {
		this.ordierId = ordierid;
	}
	public String getTid() {
		return tid;
	}
	public void setTid(String tid) {
		this.tid = tid;
	}
	
	
}
