package com.bhu.vas.business.ds.user.facade;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.bhu.vas.api.helper.BusinessEnumType;
import com.bhu.vas.api.helper.BusinessEnumType.ThirdpartiesPaymentType;
import com.bhu.vas.api.helper.BusinessEnumType.UWalletTransMode;
import com.bhu.vas.api.helper.BusinessEnumType.UWalletTransType;
import com.bhu.vas.api.rpc.user.dto.ThirdpartiesPaymentDTO;
import com.bhu.vas.api.rpc.user.dto.WithdrawRemoteResponseDTO;
import com.bhu.vas.api.rpc.user.model.UserThirdpartiesPayment;
import com.bhu.vas.api.rpc.user.model.UserWallet;
import com.bhu.vas.api.rpc.user.model.UserWalletConfigs;
import com.bhu.vas.api.rpc.user.model.UserWalletLog;
import com.bhu.vas.api.rpc.user.model.UserWalletWithdrawApply;
import com.bhu.vas.api.vto.wallet.UserWalletDetailVTO;
import com.bhu.vas.business.ds.user.service.UserService;
import com.bhu.vas.business.ds.user.service.UserThirdpartiesPaymentService;
import com.bhu.vas.business.ds.user.service.UserWalletConfigsService;
import com.bhu.vas.business.ds.user.service.UserWalletLogService;
import com.bhu.vas.business.ds.user.service.UserWalletService;
import com.bhu.vas.business.ds.user.service.UserWalletWithdrawApplyService;
import com.smartwork.msip.business.runtimeconf.BusinessRuntimeConfiguration;
import com.smartwork.msip.cores.helper.ArithHelper;
import com.smartwork.msip.cores.helper.StringHelper;
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
public class UserWalletFacadeService{
	private static final Logger logger = LoggerFactory.getLogger(UserWalletFacadeService.class);
	@Resource
	private UserService userService;
	
	@Resource
	private UserWalletService userWalletService;

	@Resource
	private UserWalletConfigsService userWalletConfigsService;

	@Resource
	private UserWalletLogService userWalletLogService;
	
	@Resource
	private UserWalletWithdrawApplyService userWalletWithdrawApplyService;
	
	@Resource
	private UserThirdpartiesPaymentService userThirdpartiesPaymentService;
	
/*	@Resource
	private WifiDeviceService wifiDeviceService;
	
	@Resource
	private UserDeviceService userDeviceService;*/
	
	
	
	public UserWalletDetailVTO walletDetail(int uid){
		UserWallet userWallet = userWallet(uid);
		UserWalletDetailVTO walletDetail = userWallet.toUserWalletDetailVTO();
		walletDetail.setPayments(fetchThirdpartiesPaymentTypes(uid));
		return walletDetail;
	}
	private UserWallet userWallet(int uid){
		UserValidateServiceHelper.validateUser(uid,this.userService);
		UserWallet uwallet = userWalletService.getOrCreateById(uid);
		return uwallet;
	}
	/**
	 * 现金充值 充值零钱
	 * 入账成功需要写入UserWalletLog
	 */
	public void cashToUserWallet(int uid,double cash,
			String orderid,String desc
			){
		logger.info(String.format("现金入账|充值现金 uid[%s] orderid[%s] cash[%s] desc[%s]", uid,orderid,cash,desc));
		UserValidateServiceHelper.validateUser(uid,this.userService);
		UserWallet uwallet = userWalletService.getOrCreateById(uid);
		uwallet.setCash(uwallet.getCash()+cash);
		userWalletService.update(uwallet);
		this.doWalletLog(uid, orderid,UWalletTransMode.RealMoneyPayment, UWalletTransType.Recharge2C, cash, cash,0d, desc);
	}
	
