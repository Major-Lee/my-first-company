package com.bhu.vas.api.rpc.commdity.model;

import java.util.Date;

import com.bhu.vas.api.rpc.sequence.helper.ISequenceGenable;
import com.smartwork.msip.cores.orm.model.BaseStringModel;

/**
 * 订单模型
 * @author tangzichao
 */
@SuppressWarnings("serial")
public class Order extends BaseStringModel implements ISequenceGenable{
	//商品id
	private Integer itemid;
	//应用id
	private Integer appid;
	//设备mac
	private String mac;
	//用户mac
	private String umac;
	//用户uid
	private Integer uid;
	//支付订单id
	private String pay_orderid;
	//订单金额
	private String amount;
	//业务上下文
	private String context;
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

	public Integer getItemid() {
		return itemid;
	}

	public void setItemid(Integer itemid) {
		this.itemid = itemid;
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

	public String getPay_orderid() {
		return pay_orderid;
	}

	public void setPay_orderid(String pay_orderid) {
		this.pay_orderid = pay_orderid;
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
	public void setSequenceKey(Integer key) {
		//this.setId(key);
	}

}
