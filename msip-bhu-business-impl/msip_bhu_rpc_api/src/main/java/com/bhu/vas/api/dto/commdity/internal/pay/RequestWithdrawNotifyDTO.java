package com.bhu.vas.api.dto.commdity.internal.pay;

import com.bhu.vas.api.vto.wallet.UserWithdrawApplyVTO;


/**
 * 提现申请审核通过后，需要此对象通知uPay，数据写入约定的redis中
 * @author Edmond
 *
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

