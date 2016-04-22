package com.bhu.vas.api.rpc.payment.dto;


/**
 * 提现申请费用消耗
 * @author Edmond
 *
 */
public class PaymentOrderVTO {
	private long ordierid;
	private String tid;
	public long getOrdierid() {
		return ordierid;
	}
	public void setOrdierid(long ordierid) {
		this.ordierid = ordierid;
	}
	public String getTid() {
		return tid;
	}
	public void setTid(String tid) {
		this.tid = tid;
	}
	
	
}
