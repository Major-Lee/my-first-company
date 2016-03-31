package com.bhu.vas.api.rpc.user.iservice;

import com.bhu.vas.api.dto.commdity.internal.pay.RequestWithdrawNotifyDTO;
import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.vto.wallet.UserWalletDetailVTO;
import com.bhu.vas.api.vto.wallet.UserWalletLogVTO;
import com.bhu.vas.api.vto.wallet.UserWithdrawApplyVTO;
import com.smartwork.msip.cores.orm.support.page.TailPage;

public interface IUserWalletRpcService {
	
	/**
	 * 设置提现密码
	 * 如果密码已经设置过，则跑错误码
	 * @param uid
	 * @param captcha
	 * @param pwd
	 * @return
	 */
	public RpcResponseDTO<Boolean> withdrawPwdSet(int uid,String captcha,String pwd);
	
	/**
	 * 更改密码
	 * 需要做pwd密码的验证，通过后设置npwd为新密码
	 * @param uid
	 * @param pwd
	 * @param npwd
	 * @return
	 */
	//public RpcResponseDTO<Boolean> withdrawPwdUpd(int uid,String pwd,String npwd);
	
	/**
	 * 分页提取提现申请记录
	 * @param uid 操作员uid
	 * @param tuid 指定谁的提现申请uid
	 * @param withdraw_status 指定提现申请的状态
	 * @param pageNo 分页no
	 * @param pageSize 分页size
	 */
	public RpcResponseDTO<TailPage<UserWithdrawApplyVTO>> 	pageWithdrawApplies(int reckoner,int tuid,String withdraw_status,int pageNo,int pageSize);
	
	public RpcResponseDTO<String> withdrawApplyStatus(int reckoner,String applyid);
	/**
	 * 分页提取用户钱包流水
	 * @param uid
	 * @param transmode
	 * @param transtype
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	public RpcResponseDTO<TailPage<UserWalletLogVTO>> 		pageUserWalletlogs(int uid,String transmode,String transtype,int pageNo,int pageSize);
    /**
     * 审核提现申请，只有状态为VP的申请可以被审核，业务实现考虑验证此状态
     * @param reckoner
     * @param applies applyids 逗号分割
     */
	//public RpcResponseDTO<Boolean> verifyApplies(int reckoner,String applyid,boolean passed);
	
	
	/**
	 * 验证提现申请是否有效
	 * @param reckoner
	 * @param applyid
	 * @return
	 */
	public RpcResponseDTO<RequestWithdrawNotifyDTO> doStartPaymentWithdrawApply(int reckoner,String applyid);
	public RpcResponseDTO<UserWithdrawApplyVTO> doWithdrawNotifyFromLocal(int reckoner,String applyid,boolean successed);
	/**
	 * 提现操作api
	 * @param uid
	 * @param paymode 如果此值为空，则取出用户曾经定义过的支付方式的第一个，如果没有定义过，则抛异常提示
	 * @param applies
	 * 
	 * @param passed
	 * @return
	 */
	public RpcResponseDTO<UserWithdrawApplyVTO>	withdrawOper(int appid,String payment_type,int uid,String pwd,double cash,String remoteip);
	
	/**
	 * 钱包详情
	 * @param uid
	 * @return
	 */
	public RpcResponseDTO<UserWalletDetailVTO> walletDetail(int uid);
	

	
	/**
	 * 通过用户id获取其绑定第三方转账帐号
	 * @param uid
	 * @return
	 */
	//public RpcResponseDTO<List<ThirdpartiesPaymentDTO>> fetchUserThirdpartiesPayments(int uid);
	
	/**
	 * 移除相关第三方的转账账号绑定
	 * @param uid
	 * @param paymode
	 * @return
	 */
	//public RpcResponseDTO<Boolean> removeUserThirdpartiesPayment(int uid,String payment_type);
	
	/**
	 * 创建或更新第三方转账账号关联信息
	 * 生成一个新的帐号
	 * @param uid 
	 * @param paymode
	 * @param id
	 * @param name
	 * @return
	 */
	//public RpcResponseDTO<List<ThirdpartiesPaymentDTO>> createUserThirdpartiesPayment(int uid,String paymode,String id,String name,String avatar);

}
