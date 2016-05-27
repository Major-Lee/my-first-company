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
