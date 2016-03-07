package com.bhu.vas.api.dto.commdity.internal.pay;

import com.bhu.vas.api.vto.wallet.UserWithdrawApplyVTO;


/**
 * 提现申请审核通过后，需要此对象通知uPay，数据写入约定的redis中
 * @author Edmond
 *
orderid:提现订单id
verify_ts:提现审核通过时间
withdraw:提现实体
	applyid:提现id(orderid)
	withdraw_oper:提现当前状态码 WD代表正系统在进行提现操作
	uid:用户id
	appid:应用id
	mobileno:用户手机号
	nick:用户昵称
	cash：提现金额
	transcost:交易手续费
	taxcost：税费
	realCash：实际需要转账的金额
account:转账账号
	type:账户类别（微信、支付宝）
	openid：具体的微信、支付宝转账账户
 */
@SuppressWarnings("serial")
public class RequestWithdrawNotifyDTO  implements java.io.Serializable{
	//订单id
	private String orderid;
	//订单审核通过时间
	private long verify_ts;
	
	private UserWithdrawApplyVTO withdraw;
	
	private String account;
	
	public String getOrderid() {
		return orderid;
	}
	public void setOrderid(String orderid) {
		this.orderid = orderid;
	}
	public long getVerify_ts() {
		return verify_ts;
	}
	public void setVerify_ts(long verify_ts) {
		this.verify_ts = verify_ts;
	}
	public UserWithdrawApplyVTO getWithdraw() {
		return withdraw;
	}
	public void setWithdraw(UserWithdrawApplyVTO withdraw) {
		this.withdraw = withdraw;
	}
	public String getAccount() {
		return account;
	}
	public void setAccount(String account) {
		this.account = account;
	}
	
	public static RequestWithdrawNotifyDTO from(UserWithdrawApplyVTO apply,long ts){
		RequestWithdrawNotifyDTO notify = new RequestWithdrawNotifyDTO();
		notify.setOrderid(apply.getApplyid());
		notify.setVerify_ts(ts);
		notify.setWithdraw(apply);
		return notify;
	}
}

