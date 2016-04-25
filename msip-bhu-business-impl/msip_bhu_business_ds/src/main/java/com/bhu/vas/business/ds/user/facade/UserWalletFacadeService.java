package com.bhu.vas.business.ds.user.facade;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.bhu.vas.api.dto.procedure.ShareDealWalletProcedureDTO;
import com.bhu.vas.api.dto.procedure.ShareDealWalletSummaryProcedureDTO;
import com.bhu.vas.api.helper.BusinessEnumType;
import com.bhu.vas.api.helper.BusinessEnumType.OAuthType;
import com.bhu.vas.api.helper.BusinessEnumType.UWalletTransMode;
import com.bhu.vas.api.helper.BusinessEnumType.UWalletTransType;
import com.bhu.vas.api.rpc.charging.dto.SharedealInfo;
import com.bhu.vas.api.rpc.user.dto.ShareDealWalletSummaryProcedureVTO;
import com.bhu.vas.api.rpc.user.dto.UserOAuthStateDTO;
import com.bhu.vas.api.rpc.user.dto.WithdrawRemoteResponseDTO;
import com.bhu.vas.api.rpc.user.model.UserOAuthState;
import com.bhu.vas.api.rpc.user.model.UserWallet;
import com.bhu.vas.api.rpc.user.model.UserWalletLog;
import com.bhu.vas.api.rpc.user.model.UserWalletWithdrawApply;
import com.bhu.vas.api.rpc.user.model.pk.UserOAuthStatePK;
import com.bhu.vas.api.rpc.user.notify.IWalletNotifyCallback;
import com.bhu.vas.api.rpc.user.notify.IWalletSharedealNotifyCallback;
import com.bhu.vas.api.vto.wallet.UserWalletDetailVTO;
import com.bhu.vas.business.ds.charging.facade.ChargingFacadeService;
import com.bhu.vas.business.ds.user.service.UserService;
import com.bhu.vas.business.ds.user.service.UserWalletLogService;
import com.bhu.vas.business.ds.user.service.UserWalletService;
import com.bhu.vas.business.ds.user.service.UserWalletWithdrawApplyService;
import com.smartwork.msip.business.runtimeconf.BusinessRuntimeConfiguration;
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
	private ChargingFacadeService chargingFacadeService;
	
	@Resource
	private UserWalletLogService userWalletLogService;
	
	@Resource
	private UserWalletWithdrawApplyService userWalletWithdrawApplyService;

	@Resource
	private UserOAuthFacadeService userOAuthFacadeService;
	
	public UserWalletDetailVTO walletDetail(int uid){
		UserWallet userWallet = userWallet(uid);
		UserWalletDetailVTO walletDetail = userWallet.toUserWalletDetailVTO();
		walletDetail.setPayments(userOAuthFacadeService.fetchRegisterIdentifies(uid,true));
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
		this.doWalletLog(uid, orderid,UWalletTransMode.RealMoneyPayment, UWalletTransType.Recharge2C,StringUtils.EMPTY, cash, cash,0d, desc);
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
	/*public UserWallet sharedealCashToUserWalletWithBindUid(Integer bindUid, double cash, String orderid,String description){
		int sharedeal_uid = UserWallet.Default_WalletUID_WhenUIDNotExist;
		boolean owner = false;
		if(bindUid != null){
			sharedeal_uid = bindUid;
			owner = true; 
		}
		return sharedealCashToUserWallet(sharedeal_uid, cash, orderid, owner,description);
	}*/
	
	/**
	 * 分成现金入账
	 * TODO:分成现金分为几部分 
	 * 	绑定用户
		我司
		TODO：需要改成部分由存储过程实现
	 * @param uid  具体的入账用户
	 * @param cash 总收益现金
	 * @param orderid
	 * @param desc
	 */
	public UserWallet sharedealCashToUserWallet(String dmac, double cash, String orderid,String description){
		logger.info(String.format("分成现金入账-1 dmac[%s] orderid[%s] cash[%s]", dmac,orderid,cash));
		//UserValidateServiceHelper.validateUser(uid,this.userService);
		//UserWalletConfigs configs = userWalletConfigsService.userfulWalletConfigs(uid);
		//double realIncommingCash = ArithHelper.round(ArithHelper.mul(cash, configs.getSharedeal_percent()),2);
		SharedealInfo sharedeal = chargingFacadeService.calculateSharedeal(dmac, orderid, cash);
		//double realIncommingCash = userWithdrawCostConfigsService.calculateSharedeal(uid, cash);
		logger.info(String.format("分成现金入账-2 uid[%s] orderid[%s] cash[%s] incomming[%s] owner[%s]", sharedeal.getOwner(),orderid,cash,sharedeal.getOwner_cash(),sharedeal.isBelong()));
		UserWallet uwallet = userWalletService.getOrCreateById(sharedeal.getOwner());
		uwallet.setCash(uwallet.getCash()+sharedeal.getOwner_cash());
		uwallet = userWalletService.update(uwallet);
		this.doWalletLog(sharedeal.getOwner(), orderid, UWalletTransMode.SharedealPayment,UWalletTransType.ReadPacketSettle2C,description, sharedeal.getOwner_cash(), sharedeal.getOwner_cash(),0d, String.format("Total:%s Incomming:%s owner:%s", cash,sharedeal.getOwner_cash(),sharedeal.isBelong()));
		return uwallet;
	}
	
	/**
	 * 分成现金入账 存储过程实现
	 * TODO:分成现金分为几部分 
	 * 	绑定用户
		我司
		TODO：需要改成部分由存储过程实现
	 * @param uid  具体的入账用户
	 * @param cash 总收益现金
	 * @param orderid
	 * @param desc
	 */
	public int sharedealCashToUserWalletWithProcedure(String dmac, double cash, String orderid,String description,IWalletSharedealNotifyCallback callback){
		logger.info(String.format("分成现金入账-1 dmac[%s] orderid[%s] cash[%s]", dmac,orderid,cash));
		SharedealInfo sharedeal = chargingFacadeService.calculateSharedeal(dmac, orderid, cash);
		ShareDealWalletProcedureDTO procedureDTO = ShareDealWalletProcedureDTO.buildWith(sharedeal);
		procedureDTO.setTransmode(UWalletTransMode.SharedealPayment.getKey());
		procedureDTO.setTransmode_desc(UWalletTransMode.SharedealPayment.getName());
		procedureDTO.setTranstype(UWalletTransType.ReadPacketSettle2C.getKey());
		procedureDTO.setTranstype_desc(UWalletTransType.ReadPacketSettle2C.getName());
		procedureDTO.setDescription(description);
		procedureDTO.setOwner_memo(String.format("Total:%s Incomming:%s owner:%s mac:%s", cash,sharedeal.getOwner_cash(),sharedeal.isBelong(),sharedeal.getMac()));
		procedureDTO.setManufacturer_memo(String.format("Total:%s Incomming:%s manufacturer:%s mac:%s", cash,sharedeal.getManufacturer_cash(),sharedeal.isBelong(),sharedeal.getMac()));
		int executeRet = userWalletService.executeProcedure(procedureDTO);
		if(executeRet == 0){
			logger.info( String.format("分成现金入账-成功 uid[%s] orderid[%s] cash[%s] incomming[%s] owner[%s]", sharedeal.getOwner(),orderid,cash,sharedeal.getOwner_cash(),sharedeal.isBelong()));
			if(sharedeal.isBelong() && callback != null){
				callback.notifyCashSharedealOper(sharedeal.getOwner(),sharedeal.getOwner_cash());
			}
		}else
			logger.error(String.format("分成现金入账-失败 uid[%s] orderid[%s] cash[%s] incomming[%s] owner[%s]", sharedeal.getOwner(),orderid,cash,sharedeal.getOwner_cash(),sharedeal.isBelong()));
		//uwallet.setCash(uwallet.getCash()+sharedeal.getOwner_cash());
		//uwallet = userWalletService.update(uwallet);
		//this.doWalletLog(sharedeal.getOwner(), orderid, UWalletTransMode.SharedealPayment,UWalletTransType.ReadPacketSettle2C,description, sharedeal.getOwner_cash(), sharedeal.getOwner_cash(),0d, String.format("Total:%s Incomming:%s owner:%s mac:%s", cash,sharedeal.getOwner_cash(),sharedeal.isBelong(),sharedeal.getMac()));
		//this.doWalletLog(sharedeal.getManufacturer(), orderid, UWalletTransMode.SharedealPayment,UWalletTransType.ReadPacketSettle2C,description, sharedeal.getManufacturer_cash(), sharedeal.getManufacturer_cash(),0d, String.format("Total:%s Incomming:%s manufacturer:%s mac:%s", cash,sharedeal.getOwner_cash(),sharedeal.isBelong(),sharedeal.getMac()));
		return executeRet;
	}
	
	public ShareDealWalletSummaryProcedureVTO sharedealSummaryWithProcedure(int uid){
		ShareDealWalletSummaryProcedureDTO procedureDTO = new ShareDealWalletSummaryProcedureDTO();
		procedureDTO.setUserid(uid);
		int executeRet = userWalletService.executeProcedure(procedureDTO);
		if(executeRet == 0){
			;
		}else{
			throw new BusinessI18nCodeException(ResponseErrorCode.COMMON_BUSINESS_ERROR,new String[]{procedureDTO.getName()});
		}
		return procedureDTO.toVTO();
		/*
		logger.info(String.format("分成现金入账-1 dmac[%s] orderid[%s] cash[%s]", dmac,orderid,cash));
		SharedealInfo sharedeal = chargingFacadeService.calculateSharedeal(dmac, orderid, cash);
		ShareDealWalletProcedureDTO procedureDTO = ShareDealWalletProcedureDTO.buildWith(sharedeal);
		procedureDTO.setTransmode(UWalletTransMode.SharedealPayment.getKey());
		procedureDTO.setTransmode_desc(UWalletTransMode.SharedealPayment.getName());
		procedureDTO.setTranstype(UWalletTransType.ReadPacketSettle2C.getKey());
		procedureDTO.setTranstype_desc(UWalletTransType.ReadPacketSettle2C.getName());
		procedureDTO.setDescription(description);
		procedureDTO.setOwner_memo(String.format("Total:%s Incomming:%s owner:%s mac:%s", cash,sharedeal.getOwner_cash(),sharedeal.isBelong(),sharedeal.getMac()));
		procedureDTO.setManufacturer_memo(String.format("Total:%s Incomming:%s manufacturer:%s mac:%s", cash,sharedeal.getManufacturer_cash(),sharedeal.isBelong(),sharedeal.getMac()));
		int executeRet = userWalletService.executeProcedure(procedureDTO);
		if(executeRet == 0){
			logger.info( String.format("分成现金入账-成功 uid[%s] orderid[%s] cash[%s] incomming[%s] owner[%s]", sharedeal.getOwner(),orderid,cash,sharedeal.getOwner_cash(),sharedeal.isBelong()));
			if(sharedeal.isBelong() && callback != null){
				callback.notifyCashSharedealOper(sharedeal.getOwner(),sharedeal.getOwner_cash());
			}
		}else
			logger.error(String.format("分成现金入账-失败 uid[%s] orderid[%s] cash[%s] incomming[%s] owner[%s]", sharedeal.getOwner(),orderid,cash,sharedeal.getOwner_cash(),sharedeal.isBelong()));
		//uwallet.setCash(uwallet.getCash()+sharedeal.getOwner_cash());
		//uwallet = userWalletService.update(uwallet);
		//this.doWalletLog(sharedeal.getOwner(), orderid, UWalletTransMode.SharedealPayment,UWalletTransType.ReadPacketSettle2C,description, sharedeal.getOwner_cash(), sharedeal.getOwner_cash(),0d, String.format("Total:%s Incomming:%s owner:%s mac:%s", cash,sharedeal.getOwner_cash(),sharedeal.isBelong(),sharedeal.getMac()));
		//this.doWalletLog(sharedeal.getManufacturer(), orderid, UWalletTransMode.SharedealPayment,UWalletTransType.ReadPacketSettle2C,description, sharedeal.getManufacturer_cash(), sharedeal.getManufacturer_cash(),0d, String.format("Total:%s Incomming:%s manufacturer:%s mac:%s", cash,sharedeal.getOwner_cash(),sharedeal.isBelong(),sharedeal.getMac()));
		return executeRet;*/
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
	private UserWallet cashWithdrawOperFromUserWallet(int uid, String pwd,double cash,String description,IWalletNotifyCallback callback){
		if(StringUtils.isEmpty(pwd) || cash <=0){
			throw new BusinessI18nCodeException(ResponseErrorCode.COMMON_DATA_PARAM_ERROR);
		}
		if(cash < BusinessRuntimeConfiguration.User_WalletWithdraw_Default_Withdraw_MinLimit){
			throw new BusinessI18nCodeException(ResponseErrorCode.USER_WALLET_WITHDRAW_LOWERTHEN_MINLIMIT,new String[]{String.valueOf(BusinessRuntimeConfiguration.User_WalletWithdraw_Default_Withdraw_MinLimit)});
		}
		UserValidateServiceHelper.validateUser(uid,this.userService);
		UserWallet uwallet = userWalletService.getById(uid);
		if(uwallet == null){
			throw new BusinessI18nCodeException(ResponseErrorCode.COMMON_DATA_NOTEXIST,new String[]{"用户钱包"});
		}
		if(uwallet.isWithdraw()){
			throw new BusinessI18nCodeException(ResponseErrorCode.USER_WALLET_WITHDRAW_OPER_DOING);
		}
		if(StringUtils.isEmpty(uwallet.getPassword())){
			throw new BusinessI18nCodeException(ResponseErrorCode.USER_WALLET_WITHDRAWPWD_SETOPER_NEEDED);
		}
		if(uwallet.getCash() < BusinessRuntimeConfiguration.User_WalletWithdraw_Default_Remainer_MinLimit){
			throw new BusinessI18nCodeException(ResponseErrorCode.USER_WALLET_CASH_LOWERTHAN_WITHDRAW_MINLIMIT,new String[]{String.valueOf(BusinessRuntimeConfiguration.User_WalletWithdraw_Default_Remainer_MinLimit)});
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
		String orderid = callback.notifyCashWithdrawOper(cash);
		this.doWalletLog(uid, orderid,UWalletTransMode.CashPayment, UWalletTransType.Cash2Realmoney,description, cash, cash,0d, String.format("WalletTotal:%s withdraw:%s ",wallettotal, cash));
		return uwallet;
	}
	
	/**
	 * 提现审核失败，直接进行返现操作
	 * @param uid
	 * @param cash
	 */
	private void cashWithdrawRollback2UserWalletWhenVerifyFailed(int uid, double cash,String description){
		if(cash <=0){
			throw new BusinessI18nCodeException(ResponseErrorCode.COMMON_DATA_PARAM_ERROR);
		}
		UserValidateServiceHelper.validateUser(uid,this.userService);
		UserWallet uwallet = userWalletService.getById(uid);
		uwallet.setCash(uwallet.getCash()+cash);
		uwallet.setWithdraw(false);
		userWalletService.update(uwallet);
		this.doWalletLog(uid, StringUtils.EMPTY,UWalletTransMode.CashRollbackPayment, UWalletTransType.Rollback2C,description, cash, cash,0d, null);
	}
	
	/**
	 * 远程upay支付失败，对钱包的操作
	 * 则置提现申请状态为转账失败
	 * 由别的任务机制把系统中的提现失败的提现申请重新写入redis，并重新置为转账中状态
	 * @param uid
	 * @param cash
	 */
	private void cashWithdrawRollback2UserWalletWhenRemoteFailed(int uid, double cash,String description){
		if(cash <=0){
			throw new BusinessI18nCodeException(ResponseErrorCode.COMMON_DATA_PARAM_ERROR);
		}
		UserValidateServiceHelper.validateUser(uid,this.userService);
		UserWallet uwallet = userWalletService.getById(uid);
		uwallet.setCash(uwallet.getCash()+cash);
		uwallet.setWithdraw(false);
		userWalletService.update(uwallet);
		this.doWalletLog(uid, StringUtils.EMPTY,UWalletTransMode.CashRollbackPayment, UWalletTransType.Rollback2C,description, cash, cash,0d, null);
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
	
/*	private List<ThirdpartiesPaymentDTO> fetchThirdpartiesPaymentTypes(int uid){
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
		
	}*/
	
	private UserOAuthStateDTO validateOAuthPaymentType(int uid,OAuthType type){
		if(uid <=0){
			throw new BusinessI18nCodeException(ResponseErrorCode.USER_DATA_NOT_EXIST);
		}
		if(type == null){
			throw new BusinessI18nCodeException(ResponseErrorCode.USER_WALLET_PAYMENT_PARAM_EMPTY);
		}
		if(!type.isPayment()){
			throw new BusinessI18nCodeException(ResponseErrorCode.USER_WALLET_PAYMENT_PARAM_EMPTY,new String[]{type.getType()});
		}
		UserOAuthStatePK pk = new UserOAuthStatePK(uid,type.getType());
		UserOAuthState oauthState = userOAuthFacadeService.getUserOAuthStateService().getById(pk);
		
		if(oauthState == null){
			throw new BusinessI18nCodeException(ResponseErrorCode.USER_WALLET_PAYMENT_NOTDEFINED);
		}
		UserOAuthStateDTO innerModel = oauthState.getInnerModel();
		if(innerModel == null){
			throw new BusinessI18nCodeException(ResponseErrorCode.USER_WALLET_PAYMENT_NOTDEFINED);
		}
		
		//如果是微信的话，需要判定值是否存在openid
		if(OAuthType.Weichat.getType().equals(type.getType())){
			if(StringUtils.isEmpty(innerModel.getOpenid())){
				throw new BusinessI18nCodeException(ResponseErrorCode.USER_WALLET_PAYMENT_DATA_IMPERFECT,new String[]{"weichat-openid"} );
			}
		}
		
		return innerModel;
		
		/*UserThirdpartiesPayment payment = userThirdpartiesPaymentService.getById(uid);
		if(payment == null){
			throw new BusinessI18nCodeException(ResponseErrorCode.USER_WALLET_PAYMENT_WASEMPTY);
		}
		if(payment.containsKey(type.getType())){
			return payment.getInnerModel(type.getType());
		}else{
			throw new BusinessI18nCodeException(ResponseErrorCode.USER_WALLET_PAYMENT_NOTDEFINED);
		}*/
		
	}
	
	/**
	 * 生成提现申请
	 * 申请提交后需要进行相关现金出账
	 * @param uid 申请用户id
	 * @param pwd
	 * @param cash 
	 */
	public UserWalletWithdrawApply doWithdrawApply(int appid,OAuthType type,int uid, String pwd,double cash,String remoteip){
		validateOAuthPaymentType(uid,type);
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
	private Map<String,String> lockWithdrawMap = new HashMap<>();
	//给每个用户维护一个锁定key，没有就创建一个
	private synchronized String lockObjectFetch(int uid){
		String lockKey = String.valueOf(uid);
		if(!lockWithdrawMap.containsKey(lockKey)){
			lockWithdrawMap.put(lockKey, lockKey);
			return lockKey;
		}else{
			return lockWithdrawMap.get(lockKey);
		}
	}
	
	private UserWalletWithdrawApply doWithdrawApplyOper(final int appid,final OAuthType type,final int uid, String pwd,double cash,final String remoteip){
		synchronized(lockObjectFetch(uid)){
			logger.info(String.format("生成提现申请 appid[%s] uid[%s] cash[%s] remoteIp[%s]", appid,uid,cash,remoteip));
			/*String orderid = StructuredIdHelper.generateStructuredIdString(appid, 
					StructuredIdHelper.buildStructuredExtSegmentString(OrderExtSegmentPayMode.Expend.getKey()),
					autoid);*/
			final UserWalletWithdrawApply apply = new UserWalletWithdrawApply();
			this.cashWithdrawOperFromUserWallet(uid, pwd, cash,type.getDescription(),new IWalletNotifyCallback(){
				@Override
				public String notifyCashWithdrawOper(double callback_cash) {
					apply.setUid(uid);
					apply.setAppid(appid);
					apply.setPayment_type(type.getType());
					apply.setCash(callback_cash);
					apply.setRemoteip(remoteip);
					apply.setWithdraw_oper(BusinessEnumType.UWithdrawStatus.Apply.getKey());
					apply.addResponseDTO(WithdrawRemoteResponseDTO.build(BusinessEnumType.UWithdrawStatus.Apply.getKey(), BusinessEnumType.UWithdrawStatus.Apply.getName()));
					UserWalletWithdrawApply tmpApply = userWalletWithdrawApplyService.insert(apply);
					return tmpApply.getId();
				}
			});
			return apply;
		}
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
			this.cashWithdrawRollback2UserWalletWhenVerifyFailed(apply.getUid(), apply.getCash(),current.getName());
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
			cashWithdrawRollback2UserWalletWhenRemoteFailed(apply.getUid(),apply.getCash(),current.getName());
			logger.info(String.format("提现操作-失败 applyid[%s] 返现并解锁钱包状态", applyid));
		}
		apply.addResponseDTO(WithdrawRemoteResponseDTO.build(current.getKey(), current.getName()));
		apply = userWalletWithdrawApplyService.update(apply);
		return apply;
	}
	
	/**
	 * 设置提现密码
	 * 添加和修改都是此方法，采用验证码验证机制
	 * @param uid 审核用户id
	 * @param pwd 新的密码
	 */
	public UserWallet doSetWithdrawPwd(int uid,String pwd){
		UserValidateServiceHelper.validateUser(uid,this.userService);
		UserWallet uwallet = userWalletService.getOrCreateById(uid);
		//if(StringUtils.isNotEmpty(uwallet.getPassword())){
		//	throw new BusinessI18nCodeException(ResponseErrorCode.COMMON_DATA_ALREADYEXIST,new String[]{"提现密码"});
		//}
		uwallet.setPlainpwd(pwd);
		uwallet.setPassword(null);
		uwallet = userWalletService.update(uwallet);
		return uwallet;
	}
	
	/**
	 * 设置提现密码
	 * @param uid 审核用户id
	 * @param pwd 新的密码
	 */
/*	public UserWallet doFirstSetWithdrawPwd(int uid,String pwd){
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
	}*/
	
	private void doWalletLog(int uid,String orderid,
			BusinessEnumType.UWalletTransMode transMode,
			BusinessEnumType.UWalletTransType transType,
			String description,
			double rmoney,double cash,double vcurrency,String memo){
		//System.out.println("~~~~~~~~~~~~~~:"+description);
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
		wlog.setDescription(description);
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
		
		//System.out.println("~~~~~~~~~~"+wlog.getId());
	}
	
	//userThirdpartiesPaymentService
/*	public List<ThirdpartiesPaymentDTO> addThirdpartiesPayment(int uid,ThirdpartiesPaymentType type,ThirdpartiesPaymentDTO paymentDTO){
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
	}*/
	
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

	public UserWalletLogService getUserWalletLogService() {
		return userWalletLogService;
	}

	public UserWalletWithdrawApplyService getUserWalletWithdrawApplyService() {
		return userWalletWithdrawApplyService;
	}
	public UserOAuthFacadeService getUserOAuthFacadeService() {
		return userOAuthFacadeService;
	}
	public ChargingFacadeService getChargingFacadeService() {
		return chargingFacadeService;
	}

	/*public UserThirdpartiesPaymentService getUserThirdpartiesPaymentService() {
		return userThirdpartiesPaymentService;
	}*/
	
}
