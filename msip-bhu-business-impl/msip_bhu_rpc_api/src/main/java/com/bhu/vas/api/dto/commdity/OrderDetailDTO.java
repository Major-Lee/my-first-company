package com.bhu.vas.api.dto.commdity;

import java.util.Date;


/**
 * 订单细节DTO
 * @author tangzichao
 *
 */
@SuppressWarnings("serial")
public class OrderDetailDTO implements java.io.Serializable{
	//订单id
	private String id;
	//订单状态
	private Integer status;
	//订单类型
	private Integer type;
	//订单金额
	private String amount;
	//订单的商品id
	private Integer commdityid;
	//订单的商品名称
	private String commdityname;
	//订单虎钻
	private long vcurrency;
	//用户mac
	private String umac;
	//用户终端厂商
	private String umac_mf;
	//设备mac
	private String mac;
	//设备mac
	private String mac_name;
	//支付方式
	private String payment_type;
	//支付方式名称
	private String payment_type_name;
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
	public String getPayment_type_name() {
		return payment_type_name;
	}
	public void setPayment_type_name(String payment_type_name) {
		this.payment_type_name = payment_type_name;
	}
	public Date getPaymented_at() {
		return paymented_at;
	}
	public void setPaymented_at(Date paymented_at) {
		this.paymented_at = paymented_at;
	}
	public Integer getCommdityid() {
		return commdityid;
	}
	public void setCommdityid(Integer commdityid) {
		this.commdityid = commdityid;
	}
	public String getCommdityname() {
		return commdityname;
	}
	public void setCommdityname(String commdityname) {
		this.commdityname = commdityname;
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
	public String getMac() {
		return mac;
	}
	public void setMac(String mac) {
		this.mac = mac;
	}
	public String getMac_name() {
		return mac_name;
	}
	public void setMac_name(String mac_name) {
		this.mac_name = mac_name;
	}
}

