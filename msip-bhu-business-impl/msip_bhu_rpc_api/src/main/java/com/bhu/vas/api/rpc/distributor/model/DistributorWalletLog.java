package com.bhu.vas.api.rpc.distributor.model;

import java.util.Date;

import com.smartwork.msip.cores.orm.model.BaseLongModel;

@SuppressWarnings("serial")
public class DistributorWalletLog extends BaseLongModel {
	private int uid;
	private String orderid;
	//交易模式 UWalletTransMode
	private String transmode;
	//交易类型 UWalletTransType
	private String transtype;
	//transmode_desc 描述 用于查看方便的冗余字段 
	private String transmode_desc;
	//transtype_desc 描述 用于查看方便的冗余字段 
	private String transtype_desc;
	//交易现金相关 
	private String rmoney;
	//交易零钱相关（正负数字 充值购买虎钻 充值现金 提现(withdraw)） 
	private String cash;
	//交易虚拟币相关
	private String vcurrency;
	
	//当前时间 打赏收益 冗余字段 设备mac
	private String mac;
	private String umac;
	//当前时间 打赏收益 冗余字段 设备所属群组
	private String current_gpath;
	//交易内容描述
	private String memo;

	private String description;
	private Date updated_at;
	private int adjusted;
	

	public DistributorWalletLog() {
		super();
	}


	public int getUid() {
		return uid;
	}


	public void setUid(int uid) {
		this.uid = uid;
	}


	public String getOrderid() {
		return orderid;
	}


	public void setOrderid(String orderid) {
		this.orderid = orderid;
	}


	public String getTransmode() {
		return transmode;
	}


	public void setTransmode(String transmode) {
		this.transmode = transmode;
	}


	public String getTranstype() {
		return transtype;
	}


	public void setTranstype(String transtype) {
		this.transtype = transtype;
	}


	public String getTransmode_desc() {
		return transmode_desc;
	}


	public void setTransmode_desc(String transmode_desc) {
		this.transmode_desc = transmode_desc;
	}


	public String getTranstype_desc() {
		return transtype_desc;
	}


	public void setTranstype_desc(String transtype_desc) {
		this.transtype_desc = transtype_desc;
	}


	public String getRmoney() {
		return rmoney;
	}


	public void setRmoney(String rmoney) {
		this.rmoney = rmoney;
	}


	public String getCash() {
		return cash;
	}


	public void setCash(String cash) {
		this.cash = cash;
	}


	public String getVcurrency() {
		return vcurrency;
	}


	public void setVcurrency(String vcurrency) {
		this.vcurrency = vcurrency;
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


	public String getCurrent_gpath() {
		return current_gpath;
	}


	public void setCurrent_gpath(String current_gpath) {
		this.current_gpath = current_gpath;
	}


	public String getMemo() {
		return memo;
	}


	public void setMemo(String memo) {
		this.memo = memo;
	}


	public String getDescription() {
		return description;
	}


	public void setDescription(String description) {
		this.description = description;
	}


	public Date getUpdated_at() {
		return updated_at;
	}


	public void setUpdated_at(Date updated_at) {
		this.updated_at = updated_at;
	}


	public int getAdjusted() {
		return adjusted;
	}

	public void setAdjusted(int adjusted) {
		this.adjusted = adjusted;
	}
}
