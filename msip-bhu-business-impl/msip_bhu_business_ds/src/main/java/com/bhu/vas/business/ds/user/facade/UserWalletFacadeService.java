package com.bhu.vas.business.ds.user.facade;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import com.bhu.vas.api.helper.BusinessEnumType;
import com.bhu.vas.api.helper.BusinessEnumType.UWalletTransType;
import com.bhu.vas.api.rpc.user.model.User;
import com.bhu.vas.api.rpc.user.model.UserWallet;
import com.bhu.vas.api.rpc.user.model.UserWalletLog;
import com.bhu.vas.api.rpc.user.model.UserWalletWithdrawApply;
import com.bhu.vas.business.ds.user.service.UserService;
import com.bhu.vas.business.ds.user.service.UserWalletLogService;
import com.bhu.vas.business.ds.user.service.UserWalletService;
import com.bhu.vas.business.ds.user.service.UserWalletWithdrawApplyService;
import com.smartwork.msip.business.runtimeconf.BusinessRuntimeConfiguration;
import com.smartwork.msip.cores.helper.encrypt.BCryptHelper;
import com.smartwork.msip.cores.orm.support.criteria.ModelCriteria;
import com.smartwork.msip.cores.orm.support.criteria.PerfectCriteria.Criteria;
import com.smartwork.msip.cores.orm.support.page.TailPage;
import com.smartwork.msip.exception.BusinessI18nCodeException;
import com.smartwork.msip.jdo.ResponseErrorCode;

/**
 * 以后要加入事务处理
 * @author Edmond
 *
 */
@Service
public class UserWalletFacadeService {
	
	@Resource
	private UserService userService;
	
	@Resource
	private UserWalletService userWalletService;
	
	@Resource
	private UserWalletLogService userWalletLogService;
	
	@Resource
	private UserWalletWithdrawApplyService userWalletWithdrawApplyService;
	
	
	private User validateUser(int uid){
		if(uid <=0){
			throw new BusinessI18nCodeException(ResponseErrorCode.USER_DATA_NOT_EXIST);
		}
		User user = userService.getById(uid);
		if(user == null){
			throw new BusinessI18nCodeException(ResponseErrorCode.USER_DATA_NOT_EXIST);
		}
		return user;
	}
	
	/**
	 * 现金入账
	 * 入账成功需要写入UserWalletLog
	 */
	public void cashToUserWallet(int uid,double cash,
			String orderid,String desc
			){
		validateUser(uid);
		UserWallet uwallet = userWalletService.getOrCreateById(uid);
		uwallet.setCash(uwallet.getCash()+cash);
		userWalletService.update(uwallet);
		this.doWalletLog(uid, orderid, UWalletTransType.Recharge2C, 0d, cash, desc);
	}
	
	/**
	 * 分成现金入账
	 * @param uid
	 * @param cash
	 * @param orderid
	 * @param desc
	 */
	public void sharedealCashToUserWallet(int uid,double cash,
			String orderid,String desc
			){
		validateUser(uid);
		UserWallet uwallet = userWalletService.getOrCreateById(uid);
		uwallet.setCash(uwallet.getCash()+cash);
		userWalletService.update(uwallet);
		this.doWalletLog(uid, orderid, UWalletTransType.Sharedeal2C, 0d, cash, desc);
	}
	
	/**
	 * 虚拟币入账
	 * 入账成功需要写入UserWalletLog
	 */
	public void vcurrencyToUserWallet(int uid,double vcurrency,double cash,String desc){
		this.doWalletLog(uid, StringUtils.EMPTY, UWalletTransType.Recharge2V, vcurrency, cash, desc);
	}
	
	/**
	 * 现金出账
	 * 需要验证提现状态
	 * 需要验证提取密码（密码参数不能为空并且如果没有设置密码则不允许提现）
	 * 需要验证零钱是否小于要出账的金额
	 * 现金出账需要把提现状态标记
	 */
	public void cashFromUserWallet(int uid, String pwd,double cash){
		if(StringUtils.isEmpty(pwd) || cash <=0){
			throw new BusinessI18nCodeException(ResponseErrorCode.COMMON_DATA_PARAM_ERROR);
		}
		validateUser(uid);
		UserWallet uwallet = userWalletService.getById(uid);
		if(uwallet == null || uwallet.isWithdraw()){
			throw new BusinessI18nCodeException(ResponseErrorCode.USER_WALLET_WITHDRAW_OPER_BREAK);
		}
		if(uwallet.getCash() < BusinessRuntimeConfiguration.User_WalletWithdraw_Default_MaxLimit){
			throw new BusinessI18nCodeException(ResponseErrorCode.USER_WALLET_WITHDRAW_OPER_BREAK,new String[]{String.valueOf(BusinessRuntimeConfiguration.User_WalletWithdraw_Default_MaxLimit)});
		}
		if(uwallet.getCash() < cash){
			throw new BusinessI18nCodeException(ResponseErrorCode.USER_WALLET_CASH_NOT_SUFFICIENT);
		}
		if(!BCryptHelper.checkpw(pwd,uwallet.getPassword())){
			throw new BusinessI18nCodeException(ResponseErrorCode.USER_WALLET_VALIDATEPWD_FAILED);
		}
		uwallet.setCash(uwallet.getCash()-cash);
		uwallet.setWithdraw(true);
		userWalletService.update(uwallet);
		this.doWalletLog(uid, StringUtils.EMPTY, UWalletTransType.Withdraw, 0d, cash, null);
	}
	
