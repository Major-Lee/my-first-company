package com.bhu.vas.api.dto.commdity;
/**
 * 订单dto
 * @author tangzichao
 *
 */
@SuppressWarnings("serial")
public class OrderDTO implements java.io.Serializable{
	//订单id
	private String id;
	//商品id
	private Integer commdityid;
	//应用id
	private Integer appid;
	//设备mac
	private String mac;
	//设备业务线
	private String mac_dut;
	//用户mac
	private String umac;
	//用户uid
	private Integer uid;
	//支付订单id
	//private String pay_orderid;
	//订单金额
	private String amount;
	//业务上下文
	private String context;
	//订单状态
	private Integer status;
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
	public Integer getCommdityid() {
		return commdityid;
	}
	public void setCommdityid(Integer commdityid) {
		this.commdityid = commdityid;
	}
	public Integer getAppid() {
		return appid;
	}
	public void setAppid(Integer appid) {
		this.appid = appid;
	}
	public String getMac() {
		return mac;
	}
	public void setMac(String mac) {
		this.mac = mac;
	}
	public String getMac_dut() {
		return mac_dut;
	}
	public void setMac_dut(String mac_dut) {
		this.mac_dut = mac_dut;
	}
	public String getUmac() {
		return umac;
	}
	public void setUmac(String umac) {
		this.umac = umac;
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
	public String getContext() {
		return context;
	}
	public void setContext(String context) {
		this.context = context;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
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
