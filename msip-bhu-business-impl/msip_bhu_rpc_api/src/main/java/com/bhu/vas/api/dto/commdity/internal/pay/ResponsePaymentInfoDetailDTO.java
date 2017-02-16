package com.bhu.vas.api.dto.commdity.internal.pay;

/**
 * 支付系统接口返回数据基类DTO
 * @author tangzichao
 *
 */
@SuppressWarnings("serial")
public class ResponsePaymentInfoDetailDTO{
	
	private int count;
	private long amount;
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public long getAmount() {
		return amount;
	}
	public void setAmount(long amount) {
		this.amount = amount;
	}
}

