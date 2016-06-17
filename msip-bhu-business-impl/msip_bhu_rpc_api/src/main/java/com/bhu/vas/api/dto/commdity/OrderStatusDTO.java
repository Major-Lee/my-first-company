package com.bhu.vas.api.dto.commdity;

import java.util.Date;


/**
 * 订单状态DTO
 * @author tangzichao
 *
 */
@SuppressWarnings("serial")
public class OrderStatusDTO implements java.io.Serializable{
	//订单id
	private String id;
	//订单状态
	private Integer status;
	//订单类型
	private Integer type;
	//订单金额
	private String amount;
	//订单虎钻
	private long vcurrency;
	//支付方式
	private String payment_type;
	//订单支付成功时间
	private Date paymented_at;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public Integer getType() {
		return type;
	}
	public void setType(Integer type) {
		this.type = type;
	}
	public String getAmount() {
		return amount;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}
	public long getVcurrency() {
		return vcurrency;
	}
	public void setVcurrency(long vcurrency) {
		this.vcurrency = vcurrency;
	}
	public String getPayment_type() {
		return payment_type;
	}
	public void setPayment_type(String payment_type) {
		this.payment_type = payment_type;
	}
	public Date getPaymented_at() {
		return paymented_at;
	}
	public void setPaymented_at(Date paymented_at) {
		this.paymented_at = paymented_at;
	}
}

