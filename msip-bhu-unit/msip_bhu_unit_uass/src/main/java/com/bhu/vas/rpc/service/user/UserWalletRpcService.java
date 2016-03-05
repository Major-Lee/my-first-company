package com.bhu.vas.rpc.service.user;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.alibaba.dubbo.common.logger.Logger;
import com.alibaba.dubbo.common.logger.LoggerFactory;
import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.user.iservice.IUserWalletRpcService;
import com.bhu.vas.api.vto.wallet.UserWalletDetailVTO;
import com.bhu.vas.api.vto.wallet.UserWithdrawApplyVTO;
import com.bhu.vas.rpc.facade.UserWalletUnitFacadeService;
import com.smartwork.msip.cores.orm.support.page.TailPage;

@Service("userWalletRpcService")
public class UserWalletRpcService implements IUserWalletRpcService{
	private final Logger logger = LoggerFactory.getLogger(UserWalletRpcService.class);
	@Resource
	private UserWalletUnitFacadeService userWalletUnitFacadeService;
	/*@Override
	public RpcResponseDTO<List<UserOAuthStateDTO>> fetchRegisterIdentifies(
			Integer uid) {
		logger.info(String.format("fetchRegisterIdentifies with uid[%s]",uid));
		return userOAuthUnitFacadeService.fetchRegisterIdentifies(uid);
	}
	@Override
	public RpcResponseDTO<Boolean> removeIdentifies(Integer uid, String identify) {
		logger.info(String.format("removeIdentifies with uid[%s] identify[%s]",uid,identify));
		return userOAuthUnitFacadeService.removeIdentifies(uid, identify);
	}
	@Override
	public RpcResponseDTO<Map<String, Object>> createIdentifies(
			Integer uid,
			String identify,String auid, String nick, String avatar,
			String device,String regIp,String deviceuuid, String ut
			) {
		logger.info(String.format("createIdentifies with identify[%s] auid [%s] nick[%s] avatar[%s]",identify,auid,nick,avatar));
		return userOAuthUnitFacadeService.createOrUpdateIdentifies(uid,identify, auid, nick, avatar, 
				device, regIp, deviceuuid, ut);
	}*/

	@Override
	public RpcResponseDTO<TailPage<UserWithdrawApplyVTO>> pagesWithdrawApplies(
			int reckoner, int tuid, String withdraw_status, int pageNo, int pageSize) {
		logger.info(String.format("pagesWithdrawApplies with reckoner[%s] tuid [%s] withdraw_status[%s] pn[%s] ps[%s]",
				reckoner,tuid,withdraw_status,pageNo,pageSize));
		return userWalletUnitFacadeService.pagesWithdrawApplies(reckoner, tuid, withdraw_status, pageNo, pageSize);
	}

	@Override
	public RpcResponseDTO<Boolean> verifyApplies(int reckoner, String applyid,
			boolean passed) {
		logger.info(String.format("verifyApplies with reckoner[%s] applyid [%s] passed[%s]",
				reckoner,applyid,passed));
		return userWalletUnitFacadeService.verifyApplies(reckoner, applyid, passed);
	}

	@Override
	public RpcResponseDTO<UserWithdrawApplyVTO> withdrawOper(int appid,int uid,String pwd, double cash,String remoteip) {
		logger.info(String.format("withdrawOper with appid[%s] uid[%s] cash[%s]",
				appid,uid,cash));
		return userWalletUnitFacadeService.withdrawOper(appid,uid, pwd, cash,remoteip);
	}

	@Override
	public RpcResponseDTO<UserWalletDetailVTO> walletDetail(int uid) {
		logger.info(String.format("walletDetail with uid[%s]",uid));
		return userWalletUnitFacadeService.walletDetail(uid);
	}
}
