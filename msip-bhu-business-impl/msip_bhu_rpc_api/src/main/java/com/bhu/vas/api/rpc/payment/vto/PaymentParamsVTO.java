package com.bhu.vas.api.rpc.payment.vto;


/**
 * 提现申请费用消耗
 * @author Edmond
 *
 */
@SuppressWarnings("serial")
public class PaymentParamsVTO implements java.io.Serializable{
	private String ordierId;
	private String tid;
	public String getTid() {
		return tid;
	}
	public void setTid(String tid) {
		this.tid = tid;
	}
	public String getOrdierId() {
		return ordierId;
	}
	public void setOrdierId(String ordierId) {
		this.ordierId = ordierId;
	}
	
	
	
}