	/**
	 * 分成现金入账
	 * 如果mac地址没有被绑定或者设备本身不存在则 入账到指定的帐号中
	 * @param dmac 设备mac地址 通过mac查找其被哪个用户绑定
	 * @param cash 总收益现金
	 * @param orderid
	 * @param desc
	 */
/*	public UserWallet sharedealCashToUserWallet(String dmac,double cash,String orderid){
		logger.info(String.format("分成现金入账-1 dmac[%s] orderid[%s] cash[%s]", dmac,orderid,cash));
		int uid = UserWallet.Default_WalletUID_WhenUIDNotExist;
		boolean owner = false;
		if(StringUtils.isNotEmpty(dmac)){
			WifiDevice wifiDevice = wifiDeviceService.getById(dmac);
			if(wifiDevice != null){
				User user = null;
				Integer bindUid = userDeviceService.fetchBindUid(dmac);
				if(bindUid != null){
					user = userService.getById(bindUid);
					if(user != null){
						uid = user.getId();
						owner = true;
					}
				}
			}
		}
		return sharedealCashToUserWallet(uid,cash,orderid,owner);
	}*/

	/**
	 * 分成现金入账
	 * 如果uid为null 设置为指定的分成用户
	 * @param bindUid  设备绑定的用户uid 可能为null
	 * @param cash 总收益现金
	 * @param orderid
	 * @return
	 */
	public UserWallet sharedealCashToUserWalletWithBindUid(Integer bindUid, double cash, String orderid){
		int sharedeal_uid = UserWallet.Default_WalletUID_WhenUIDNotExist;
		boolean owner = false;
		if(bindUid != null){
			sharedeal_uid = bindUid;
			owner = true; 
		}
		return sharedealCashToUserWallet(sharedeal_uid, cash, orderid, owner);
	}
	/**
	 * 分成现金入账
	 * @param uid  具体的入账用户
	 * @param cash 总收益现金
	 * @param orderid
	 * @param desc
	 */
	public UserWallet sharedealCashToUserWallet(Integer uid, double cash, String orderid, boolean owner){
		logger.info(String.format("分成现金入账-1 uid[%s] orderid[%s] cash[%s] owner[%s]", uid,orderid,cash,owner));
		UserValidateServiceHelper.validateUser(uid,this.userService);
		UserWalletConfigs configs = userWalletConfigsService.userfulWalletConfigs(uid);
		double realIncommingCash = ArithHelper.round(ArithHelper.mul(cash, configs.getSharedeal_percent()),2);
		logger.info(String.format("分成现金入账-2 uid[%s] orderid[%s] cash[%s] incomming[%s] owner[%s]", uid,orderid,cash,realIncommingCash,owner));
		UserWallet uwallet = userWalletService.getOrCreateById(uid);
		uwallet.setCash(uwallet.getCash()+realIncommingCash);
		uwallet = userWalletService.update(uwallet);
		this.doWalletLog(uid, orderid, UWalletTransMode.SharedealPayment,UWalletTransType.ReadPacketSettle2C, realIncommingCash, realIncommingCash,0d, String.format("Total:%s Incomming:%s owner:%s", cash,realIncommingCash,owner));
		return uwallet;
	}
	
	/**
	 * 虚拟币入账
	 * 入账成功需要写入UserWalletLog
	 * TODO:待实现TBD
	 */
	public void vcurrencyToUserWallet(int uid,double vcurrency,double cash,String desc){
		//this.doWalletLog(uid, orderid,UWalletTransMode.RealMoneyPayment, UWalletTransType.Recharge2C, cash, cash,0d, desc);
		//this.doWalletLog(uid, StringUtils.EMPTY, UWalletTransType.Recharge2V, vcurrency, cash, desc);
	}
	
