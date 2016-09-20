package com.bhu.vas.api.vto.wallet;
/**
 * 非凡联盟专用VTO
 * @author tangzichao
 *
 */
@SuppressWarnings("serial")
public class UserWalletLogFFVTO implements java.io.Serializable {
	private long id;
	private int uid;
	// 设备mac
	private String mac;
	// 终端mac
	private String umac;
	private String orderid;
	private String transmode;
	// transmode_desc 描述 用于查看方便的冗余字段 
	private String transmode_desc;
	private String transtype;
	// transtype_desc 描述 用于查看方便的冗余字段 
	private String transtype_desc;
	// 交易现金相关 
	private String rmoney;
	// 交易零钱相关（正负数字 充值购买虎钻 充值现金 提现(withdraw)） 
	private String cash;
	// 打赏金额
	private String amount;
	// 交易虚拟币相关
	private String vcurrency;
	// 交易内容描述
	private String memo;
	private String description;
	//private String payment_type;
	//private String withdrawdate;
	
	//日志时间
	private String operdate;
	
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
	public String getMemo() {
		return memo;
	}
	public void setMemo(String memo) {
		this.memo = memo;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getOperdate() {
		return operdate;
	}
	public void setOperdate(String operdate) {
		this.operdate = operdate;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
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
	public String getMac() {
		return mac;
	}
	public void setMac(String mac) {
		this.mac = mac;
	}
	public String getAmount() {
		return amount;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}
	public String getUmac() {
		return umac;
	}
	public void setUmac(String umac) {
		this.umac = umac;
	}
	
}
