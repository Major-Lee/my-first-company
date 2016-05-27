package com.bhu.vas.api.dto.commdity;
/**
 * 用于展示用户的设备的订单支付记录实体DTO
 * @author tangzichao
 *
 */
@SuppressWarnings("serial")
public class UserOrderDTO implements java.io.Serializable{
	//订单id
	private String id;
	//设备mac
	private String mac;
	//用户mac
	private String umac;
	//用户终端厂商
	private String umac_mf;
	//用户终端类型
	private Integer umactype;
	//用户uid
	private Integer uid;
	//订单金额
	private String amount;
	//分成金额
	private String share_amount;
	//支付方式
	private String payment_type;
	//支付方式名称
	private String payment_type_name;
	//订单创建时间
	private long created_ts;
	//订单支付时间
	private long paymented_ts;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getMac() {
		return mac;
	}
	public void setMac(String mac) {
		this.mac = mac;
	}
	public String getUmac() {
		return umac;
	}
	public void setUmac(String umac) {
		this.umac = umac;
	}
	public String getUmac_mf() {
		return umac_mf;
	}
	public void setUmac_mf(String umac_mf) {
		this.umac_mf = umac_mf;
	}
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
	public String getShare_amount() {
		return share_amount;
	}
	public void setShare_amount(String share_amount) {
		this.share_amount = share_amount;
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
	public long getCreated_ts() {
		return created_ts;
	}
	public void setCreated_ts(long created_ts) {
		this.created_ts = created_ts;
	}
	public long getPaymented_ts() {
		return paymented_ts;
	}
	public void setPaymented_ts(long paymented_ts) {
		this.paymented_ts = paymented_ts;
	}
}
