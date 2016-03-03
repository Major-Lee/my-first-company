package com.bhu.vas.api.rpc.user.model;

import com.bhu.vas.api.rpc.sequence.helper.IRedisSequenceGenable;
import com.smartwork.msip.cores.orm.model.BaseLongModel;

/**
 * 用户钱包进账 出帐 消费 提现 的记录流水表
 * 包含 虚拟货币和现金两部分
 * @author Edmond
 *
 */
@SuppressWarnings("serial")
public class UserWalletLog extends BaseLongModel implements IRedisSequenceGenable {
	private int uid;
	private String orderid;
	//交易类别 充值购买虎钻 充值现金 系统/活动赠送虎钻 消费虎钻 提现(withdraw)
	private String transaction;
	//transaction 描述 用于查看方便的冗余字段 
	private String transaction_desc;
	//交易数量(正负数字 充值购买虎钻 系统/活动赠送虎钻 消费虎钻 时 值不为0) 
	private double sum;
	//现金（正负数字 充值购买虎钻 充值现金 提现(withdraw)） 
	private double cash;
	//交易内容描述
	private String memo;
	public UserWalletLog() {
		super();
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

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	public double getSum() {
		return sum;
	}

	public void setSum(double sum) {
		this.sum = sum;
	}

	public double getCash() {
		return cash;
	}

	public void setCash(double cash) {
		this.cash = cash;
	}

	public String getOrderid() {
		return orderid;
	}

	public void setOrderid(String orderid) {
		this.orderid = orderid;
	}

	@Override
	public void setSequenceKey(Long key) {
		this.id = key;
	}
}
