package com.bhu.vas.api.rpc.user.model;

import java.util.Date;

import com.bhu.vas.api.vto.wallet.UserWalletLogFFVTO;
import com.smartwork.msip.cores.helper.DateTimeHelper;
import com.smartwork.msip.cores.orm.model.BaseIntModel;

/**
 * 用户提现申请
 * 用户提取申发起之后金额会从钱包零钱中扣除，如果提现金额审核不通过或者提现失败会重新返还金额到钱包
 * @author Edmond
 *
 */
@SuppressWarnings("serial")
//BaseStringModel
public class UserSharedealDistributorView extends BaseIntModel {
	private int uid;
	//提现入账账户类别
	private String orderid;
	private String mac;//设备mac
	private String transmode;
	//transmode_desc 描述 用于查看方便的冗余字段 
	private String transmode_desc;
	private String transtype;
	//transtype_desc 描述 用于查看方便的冗余字段 
	private String transtype_desc;
	//交易现金相关 
	private String rmoney;
	//交易零钱相关（正负数字 充值购买虎钻 充值现金 提现(withdraw)） 
	private String cash;
	//打赏金额
//	private String amount;
	//交易虚拟币相关
	private String vcurrency;
	//交易内容描述
	private String memo;
	private String description;
	
	private Date updated_at;
	
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
	public String getMac() {
		return mac;
	}
	public void setMac(String mac) {
		this.mac = mac;
	}
	public String getTransmode() {
		return transmode;
	}
	public void setTransmode(String transmode) {
		this.transmode = transmode;
	}
	public String getTransmode_desc() {
		return transmode_desc;
	}
	public void setTransmode_desc(String transmode_desc) {
		this.transmode_desc = transmode_desc;
	}
	public String getTranstype() {
		return transtype;
	}
	public void setTranstype(String transtype) {
		this.transtype = transtype;
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
		vto.setMemo(memo);
		vto.setOperdate(DateTimeHelper.formatDate(this.updated_at, DateTimeHelper.FormatPattern0));
		return vto;
	}
}
