package com.bhu.vas.api.rpc.payment.dto;

/**
 * 提现申请费用消耗
 * 
 * @author Edmond
 *
 */
public class PaymentRecordDTO {
	private int amount;
	private int count;
	private int dx_count;
	private String info;

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	public int getCount() {
		return count;
	}
	
	public int getDx_count() {
		return dx_count;
	}

	public void setDx_count(int dx_count) {
		this.dx_count = dx_count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}
}
