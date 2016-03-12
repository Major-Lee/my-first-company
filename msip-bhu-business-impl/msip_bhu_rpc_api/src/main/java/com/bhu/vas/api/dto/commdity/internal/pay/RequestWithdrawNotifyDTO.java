package com.bhu.vas.api.dto.commdity.internal.pay;

import org.apache.commons.lang.StringUtils;

import com.bhu.vas.api.rpc.user.dto.ThirdpartiesPaymentDTO;
import com.bhu.vas.api.vto.wallet.UserWithdrawApplyVTO;


/**
 * 提现申请审核通过后，需要此对象通知uPay，数据写入约定的redis中
 * @author Edmond
 *
orderId:提现订单id
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
	mode:alipay
	id:账户类别（微信、支付宝）
	name：名称
 */
@SuppressWarnings("serial")
public class RequestWithdrawNotifyDTO  implements java.io.Serializable{
	//订单id
	private String orderid;
	//订单审核通过时间
	private long verify_ts;
	private UserWithdrawApplyVTO withdraw;
	
	private ThirdpartiesPaymentDTO account;

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
	
	public ThirdpartiesPaymentDTO getAccount() {
		return account;
	}
	public void setAccount(ThirdpartiesPaymentDTO account) {
		this.account = account;
	}
	public static RequestWithdrawNotifyDTO from(UserWithdrawApplyVTO apply,ThirdpartiesPaymentDTO account,long ts){
		RequestWithdrawNotifyDTO notify = new RequestWithdrawNotifyDTO();
		notify.setOrderid(apply.getApplyid());
		notify.setVerify_ts(ts);
		notify.setWithdraw(apply);
		notify.setAccount(account);
		return notify;
	}
	
	public boolean validate(){
		return StringUtils.isNotEmpty(orderid) 
				&& account != null && withdraw != null;
	}
}