	/**
	 * 零钱出账 进行提现操作
	 * 需要验证提现状态
	 * 需要验证提取密码（密码参数不能为空并且如果没有设置密码则不允许提现）
	 * 需要验证零钱是否小于要出账的金额
	 * 现金出账需要把提现状态标记
	 */
	private UserWallet cashWithdrawOperFromUserWallet(int uid, String pwd,double cash){
		if(StringUtils.isEmpty(pwd) || cash <=0){
			throw new BusinessI18nCodeException(ResponseErrorCode.COMMON_DATA_PARAM_ERROR);
		}
		UserValidateServiceHelper.validateUser(uid,this.userService);
		UserWallet uwallet = userWalletService.getById(uid);
		if(uwallet == null || uwallet.isWithdraw()){
			throw new BusinessI18nCodeException(ResponseErrorCode.USER_WALLET_WITHDRAW_OPER_BREAK);
		}
		if(StringUtils.isEmpty(uwallet.getPassword())){
			throw new BusinessI18nCodeException(ResponseErrorCode.USER_WALLET_WITHDRAWPWD_SETOPER_NEEDED);
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
		double wallettotal = uwallet.getCash();
		uwallet.setCash(uwallet.getCash()-cash);
		uwallet.setWithdraw(true);
		uwallet = userWalletService.update(uwallet);
		this.doWalletLog(uid, StringUtils.EMPTY,UWalletTransMode.CashPayment, UWalletTransType.Cash2Realmoney, cash, cash,0d, String.format("WalletTotal:%s withdraw:%s ",wallettotal, cash));
		return uwallet;
	}
	
	/**
	 * 提现审核失败，直接进行返现操作
	 * @param uid
	 * @param cash
	 */
	private void cashWithdrawRollback2UserWalletWhenVerifyFailed(int uid, double cash){
		if(cash <=0){
			throw new BusinessI18nCodeException(ResponseErrorCode.COMMON_DATA_PARAM_ERROR);
		}
		UserValidateServiceHelper.validateUser(uid,this.userService);
		UserWallet uwallet = userWalletService.getById(uid);
		uwallet.setCash(uwallet.getCash()+cash);
		uwallet.setWithdraw(false);
		userWalletService.update(uwallet);
		this.doWalletLog(uid, StringUtils.EMPTY,UWalletTransMode.CashRollbackPayment, UWalletTransType.Rollback2C, cash, cash,0d, null);
	}
	
	/**
	 * 远程upay支付失败，对钱包的操作
	 * 则置提现申请状态为转账失败
	 * 由别的任务机制把系统中的提现失败的提现申请重新写入redis，并重新置为转账中状态
	 * @param uid
	 * @param cash
	 */
	private void cashWithdrawRollback2UserWalletWhenRemoteFailed(int uid, double cash){
		if(cash <=0){
			throw new BusinessI18nCodeException(ResponseErrorCode.COMMON_DATA_PARAM_ERROR);
		}
		UserValidateServiceHelper.validateUser(uid,this.userService);
		UserWallet uwallet = userWalletService.getById(uid);
		uwallet.setCash(uwallet.getCash()+cash);
		uwallet.setWithdraw(false);
		userWalletService.update(uwallet);
		this.doWalletLog(uid, StringUtils.EMPTY,UWalletTransMode.CashRollbackPayment, UWalletTransType.Rollback2C, cash, cash,0d, null);
	}
	
	/**
	 * 提现成功后解锁钱包状态
	 * @param uid
	 */
	private void unlockWalletWithdrawStatusWhenSuccessed(int uid){
		UserValidateServiceHelper.validateUser(uid,this.userService);
		UserWallet uwallet = userWalletService.getById(uid);
		uwallet.setWithdraw(false);
		userWalletService.update(uwallet);
	}
	
	private List<ThirdpartiesPaymentDTO> fetchThirdpartiesPaymentTypes(int uid){
		if(uid <=0){
			return Collections.emptyList();
		}
		UserThirdpartiesPayment payment = userThirdpartiesPaymentService.getById(uid);
		if(payment == null){
			return Collections.emptyList();
		}
		return new ArrayList<>(payment.values());
	}
	
	private ThirdpartiesPaymentDTO validateThirdpartiesPaymentType(int uid,ThirdpartiesPaymentType type){
		if(uid <=0){
			throw new BusinessI18nCodeException(ResponseErrorCode.USER_DATA_NOT_EXIST);
		}
		if(type == null){
			throw new BusinessI18nCodeException(ResponseErrorCode.USER_WALLET_PAYMENT_PARAM_EMPTY);
		}
		
		UserThirdpartiesPayment payment = userThirdpartiesPaymentService.getById(uid);
		if(payment == null){
			throw new BusinessI18nCodeException(ResponseErrorCode.USER_WALLET_PAYMENT_WASEMPTY);
		}
		if(payment.containsKey(type.getType())){
			return payment.getInnerModel(type.getType());
		}else{
			throw new BusinessI18nCodeException(ResponseErrorCode.USER_WALLET_PAYMENT_NOTDEFINED);
		}
		
	}
	/**
	 * 生成提现申请
	 * 申请提交后需要进行相关现金出账
	 * @param uid 申请用户id
	 * @param pwd
	 * @param cash 
	 */
	public UserWalletWithdrawApply doWithdrawApply(int appid,ThirdpartiesPaymentType type,int uid, String pwd,double cash,String remoteip){
		
		validateThirdpartiesPaymentType(uid,type);
		/*logger.info(String.format("生成提现申请 appid[%s] uid[%s] cash[%s] remoteIp[%s]", appid,uid,cash,remoteip));
		this.cashWithdrawOperFromUserWallet(uid, pwd, cash);
		UserWalletWithdrawApply apply = new UserWalletWithdrawApply();
		apply.setUid(uid);
		apply.setAppid(appid);
		apply.setPayment_type(type.getType());
		
		apply.setCash(cash);
		apply.setRemoteip(remoteip);
		apply.setWithdraw_oper(BusinessEnumType.UWithdrawStatus.Apply.getKey());
		apply.addResponseDTO(WithdrawRemoteResponseDTO.build(BusinessEnumType.UWithdrawStatus.Apply.getKey(), BusinessEnumType.UWithdrawStatus.Apply.getName()));
		apply = userWalletWithdrawApplyService.insert(apply);*/
		return doWithdrawApplyOper(appid,type,uid,pwd,cash,remoteip);
	}
	
	private synchronized UserWalletWithdrawApply doWithdrawApplyOper(int appid,ThirdpartiesPaymentType type,int uid, String pwd,double cash,String remoteip){
		logger.info(String.format("生成提现申请 appid[%s] uid[%s] cash[%s] remoteIp[%s]", appid,uid,cash,remoteip));
		this.cashWithdrawOperFromUserWallet(uid, pwd, cash);
		UserWalletWithdrawApply apply = new UserWalletWithdrawApply();
		apply.setUid(uid);
		apply.setAppid(appid);
		apply.setPayment_type(type.getType());
		
		apply.setCash(cash);
		apply.setRemoteip(remoteip);
		apply.setWithdraw_oper(BusinessEnumType.UWithdrawStatus.Apply.getKey());
		apply.addResponseDTO(WithdrawRemoteResponseDTO.build(BusinessEnumType.UWithdrawStatus.Apply.getKey(), BusinessEnumType.UWithdrawStatus.Apply.getName()));
		apply = userWalletWithdrawApplyService.insert(apply);
		return apply;
	}
	/**
	 * 提现申请审核列表
	 * @param uid
	 * @param status 
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
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
	public UserWalletWithdrawApply doWithdrawVerify(int reckoner,String applyid,boolean passed){
		//UserValidateServiceHelper.validateUser(reckoner,this.userService);
		if(StringUtils.isEmpty(applyid)){
			throw new BusinessI18nCodeException(ResponseErrorCode.COMMON_DATA_PARAM_ERROR,new String[]{"applyid:".concat(String.valueOf(applyid))});
		}
		UserWalletWithdrawApply apply = userWalletWithdrawApplyService.getById(applyid);
		if(apply == null){
			throw new BusinessI18nCodeException(ResponseErrorCode.COMMON_DATA_NOTEXIST,new String[]{"提现申请审核",String.valueOf(applyid)});
		}
		apply.setLast_reckoner(reckoner);
		BusinessEnumType.UWithdrawStatus current = null;
		if(passed){
			current = BusinessEnumType.UWithdrawStatus.VerifyPassed;
			apply.setWithdraw_oper(current.getKey());
		}else{
			current = BusinessEnumType.UWithdrawStatus.VerifyFailed;
			apply.setWithdraw_oper(current.getKey());
			//返还金额到用户钱包
			this.cashWithdrawRollback2UserWalletWhenVerifyFailed(apply.getUid(), apply.getCash());
		}
		apply.addResponseDTO(WithdrawRemoteResponseDTO.build(current.getKey(), current.getName()));
		apply = userWalletWithdrawApplyService.update(apply);
		return apply;
	}

	
	public UserWalletWithdrawApply doStartPaymentWithdrawApply(int reckoner,String applyid){
		if(StringUtils.isEmpty(applyid)){
			throw new BusinessI18nCodeException(ResponseErrorCode.COMMON_DATA_PARAM_ERROR,new String[]{"applyid:".concat(String.valueOf(applyid))});
		}
		UserWalletWithdrawApply apply = userWalletWithdrawApplyService.getById(applyid);
		if(apply == null){
			throw new BusinessI18nCodeException(ResponseErrorCode.COMMON_DATA_NOTEXIST,new String[]{"提现申请支付",String.valueOf(applyid)});
		}
		//如果状态不是 Apply|WithdrawDoing则抛出错误码
		if(BusinessEnumType.UWithdrawStatus.Apply.getKey().equals(apply.getWithdraw_oper()) 
				|| BusinessEnumType.UWithdrawStatus.WithdrawDoing.getKey().equals(apply.getWithdraw_oper())){
			apply.setLast_reckoner(reckoner);
			BusinessEnumType.UWithdrawStatus current = BusinessEnumType.UWithdrawStatus.WithdrawDoing;
			apply.setWithdraw_oper(current.getKey());
			apply.addResponseDTO(WithdrawRemoteResponseDTO.build(current.getKey(), current.getName()));
			apply = userWalletWithdrawApplyService.update(apply);
		}else{
			throw new BusinessI18nCodeException(ResponseErrorCode.USER_WALLET_WITHDRAW_APPLY_STATUS_NOTMATCHED,
					new String[]{String.valueOf(applyid),
					"current:".concat(apply.getWithdraw_oper()),
					String.format("should [%s|%s]", 
							BusinessEnumType.UWithdrawStatus.Apply.getKey(),
							BusinessEnumType.UWithdrawStatus.WithdrawDoing.getKey())
					});
		}
		return apply;
	}
	
	public UserWalletWithdrawApply doWithdrawNotifyFromLocal(String applyid,boolean successed){
		return doWithdrawNotifyFromRemote(applyid,successed);
	}
	
	/**
	 * 对于审核通过的申请，远程uPay支付完成后进行此步骤,成功后会callback 消息，执行此函数
	 * 考虑成功和失败，失败则金额返还到钱包
	 */
	public UserWalletWithdrawApply doWithdrawNotifyFromRemote(String applyid,boolean successed){//,String customer_desc
		logger.info(String.format("提现操作 applyid[%s] successed[%s]", applyid,successed));
		UserWalletWithdrawApply apply = userWalletWithdrawApplyService.getById(applyid);
		if(apply == null){
			logger.error(String.format("提现操作-失败 不存在此提现申请 applyid[%s]", applyid));
			throw new BusinessI18nCodeException(ResponseErrorCode.COMMON_DATA_NOTEXIST,new String[]{"提现申请通知",String.valueOf(applyid)});
		}
		//状态必须是uPay正在提现处理中
		if(!BusinessEnumType.UWithdrawStatus.WithdrawDoing.getKey().equals(apply.getWithdraw_oper())){
			logger.error(String.format("提现操作-失败 此提现申请 applyid[%s] 状态不匹配", applyid));
			throw new BusinessI18nCodeException(ResponseErrorCode.USER_WALLET_WITHDRAW_APPLY_STATUS_NOTMATCHED,new String[]{String.valueOf(applyid),"current:".concat(apply.getWithdraw_oper()),"should:".concat(BusinessEnumType.UWithdrawStatus.VerifyPassed.getKey())});
		}
		BusinessEnumType.UWithdrawStatus current = null;
		if(successed){
			current = BusinessEnumType.UWithdrawStatus.WithdrawSucceed;
			apply.setWithdraw_oper(current.getKey());
			//解锁钱包提现状态
			unlockWalletWithdrawStatusWhenSuccessed(apply.getUid());
			logger.info(String.format("提现操作-成功 applyid[%s] 并解锁钱包状态", applyid));
		}else{
			current = BusinessEnumType.UWithdrawStatus.WithdrawFailed;
			apply.setWithdraw_oper(current.getKey());
			cashWithdrawRollback2UserWalletWhenRemoteFailed(apply.getUid(),apply.getCash());
			logger.info(String.format("提现操作-失败 applyid[%s] 返现并解锁钱包状态", applyid));
		}
		apply.addResponseDTO(WithdrawRemoteResponseDTO.build(current.getKey(), current.getName()));
		apply = userWalletWithdrawApplyService.update(apply);
		return apply;
	}
	
	/**
	 * 设置提现密码
	 * @param uid 审核用户id
	 * @param pwd 新的密码
	 */
	public UserWallet doFirstSetWithdrawPwd(int uid,String pwd){
		UserValidateServiceHelper.validateUser(uid,this.userService);
		UserWallet uwallet = userWalletService.getOrCreateById(uid);
		if(StringUtils.isNotEmpty(uwallet.getPassword())){
			throw new BusinessI18nCodeException(ResponseErrorCode.COMMON_DATA_ALREADYEXIST,new String[]{"提现密码"});
		}
		uwallet.setPlainpwd(pwd);
		uwallet.setPassword(null);
		uwallet = userWalletService.update(uwallet);
		return uwallet;
	}
	
	public UserWallet doChangedWithdrawPwd(int uid,String pwd,String npwd){
		UserValidateServiceHelper.validateUser(uid,this.userService);
		UserWallet uwallet = userWalletService.getOrCreateById(uid);
		if(!BCryptHelper.checkpw(pwd,uwallet.getPassword())){
			throw new BusinessI18nCodeException(ResponseErrorCode.USER_WALLET_VALIDATEPWD_FAILED);
		}
		uwallet.setPlainpwd(npwd);
		uwallet.setPassword(null);
		uwallet = userWalletService.update(uwallet);
		return uwallet;
	}
	
	
	private void doWalletLog(int uid,String orderid,
			BusinessEnumType.UWalletTransMode transMode,
			BusinessEnumType.UWalletTransType transType,
			double rmoney,double cash,double vcurrency,String memo){
		UserWalletLog wlog = new UserWalletLog();
		wlog.setUid(uid);
		wlog.setOrderid(orderid);
		wlog.setTransmode(transMode.getKey());
		wlog.setTransmode_desc(transMode.getName());		
		wlog.setTranstype(transType.getKey());
		wlog.setTranstype_desc(transType.getName());
		wlog.setRmoney("0");
		wlog.setCash("0");
		wlog.setVcurrency("0");
		switch(transMode){
			case RealMoneyPayment://充值零钱、充值虎钻
				wlog.setRmoney(StringHelper.MINUS_STRING_GAP.concat(String.valueOf(rmoney)));
				if(UWalletTransType.Recharge2C.equals(transType)){
					wlog.setCash(StringHelper.PLUS_STRING_GAP.concat(String.valueOf(cash)));
				}
				if(UWalletTransType.Recharge2V.equals(transType)){
					wlog.setVcurrency(StringHelper.PLUS_STRING_GAP.concat(String.valueOf(vcurrency)));
				}
				break;
			case CashPayment://充值虎钻、零钱购买道具、提现
				wlog.setCash(StringHelper.MINUS_STRING_GAP.concat(String.valueOf(cash)));
				if(UWalletTransType.Recharge2V.equals(transType)){
					wlog.setVcurrency(StringHelper.PLUS_STRING_GAP.concat(String.valueOf(vcurrency)));
				}
				if(UWalletTransType.PurchaseGoodsUsedC.equals(transType)){
					;//日志里不体现具体道具
				}
				if(UWalletTransType.Cash2Realmoney.equals(transType)){
					wlog.setRmoney(StringHelper.PLUS_STRING_GAP.concat(String.valueOf(rmoney)));
				}
				break;
			case VCurrencyPayment://购买道具
				wlog.setVcurrency(StringHelper.MINUS_STRING_GAP.concat(String.valueOf(vcurrency)));
				break;
			case SharedealPayment://红包打赏结算
				wlog.setRmoney(StringHelper.MINUS_STRING_GAP.concat(String.valueOf(rmoney)));
				wlog.setCash(StringHelper.PLUS_STRING_GAP.concat(String.valueOf(cash)));
				break;
			case CashRollbackPayment://
				wlog.setRmoney(StringHelper.MINUS_STRING_GAP.concat(String.valueOf(rmoney)));
				wlog.setCash(StringHelper.PLUS_STRING_GAP.concat(String.valueOf(cash)));
				break;
		}
		wlog.setMemo(memo);
		userWalletLogService.insert(wlog);
	}
	
	//userThirdpartiesPaymentService
	public List<ThirdpartiesPaymentDTO> addThirdpartiesPayment(int uid,ThirdpartiesPaymentType type,ThirdpartiesPaymentDTO paymentDTO){
		UserThirdpartiesPayment payment = userThirdpartiesPaymentService.getOrCreateById(uid);
		boolean ret = payment.addOrReplace(type, paymentDTO);
		if(ret){
			payment = userThirdpartiesPaymentService.update(payment);
		}
		return new ArrayList<>(payment.values());
	}
	public void removeThirdpartiesPayment(int uid,ThirdpartiesPaymentType type){
		UserThirdpartiesPayment payment = userThirdpartiesPaymentService.getById(uid);
		if(payment != null){
			boolean ret = payment.remove(type);
			if(ret){
				userThirdpartiesPaymentService.update(payment);
			}
		}
	}
	
	public ThirdpartiesPaymentDTO fetchFirstThirdpartiesPayment(int uid){
		UserThirdpartiesPayment payment = userThirdpartiesPaymentService.getById(uid);
		if(payment == null ) return null;
		ArrayList<ThirdpartiesPaymentDTO> payments = new ArrayList<>(payment.values());
		if(payments.isEmpty()) return null;
		return payments.get(0);
	}
	
	public ThirdpartiesPaymentDTO fetchThirdpartiesPayment(int uid,ThirdpartiesPaymentType type){
		UserThirdpartiesPayment payment = userThirdpartiesPaymentService.getById(uid);
		if(payment != null){
			return payment.getInnerModel(type.getType());
		}
		return null;
	}
	
	public List<ThirdpartiesPaymentDTO> fetchAllThirdpartiesPayment(int uid){
		UserThirdpartiesPayment payment = userThirdpartiesPaymentService.getById(uid);
		if(payment != null){
			return new ArrayList<>(payment.values());
		}
		return Collections.emptyList();
	}
	
	/**
	 * 钱包日志列表
	 * @param uid
	 * @param status 
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	public TailPage<UserWalletLog> pageUserWalletlogs(Integer uid,
			BusinessEnumType.UWalletTransMode transmode,
			BusinessEnumType.UWalletTransType transtype,
			int pageNo,int pageSize){
		ModelCriteria mc = new ModelCriteria();
		Criteria createCriteria = mc.createCriteria();
		if(uid != null && uid.intValue()>0){
			createCriteria.andColumnEqualTo("uid", uid);
		}
		if(transmode != null)
			createCriteria.andColumnEqualTo("transmode", transmode.getKey());
		if(transtype != null)
			createCriteria.andColumnEqualTo("transtype", transtype.getKey());
		createCriteria.andSimpleCaulse(" 1=1 ");
    	mc.setPageNumber(pageNo);
    	mc.setPageSize(pageSize);
    	mc.setOrderByClause(" updated_at desc ");
		TailPage<UserWalletLog> pages = userWalletLogService.findModelTailPageByModelCriteria(mc);
		return pages;
	}
	
	
	
	public UserService getUserService() {
		return userService;
	}

	public UserWalletService getUserWalletService() {
		return userWalletService;
	}

	public UserWalletConfigsService getUserWalletConfigsService() {
		return userWalletConfigsService;
	}

	public UserWalletLogService getUserWalletLogService() {
		return userWalletLogService;
	}

	public UserWalletWithdrawApplyService getUserWalletWithdrawApplyService() {
		return userWalletWithdrawApplyService;
	}

	public UserThirdpartiesPaymentService getUserThirdpartiesPaymentService() {
		return userThirdpartiesPaymentService;
	}
	
}
