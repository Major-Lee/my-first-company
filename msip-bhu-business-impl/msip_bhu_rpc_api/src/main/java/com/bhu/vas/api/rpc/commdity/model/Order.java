package com.bhu.vas.api.rpc.commdity.model;

import java.util.Date;

import com.bhu.vas.api.helper.BusinessEnumType.OrderExtSegmentPayMode;
import com.bhu.vas.api.rpc.commdity.helper.StructuredIdHelper;
import com.bhu.vas.api.rpc.sequence.helper.IRedisSequenceGenable;
import com.smartwork.msip.cores.orm.model.BaseStringModel;

/**
 * 订单模型
 * @author tangzichao
 */
@SuppressWarnings("serial")
public class Order extends BaseStringModel implements IRedisSequenceGenable{
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
	//用户终端类型
	private Integer umactype;
	//用户uid
	private Integer uid;
	//订单类型
	private Integer type;
	//支付订单id
	//private String pay_orderid;
	//支付方式
	private String payment_type;
	//支付代理方式
	private String payment_proxy_type;
	//订单支付成功时间
	private Date paymented_at;
	//订单金额
	private String amount;
	//订单虎钻
	private long vcurrency;
	//业务上下文
	private String context;
	//user agent sources content
	private String user_agent;
	//订单状态
	private Integer status;
	//订单流程状态
	private Integer process_status;
	//订单创建时间
	private Date created_at;
	
	public Order() {
		super();
	}

	@Override
	public void preInsert() {
		if (this.created_at == null)
			this.created_at = new Date();
		super.preInsert();
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
	
	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	/*	public String getPay_orderid() {
		return pay_orderid;
	}

	public void setPay_orderid(String pay_orderid) {
		this.pay_orderid = pay_orderid;
	}*/
	public String getPayment_type() {
		return payment_type;
	}

	public void setPayment_type(String payment_type) {
		this.payment_type = payment_type;
	}
	
	public String getPayment_proxy_type() {
		return payment_proxy_type;
	}

	public void setPayment_proxy_type(String payment_proxy_type) {
		this.payment_proxy_type = payment_proxy_type;
	}

	public Date getPaymented_at() {
		return paymented_at;
	}

	public void setPaymented_at(Date paymented_at) {
		this.paymented_at = paymented_at;
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

	public String getContext() {
		return context;
	}

	public void setContext(String context) {
		this.context = context;
	}

	public String getUser_agent() {
		return user_agent;
	}

	public void setUser_agent(String user_agent) {
		this.user_agent = user_agent;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Integer getProcess_status() {
		return process_status;
	}

	public void setProcess_status(Integer process_status) {
		this.process_status = process_status;
	}

	public Date getCreated_at() {
		return created_at;
	}

	public void setCreated_at(Date created_at) {
		this.created_at = created_at;
	}

	@Override
	public void setSequenceKey(Long autoId) {
		String ext_segment = StructuredIdHelper.buildStructuredExtSegmentString(OrderExtSegmentPayMode.Receipt.getKey());
		this.setId(StructuredIdHelper.generateStructuredIdString(getAppid(), ext_segment, autoId));
	}

}
