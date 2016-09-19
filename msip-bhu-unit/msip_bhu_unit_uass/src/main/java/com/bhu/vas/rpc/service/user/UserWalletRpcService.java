package com.bhu.vas.rpc.service.user;

import java.util.Date;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.alibaba.dubbo.common.logger.Logger;
import com.alibaba.dubbo.common.logger.LoggerFactory;
import com.bhu.vas.api.dto.commdity.internal.pay.RequestWithdrawNotifyDTO;
import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.unifyStatistics.vto.UcloudMacStatisticsVTO;
import com.bhu.vas.api.rpc.user.dto.ShareDealWalletSummaryProcedureVTO;
import com.bhu.vas.api.rpc.user.iservice.IUserWalletRpcService;
import com.bhu.vas.api.vto.statistics.FincialStatisticsVTO;
import com.bhu.vas.api.vto.statistics.RankingListVTO;
import com.bhu.vas.api.vto.wallet.UserWalletDetailVTO;
import com.bhu.vas.api.vto.wallet.UserWalletLogFFVTO;
import com.bhu.vas.api.vto.wallet.UserWalletLogVTO;
import com.bhu.vas.api.vto.wallet.UserWithdrawApplyVTO;
import com.bhu.vas.rpc.facade.UserWalletUnitFacadeService;
import com.smartwork.msip.cores.orm.support.page.TailPage;

@Service("userWalletRpcService")
public class UserWalletRpcService implements IUserWalletRpcService{
	private final Logger logger = LoggerFactory.getLogger(UserWalletRpcService.class);
	@Resource
	private UserWalletUnitFacadeService userWalletUnitFacadeService;
	
	/**
	 * update by dongrui 2016-06-14 start
	 * 接口增加参数payment_type 提现类型【微信&对公账号】
	 * update by dongtui 2016-06-14 E N D
	 */
	@Override
	public RpcResponseDTO<TailPage<UserWithdrawApplyVTO>> pageWithdrawApplies(int reckoner, int tuid, String withdraw_status,String payment_type,String startTime,String endTime,int pageNo, int pageSize) {
		logger.info(String.format("pageWithdrawApplies with reckoner[%s] tuid [%s] withdraw_status[%s] payment_type[%s] startTime[%s] endtTime[%s] pn[%s] ps[%s]",
				reckoner,tuid,withdraw_status,payment_type,startTime,endTime,pageNo,pageSize));
		return userWalletUnitFacadeService.pageWithdrawApplies(reckoner, tuid, withdraw_status,payment_type,startTime,endTime,pageNo, pageSize);
	}

	@Override
	public RpcResponseDTO<String> withdrawApplyStatus(int reckoner, String applyid) {
		logger.info(String.format("withdrawApplyStatus with reckoner[%s] applyid[%s]",reckoner,applyid));
		return userWalletUnitFacadeService.withdrawApplyStatus(reckoner, applyid);
	}
	
	@Override
	public RpcResponseDTO<Boolean> verifyApplies(int reckoner, String applyid,
			boolean passed) {
		logger.info(String.format("verifyApplies with reckoner[%s] applyid [%s] passed[%s]",
				reckoner,applyid,passed));
		return userWalletUnitFacadeService.verifyApplies(reckoner, applyid, passed);
	}

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
	public RpcResponseDTO<RequestWithdrawNotifyDTO> doStartPaymentWithdrawApply(int reckoner,String applyid,String note) {
		logger.info(String.format("doStartPaymentWithdrawApply with reckoner[%s] applyid[%s]",reckoner,applyid));
		return userWalletUnitFacadeService.doStartPaymentWithdrawApply(reckoner,applyid,note); 
	}
	
	@Override
	public RpcResponseDTO<UserWithdrawApplyVTO> doWithdrawNotifyFromLocal(int reckoner,String applyid,boolean successed){
		logger.info(String.format("doWithdrawNotifyFromLocal with reckoner[%s] applyid[%s] successed[%s] ",reckoner,applyid,successed));
		return userWalletUnitFacadeService.doWithdrawNotifyFromLocal(reckoner,applyid,successed);
	}

	@Override
	public RpcResponseDTO<ShareDealWalletSummaryProcedureVTO> walletLogStatistics(
			int uid) {
		logger.info(String.format("doWithdrawNotifyFromLocal with uid[%s]",uid));
		return userWalletUnitFacadeService.walletLogStatistics(uid);
	}
	@Override
	public RpcResponseDTO<FincialStatisticsVTO> fincialStatistics(String time) {
		logger.info(String.format("fincialStatistics with time[%s]",time));
		return userWalletUnitFacadeService.fincialStatistics(time);
	}

	@Override
	public RpcResponseDTO<Boolean> directDrawPresent(int uid, String thirdparties_orderid,double cash,String desc) {
		logger.info(String.format("directDrawPresent with uid[%s] thirdparties_orderid[%s] cash[%s] desc[%s]",uid,thirdparties_orderid,cash,desc));
		return userWalletUnitFacadeService.directDrawPresent(uid,thirdparties_orderid,cash,desc);
	}

	@Override
	public RpcResponseDTO<RankingListVTO> rankingList(int uid,int type,String time) {
		logger.info(String.format("rankingList with uid[%s] type[%s] time[%s]",uid,type,time));
		return userWalletUnitFacadeService.rankingList(uid,type,time);
	}

	@Override
	public RpcResponseDTO<UcloudMacStatisticsVTO> richStatistics(int uid) {
		logger.info(String.format("richStatistics"));
		return userWalletUnitFacadeService.richStatistics(uid);

	}
	
	@Override
	public RpcResponseDTO<TailPage<UserWalletLogFFVTO>> pageUserWalletlogsByFeifan(int uid, String transmode,String transtype, 
			Date start_date, Date end_date, int pageNo, int pageSize) {
		logger.info(String.format("pageUserWalletlogsByFeifan with uid[%s] transmode[%s] transtype[%s] start_date[%s] end_date[%s] pn[%s] ps[%s]",
				uid, transmode, transtype, start_date, end_date, pageNo, pageSize));
		return userWalletUnitFacadeService.pageUserWalletlogsByFeifan(uid, transmode, transtype, start_date, end_date, pageNo, pageSize);
	}
}
