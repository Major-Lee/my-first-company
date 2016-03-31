package com.bhu.vas.rpc.service.user;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.alibaba.dubbo.common.logger.Logger;
import com.alibaba.dubbo.common.logger.LoggerFactory;
import com.bhu.vas.api.dto.commdity.internal.pay.RequestWithdrawNotifyDTO;
import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.user.iservice.IUserWalletRpcService;
import com.bhu.vas.api.vto.wallet.UserWalletDetailVTO;
import com.bhu.vas.api.vto.wallet.UserWalletLogVTO;
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
	public RpcResponseDTO<TailPage<UserWithdrawApplyVTO>> pageWithdrawApplies(
			int reckoner, int tuid, String withdraw_status, int pageNo, int pageSize) {
		logger.info(String.format("pageWithdrawApplies with reckoner[%s] tuid [%s] withdraw_status[%s] pn[%s] ps[%s]",
				reckoner,tuid,withdraw_status,pageNo,pageSize));
		return userWalletUnitFacadeService.pageWithdrawApplies(reckoner, tuid, withdraw_status, pageNo, pageSize);
	}

	@Override
	public RpcResponseDTO<String> withdrawApplyStatus(int reckoner, String applyid) {
		logger.info(String.format("withdrawApplyStatus with reckoner[%s] applyid[%s]",reckoner,applyid));
		return userWalletUnitFacadeService.withdrawApplyStatus(reckoner, applyid);
	}
	
/*	@Override
	public RpcResponseDTO<Boolean> verifyApplies(int reckoner, String applyid,
			boolean passed) {
		logger.info(String.format("verifyApplies with reckoner[%s] applyid [%s] passed[%s]",
				reckoner,applyid,passed));
		return userWalletUnitFacadeService.verifyApplies(reckoner, applyid, passed);
	}*/

	@Override
	public RpcResponseDTO<UserWithdrawApplyVTO> withdrawOper(int appid,String payment_type,int uid,String pwd, double cash,String remoteip) {
		logger.info(String.format("withdrawOper with appid[%s] payment_type[%s] uid[%s] cash[%s]",
				appid,payment_type,uid,cash));
		return userWalletUnitFacadeService.withdrawOper(appid, payment_type,uid, pwd, cash,remoteip);
	}

	@Override
	public RpcResponseDTO<UserWalletDetailVTO> walletDetail(int uid) {
		logger.info(String.format("walletDetail with uid[%s]",uid));
		return userWalletUnitFacadeService.walletDetail(uid);
	}

	/*@Override
	public RpcResponseDTO<List<ThirdpartiesPaymentDTO>> fetchUserThirdpartiesPayments(
			int uid) {
		logger.info(String.format("fetchUserThirdpartiesPayments with uid[%s]",uid));
		return userWalletUnitFacadeService.fetchUserThirdpartiesPayments(uid);
	}

	@Override
	public RpcResponseDTO<Boolean> removeUserThirdpartiesPayment(int uid,
			String paymode) {
		logger.info(String.format("removeUserThirdpartiesPayment with uid[%s] paymode[%s]",uid,paymode));
		return userWalletUnitFacadeService.removeUserThirdpartiesPayment(uid, paymode);
	}

	@Override
	public RpcResponseDTO<List<ThirdpartiesPaymentDTO>> createUserThirdpartiesPayment(
			int uid, String payment_type, String id, String name,String avatar) {
		logger.info(String.format("createUserThirdpartiesPayment with uid[%s] payment_type[%s] id[%s] name[%s] avatar[%s]",uid,payment_type,id,name,avatar));
		return userWalletUnitFacadeService.createUserThirdpartiesPayment(uid, payment_type, id, name,avatar);
	}*/

	@Override
	public RpcResponseDTO<Boolean> withdrawPwdSet(int uid,String captcha, String pwd) {
		logger.info(String.format("withdrawPwdSet with uid[%s] captcha[%s] pwd[%s]",uid,captcha,pwd));
		return userWalletUnitFacadeService.withdrawPwdSet(uid,captcha, pwd);
	}

/*	@Override
	public RpcResponseDTO<Boolean> withdrawPwdUpd(int uid, String pwd,String npwd) {
		logger.info(String.format("withdrawPwdUpd with uid[%s] pwd[%s] npwd[%s]",uid,pwd,npwd));
		return userWalletUnitFacadeService.withdrawPwdUpd(uid, pwd, npwd);
	}*/

	@Override
	public RpcResponseDTO<TailPage<UserWalletLogVTO>> pageUserWalletlogs(int uid, String transmode,String transtype, int pageNo, int pageSize) {
		logger.info(String.format("pageUserWalletlogs with uid[%s] transmode[%s] transtype[%s] pn[%s] ps[%s]",uid,transmode,transtype,pageNo,pageSize));
		return userWalletUnitFacadeService.pageUserWalletlogs(uid, transmode, transtype, pageNo, pageSize);
	}

	@Override
	public RpcResponseDTO<RequestWithdrawNotifyDTO> doStartPaymentWithdrawApply(int reckoner,String applyid) {
		logger.info(String.format("doStartPaymentWithdrawApply with reckoner[%s] applyid[%s]",reckoner,applyid));
		return userWalletUnitFacadeService.doStartPaymentWithdrawApply(reckoner,applyid);
	}
	
	@Override
	public RpcResponseDTO<UserWithdrawApplyVTO> doWithdrawNotifyFromLocal(int reckoner,String applyid,boolean successed){
		logger.info(String.format("doWithdrawNotifyFromLocal with reckoner[%s] applyid[%s] successed[%s]",reckoner,applyid,successed));
		return userWalletUnitFacadeService.doWithdrawNotifyFromLocal(reckoner,applyid,successed);
	}

}
