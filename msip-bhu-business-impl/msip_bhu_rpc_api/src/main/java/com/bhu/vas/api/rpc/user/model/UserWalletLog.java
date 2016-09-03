package com.bhu.vas.api.rpc.user.model;

import com.bhu.vas.api.vto.wallet.UserWalletLogFFVTO;
import com.bhu.vas.api.vto.wallet.UserWalletLogVTO;
import com.smartwork.msip.cores.helper.ArithHelper;
import com.smartwork.msip.cores.helper.DateTimeHelper;
import com.smartwork.msip.cores.orm.model.BaseLongModel;

/**
 * 用户钱包进账 出帐 消费 提现 的记录流水表
 * 包含 虚拟货币和现金两部分
 * @author Edmond
 *
 */
@SuppressWarnings("serial")
public class UserWalletLog extends BaseLongModel{// implements IRedisSequenceGenable {
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
	//当前时间 打赏收益 冗余字段 设备所属群组
	private String current_gpath;
	//交易内容描述
	private String memo;
	private String description;
	public UserWalletLog() {
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

	/*@Override
	public void setSequenceKey(Long key) {
		this.id = key;
	}*/

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
	

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public UserWalletLogVTO toUserWalletLogVTO(String mobileno,String nick){
		UserWalletLogVTO vto = new UserWalletLogVTO();
		vto.setId(id);
		vto.setUid(uid);
		vto.setOrderid(orderid);
		vto.setNick(nick);
		vto.setMobileno(mobileno);
		vto.setCash(ArithHelper.getCuttedCurrency(cash));
		vto.setRmoney(rmoney);
		vto.setVcurrency(vcurrency);
		vto.setTransmode(transmode);
		vto.setTransmode_desc(transmode_desc);
		vto.setTranstype(transtype);
		vto.setTranstype_desc(transtype_desc);
		vto.setDescription(description);
		//vto.setPayment_type(payment_type);
		vto.setMemo(memo);
		vto.setOperdate(DateTimeHelper.formatDate(this.updated_at, DateTimeHelper.FormatPattern0));
		return vto;
	}
	
	public UserWalletLogFFVTO toUserWalletLogFFVTO(String amount, String mac){
		UserWalletLogFFVTO vto = new UserWalletLogFFVTO();
		vto.setId(id);
		vto.setUid(uid);
		vto.setMac(mac);
		vto.setOrderid(orderid);
		vto.setCash(cash);
		vto.setAmount(amount);
		vto.setRmoney(rmoney);
		vto.setVcurrency(vcurrency);
		vto.setTransmode(transmode);
		vto.setTransmode_desc(transmode_desc);
		vto.setTranstype(transtype);
		vto.setTranstype_desc(transtype_desc);
		vto.setDescription(description);
		//vto.setPayment_type(payment_type);
		vto.setMemo(memo);
		vto.setOperdate(DateTimeHelper.formatDate(this.updated_at, DateTimeHelper.FormatPattern0));
		return vto;
	}

	public String getMac() {
		return mac;
	}

	public void setMac(String mac) {
		this.mac = mac;
	}

	public String getCurrent_gpath() {
		return current_gpath;
	}

	public void setCurrent_gpath(String current_gpath) {
		this.current_gpath = current_gpath;
	}
}
