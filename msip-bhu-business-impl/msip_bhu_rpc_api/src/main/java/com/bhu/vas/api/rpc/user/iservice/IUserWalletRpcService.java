package com.bhu.vas.api.rpc.user.iservice;

import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.vto.wallet.UserWalletDetailVTO;
import com.bhu.vas.api.vto.wallet.UserWithdrawApplyVTO;
import com.smartwork.msip.cores.orm.support.page.TailPage;

public interface IUserWalletRpcService {
	/**
	 * 分页提取提现申请记录
	 * @param uid 操作员uid
	 * @param tuid 指定谁的提现申请uid
	 * @param withdraw_status 指定提现申请的状态
	 * @param pageNo 分页no
	 * @param pageSize 分页size
	 */
	public RpcResponseDTO<TailPage<UserWithdrawApplyVTO>> pagesWithdrawApplies(int reckoner,int tuid,String withdraw_status,int pageNo,int pageSize);
	
	
    /**
     * 审核提现申请，只有状态为VP的申请可以被审核，业务实现考虑验证此状态
     * @param reckoner
     * @param applies applyids 逗号分割
     */
	public RpcResponseDTO<Boolean> verifyApplies(int reckoner,long applyid,boolean passed);
	
	/**
	 * 提现操作api
	 * @param uid
	 * @param applies
	 * @param passed
	 * @return
	 */
	public RpcResponseDTO<UserWithdrawApplyVTO>	withdrawOper(int uid,String pwd,double cash,String remoteip);
	
	/**
	 * 钱包详情
	 * @param uid
	 * @return
	 */
	public RpcResponseDTO<UserWalletDetailVTO> walletDetail(int uid);
}
