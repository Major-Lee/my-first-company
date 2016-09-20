package com.bhu.vas.api.dto.commdity;
/**
 * 用于展示用户的设备的充值虎钻订单支付记录实体DTO
 * @author tangzichao
 *
 */
@SuppressWarnings("serial")
public class OrderRechargeVCurrencyVTO extends OrderVTO{
	//用户终端类型
	private Integer umactype;
	//用户uid
	private Integer uid;
	//订单金额
	private String amount;
	//虎钻数量
	private long vcurrency;
	//支付方式
	private String payment_type;
	//支付方式名称
	private String payment_type_name;
	
	public Integer getUmactype() {
		return umactype;
	}
	public void setUmactype(Integer umactype) {
		this.umactype = umactype;
	}
	public Integer getUid() {
		return uid;
	}
	public void setUid(Integer uid) {
		this.uid = uid;
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
	public String getPayment_type_name() {
		return payment_type_name;
	}
	public void setPayment_type_name(String payment_type_name) {
		this.payment_type_name = payment_type_name;
	}
}
