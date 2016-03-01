package com.bhu.vas.api.rpc.user.model;

import com.smartwork.msip.cores.orm.model.BaseIntModel;

/**
 * 用户钱包进账 出帐 消费 记录流水表
 * 包含 虚拟货币和现金两部分
 * @author Edmond
 *
 */
@SuppressWarnings("serial")
public class UserWalletLog extends BaseIntModel{// implements ISequenceGenable,TableSplitable<Integer>{
	private int uid;
	//交易类别 充值购买虎钻 充值现金 系统/活动赠送虎钻 消费虎钻 提现(withdraw)
	private String transaction;
	//transaction 描述 用于查看方便的冗余字段 
	private String transaction_desc;
	//交易数量(正负数字 充值购买虎钻 系统/活动赠送虎钻 消费虎钻 时 值不为0) 
	private int sum;
	//现金（正负数字 充值购买虎钻 充值现金 提现(withdraw)） 
	private Double amount;
	//交易内容描述
	private String desc;
	public UserWalletLog() {
		super();
	}
	public UserWalletLog(Integer userid){//,String email){
		super();
		this.id = userid;
	}
	public int getUid() {
		return uid;
	}
	public void setUid(int uid) {
		this.uid = uid;
	}
	
	public String getTransaction() {
		return transaction;
	}
	public void setTransaction(String transaction) {
		this.transaction = transaction;
	}
	public String getTransaction_desc() {
		return transaction_desc;
	}
	public void setTransaction_desc(String transaction_desc) {
		this.transaction_desc = transaction_desc;
	}
	public Double getAmount() {
		return amount;
	}
	public void setAmount(Double amount) {
		this.amount = amount;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	public int getSum() {
		return sum;
	}
	public void setSum(int sum) {
		this.sum = sum;
	}
	
	
}