	/**
	 * 提现审核失败或者远程upay支付失败
	 * @param uid
	 * @param cash
	 */
	public void cashRollback2UserWallet(int uid, double cash){
		if(cash <=0){
			throw new BusinessI18nCodeException(ResponseErrorCode.COMMON_DATA_PARAM_ERROR);
		}
		validateUser(uid);
		UserWallet uwallet = userWalletService.getById(uid);
		uwallet.setCash(uwallet.getCash()+cash);
		uwallet.setWithdraw(false);
		userWalletService.update(uwallet);
		this.doWalletLog(uid, StringUtils.EMPTY, UWalletTransType.WithdrawRollback, 0d, cash, null);
	}
	
	public void unlockWalletWithdrawStatus(int uid){
		validateUser(uid);
		UserWallet uwallet = userWalletService.getById(uid);
		uwallet.setWithdraw(false);
		userWalletService.update(uwallet);
	}
	
	/**
	 * 生成提现申请
	 * 申请提交后需要进行相关现金出账
	 * @param uid 申请用户id
	 * @param pwd
	 * @param cash 
	 */
	public void doWithdrawApply(int uid, String pwd,double cash){
		this.cashFromUserWallet(uid, pwd, cash);
		UserWalletWithdrawApply apply = new UserWalletWithdrawApply();
		apply.setCash(cash);
		apply.setWithdraw_oper(BusinessEnumType.UWithdrawStatus.Apply.getKey());
		userWalletWithdrawApplyService.update(apply);
	}
	
	
	public TailPage<UserWalletWithdrawApply> pageWithdrawApplies(Integer uid,BusinessEnumType.UWithdrawStatus status,int pageNo,int pageSize){
		ModelCriteria mc = new ModelCriteria();
		Criteria createCriteria = mc.createCriteria();
		if(uid != null && uid.intValue()>0){
			createCriteria.andColumnEqualTo("uid", uid);
		}
		if(status != null)
			createCriteria.andColumnEqualTo("withdraw_oper", status.getKey());
		createCriteria.andSimpleCaulse(" 1=1 ");
    	mc.setPageNumber(pageNo);
    	mc.setPageSize(pageSize);
    	mc.setOrderByClause(" created_at desc ");
		TailPage<UserWalletWithdrawApply> pages = userWalletWithdrawApplyService.findModelTailPageByModelCriteria(mc);
		return pages;
	}
	/**
	 * 提现申请审核
	 * 如果验证失败则金额返还到钱包
	 * @param uid 审核用户id
	 * @param applyid 申请流水号
	 */
	public void doWithdrawVerify(int reckoner,long applyid,boolean passed){
		validateUser(reckoner);
		UserWalletWithdrawApply apply = userWalletWithdrawApplyService.getById(applyid);
		if(apply == null){
			throw new BusinessI18nCodeException(ResponseErrorCode.COMMON_DATA_NOTEXIST,new String[]{"提现申请审核",String.valueOf(applyid)});
		}
		apply.setLast_reckoner(reckoner);
		if(passed){
			apply.setWithdraw_oper(BusinessEnumType.UWithdrawStatus.VerifySucceed.getKey());
		}else{
			apply.setWithdraw_oper(BusinessEnumType.UWithdrawStatus.VerifyFailed.getKey());
			//返还金额到用户钱包
			this.cashRollback2UserWallet(apply.getUid(), apply.getCash());
		}
		userWalletWithdrawApplyService.update(apply);
	}

	
	/**
	 * 对于审核通过的申请，远程uPay支付完成后进行此步骤
	 * 考虑成功和失败，失败则金额返还到钱包
	 */
	public void doWithdrawRemoteNotify(long applyid,boolean successed){
		UserWalletWithdrawApply apply = userWalletWithdrawApplyService.getById(applyid);
		if(apply == null){
			throw new BusinessI18nCodeException(ResponseErrorCode.COMMON_DATA_NOTEXIST,new String[]{"提现申请通知",String.valueOf(applyid)});
		}
		if(successed){
			apply.setWithdraw_oper(BusinessEnumType.UWithdrawStatus.WithdrawSucceed.getKey());
			//解锁钱包提现状态
			unlockWalletWithdrawStatus(apply.getUid());
		}else{
			apply.setWithdraw_oper(BusinessEnumType.UWithdrawStatus.WithdrawFailed.getKey());
			//返回金额并解锁钱包提现状态
			cashRollback2UserWallet(apply.getUid(),apply.getCash());
		}
	}
	
	/**
	 * 设置提现密码
	 * @param uid 审核用户id
	 * @param pwd 新的密码
	 */
	public void doUpdWithdrawPwd(int uid,String pwd){
		validateUser(uid);
		UserWallet uwallet = userWalletService.getById(uid);
		if(uwallet == null){
			throw new BusinessI18nCodeException(ResponseErrorCode.USER_WALLET_WITHDRAW_OPER_BREAK);
		}
		uwallet.setPlainpwd(pwd);
		uwallet.setPassword(null);
		userWalletService.update(uwallet);
	}
	
	private void doWalletLog(int uid,String orderid,
			BusinessEnumType.UWalletTransType transType,double sum,double cash,String desc){
		UserWalletLog wlog = new UserWalletLog();
		wlog.setUid(uid);
		wlog.setOrderid(orderid);
		wlog.setTransaction(transType.getKey());
		wlog.setTransaction_desc(transType.getName());
		wlog.setSum(sum);
		wlog.setCash(cash);
		wlog.setDesc(desc);
		userWalletLogService.insert(wlog);
	}
}
