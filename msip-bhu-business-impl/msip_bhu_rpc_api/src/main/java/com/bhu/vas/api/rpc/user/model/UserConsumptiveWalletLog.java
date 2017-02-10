package com.bhu.vas.api.rpc.user.model;

import com.smartwork.msip.cores.orm.model.BaseLongModel;

/**
 * 用户消费的钱包进账 出帐 消费  的记录流水表
 * @author fengshibo
 *
 */
@SuppressWarnings("serial")
public class UserConsumptiveWalletLog extends BaseLongModel{// implements IRedisSequenceGenable {
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
	//交易零钱相关
	private String cash;
	//交易内容描述
	private String memo;

	private String description;

	public UserConsumptiveWalletLog() {
		super();
	}

	public int getUid() {
		return uid;
	}
	public void setUid(int uid) {
		this.uid = uid;
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
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


	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
