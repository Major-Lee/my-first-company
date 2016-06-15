package com.bhu.vas.rpc.facade;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import com.bhu.vas.api.dto.UserType;
import com.bhu.vas.api.dto.commdity.internal.pay.RequestWithdrawNotifyDTO;
import com.bhu.vas.api.helper.BusinessEnumType.CommdityApplication;
import com.bhu.vas.api.helper.BusinessEnumType.OAuthType;
import com.bhu.vas.api.helper.BusinessEnumType.UWalletTransMode;
import com.bhu.vas.api.helper.BusinessEnumType.UWalletTransType;
import com.bhu.vas.api.helper.BusinessEnumType.UWithdrawStatus;
import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.RpcResponseDTOBuilder;
import com.bhu.vas.api.rpc.charging.dto.WithdrawCostInfo;
import com.bhu.vas.api.rpc.charging.model.DeviceGroupPaymentStatistics;
import com.bhu.vas.api.rpc.statistics.model.FincialStatistics;
import com.bhu.vas.api.rpc.user.dto.ShareDealWalletSummaryProcedureVTO;
import com.bhu.vas.api.rpc.user.dto.UserOAuthStateDTO;
import com.bhu.vas.api.rpc.user.model.User;
import com.bhu.vas.api.rpc.user.model.UserPublishAccount;
import com.bhu.vas.api.rpc.user.model.UserWalletLog;
import com.bhu.vas.api.rpc.user.model.UserWalletWithdrawApply;
import com.bhu.vas.api.vto.statistics.FincialStatisticsVTO;
import com.bhu.vas.api.vto.statistics.RankSingle;
import com.bhu.vas.api.vto.statistics.RankingListVTO;
import com.bhu.vas.api.vto.wallet.UserWalletDetailVTO;
import com.bhu.vas.api.vto.wallet.UserWalletLogVTO;
import com.bhu.vas.api.vto.wallet.UserWithdrawApplyVTO;
import com.bhu.vas.business.bucache.local.serviceimpl.wallet.BusinessWalletCacheService;
import com.bhu.vas.business.ds.charging.service.DeviceGroupPaymentStatisticsService;
import com.bhu.vas.business.ds.user.facade.UserOAuthFacadeService;
import com.bhu.vas.business.ds.user.facade.UserValidateServiceHelper;
import com.bhu.vas.business.ds.user.facade.UserWalletFacadeService;
import com.bhu.vas.business.ds.user.service.UserCaptchaCodeService;
import com.bhu.vas.business.ds.user.service.UserPublishAccountService;
import com.bhu.vas.business.ds.user.service.UserService;
import com.smartwork.msip.business.runtimeconf.BusinessRuntimeConfiguration;
import com.smartwork.msip.business.runtimeconf.RuntimeConfiguration;
import com.smartwork.msip.cores.helper.JsonHelper;
import com.smartwork.msip.cores.helper.phone.PhoneHelper;
import com.smartwork.msip.cores.orm.support.page.CommonPage;
import com.smartwork.msip.cores.orm.support.page.TailPage;
import com.smartwork.msip.exception.BusinessI18nCodeException;
import com.smartwork.msip.jdo.ResponseErrorCode;

@Service
public class UserWalletUnitFacadeService {
	@Resource
	private UserWalletFacadeService userWalletFacadeService;

	@Resource
	private UserCaptchaCodeService userCaptchaCodeService;
	
	@Resource
	private BusinessWalletCacheService businessWalletCacheService;
	
	@Resource
	private DeviceGroupPaymentStatisticsService deviceGroupPaymentStatisticsService;
	
	@Resource
	private UserService userService;
	
	@Resource
	private UserPublishAccountService userPublishAccountService;
	
	@Resource
	private UserOAuthFacadeService userOAuthFacadeService;
	
	public RpcResponseDTO<TailPage<UserWalletLogVTO>> pageUserWalletlogs(
			int uid, 
			String transmode,String transtype, 
			int pageNo, int pageSize) {
		try{
			UWalletTransMode tmode = null;
			if(StringUtils.isNotEmpty(transmode)){
				tmode = UWalletTransMode.fromKey(transmode);
			}
			
			UWalletTransType ttype = null;
			if(StringUtils.isNotEmpty(transtype)){
				ttype = UWalletTransType.fromKey(transtype);
			}
			TailPage<UserWalletLog> pages = userWalletFacadeService.pageUserWalletlogs(uid, tmode, ttype, pageNo, pageSize);
			TailPage<UserWalletLogVTO> result_pages = null;
			List<UserWalletLogVTO> vtos = new ArrayList<>();
			if(!pages.isEmpty()){
				List<Integer> uids = new ArrayList<>();
				for(UserWalletLog log:pages.getItems()){
					uids.add(log.getUid());
				}
				List<User> users = userWalletFacadeService.getUserService().findByIds(uids, true, true);
				int index = 0;
				for(UserWalletLog log:pages.getItems()){
					User user = users.get(index);
					//String payment_type = StringUtils.EMPTY;
					//String description = StringUtils.EMPTY;
					/*ThirdpartiesPaymentType paymentType = null;
					if(BusinessEnumType.UWalletTransMode.CashPayment.getKey().equals(log.getTransmode())
							&& BusinessEnumType.UWalletTransType.Cash2Realmoney.getKey().equals(log.getTranstype()) 
							&& StringUtils.isNotEmpty(log.getOrderid())){
						UserWalletWithdrawApply apply = userWalletFacadeService.getUserWalletWithdrawApplyService().getById(log.getOrderid());
						paymentType = ThirdpartiesPaymentType.fromType(apply!= null ?apply.getPayment_type():StringUtils.EMPTY);
						String payment_type = apply!= null ?apply.getPayment_type():StringUtils.EMPTY;
						if(StringUtils.isNotEmpty(payment_type)){
							if(ThirdpartiesPaymentType.Alipay.equals(payment_type)){
								description = ThirdpartiesPaymentType.Alipay.getDescription();
							}else if(ThirdpartiesPaymentType.Weichat.equals(payment_type)) {
								description = ThirdpartiesPaymentType.Weichat.getDescription();
							}
						}
					}*/
					vtos.add(log.toUserWalletLogVTO(
							user!=null?user.getMobileno():StringUtils.EMPTY,
							user!=null?user.getNick():StringUtils.EMPTY));
					index++;
				}
			}
			result_pages = new CommonPage<UserWalletLogVTO>(pages.getPageNumber(), pages.getPageSize(), pages.getTotalItemsCount(), vtos);
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(result_pages);
		}catch(BusinessI18nCodeException bex){
			return RpcResponseDTOBuilder.builderErrorRpcResponse(bex.getErrorCode(),bex.getPayload());
		}catch(Exception ex){
			ex.printStackTrace(System.out);
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
	}
	/**
	 * 需要判定用户是否是财务用户
	 * @param uid
	 * @param tuid
	 * @param withdraw_status
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	public RpcResponseDTO<TailPage<UserWithdrawApplyVTO>> pageWithdrawApplies(int reckoner, int tuid, String withdraw_status,String payment_type, int pageNo, int pageSize) {
		try{
			User validateUser = UserValidateServiceHelper.validateUser(reckoner,userWalletFacadeService.getUserService());
			if(validateUser.getUtype() != UserType.AgentFinance.getIndex()){
				throw new BusinessI18nCodeException(ResponseErrorCode.USER_TYPE_NOTMATCHED,new String[]{UserType.AgentFinance.getSname()}); 
			}
			UWithdrawStatus status = null;
			if(StringUtils.isNotEmpty(withdraw_status)){
				status = UWithdrawStatus.fromKey(withdraw_status);
			}
			TailPage<UserWalletWithdrawApply> pages = userWalletFacadeService.pageWithdrawApplies(tuid, status,payment_type,pageNo, pageSize);
			TailPage<UserWithdrawApplyVTO> result_pages = null;
			List<UserWithdrawApplyVTO> vtos = new ArrayList<>();
			if(!pages.isEmpty()){
				List<Integer> uids = new ArrayList<>();
				for(UserWalletWithdrawApply apply:pages.getItems()){
					uids.add(apply.getUid());
				}
				List<User> users = userWalletFacadeService.getUserService().findByIds(uids, true, true);
				int index = 0;
				for(UserWalletWithdrawApply apply:pages.getItems()){
					User user = users.get(index);
					WithdrawCostInfo calculateApplyCost = userWalletFacadeService.getChargingFacadeService().calculateWithdrawCost(apply.getUid(),apply.getId(),apply.getCash());
					//ApplyCost calculateApplyCost = userWalletFacadeService.getUserWalletConfigsService().calculateApplyCost(apply.getUid(), apply.getCash());
					//UserWalletConfigs walletConfigs = userWalletFacadeService.getUserWalletConfigsService().userfulWalletConfigs(user.getId());
					vtos.add(apply.toUserWithdrawApplyVTO(
							user!=null?user.getMobileno():StringUtils.EMPTY,
							user!=null?user.getNick():StringUtils.EMPTY,
									calculateApplyCost));
					index++;
				}
			}
			result_pages = new CommonPage<UserWithdrawApplyVTO>(pages.getPageNumber(), pages.getPageSize(), pages.getTotalItemsCount(), vtos);
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(result_pages);
		}catch(BusinessI18nCodeException bex){
			return RpcResponseDTOBuilder.builderErrorRpcResponse(bex.getErrorCode(),bex.getPayload());
		}catch(Exception ex){
			ex.printStackTrace(System.out);
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
	}
	
	/**
	 * 返回指定的applyid的提现状态
	 * @param reckoner
	 * @param applyid
	 * @return
	 */
	public RpcResponseDTO<String> withdrawApplyStatus(int reckoner, String applyid){
		try{
			User validateUser = UserValidateServiceHelper.validateUser(reckoner,userWalletFacadeService.getUserService());
			if(validateUser.getUtype() != UserType.AgentFinance.getIndex()){
				throw new BusinessI18nCodeException(ResponseErrorCode.USER_TYPE_NOTMATCHED,new String[]{UserType.AgentFinance.getSname()}); 
			}
			UserWalletWithdrawApply apply = userWalletFacadeService.getUserWalletWithdrawApplyService().getById(applyid);
			if(apply == null){
				throw new BusinessI18nCodeException(ResponseErrorCode.COMMON_DATA_NOTEXIST,new String[]{applyid});
			}
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(apply.getWithdraw_oper());
		}catch(BusinessI18nCodeException bex){
			return RpcResponseDTOBuilder.builderErrorRpcResponse(bex.getErrorCode(),bex.getPayload());
		}catch(Exception ex){
			ex.printStackTrace(System.out);
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
	}
	
	/*public RpcResponseDTO<Boolean> verifyApplies(int reckoner, String applyid,boolean passed) {
		try{
			User validateUser = UserValidateServiceHelper.validateUser(reckoner,userWalletFacadeService.getUserService());
			if(validateUser.getUtype() != UserType.AgentFinance.getIndex()){
				throw new BusinessI18nCodeException(ResponseErrorCode.USER_TYPE_NOTMATCHED,new String[]{UserType.AgentFinance.getSname()}); 
			}
			UserWalletWithdrawApply withdrawApply = userWalletFacadeService.doWithdrawVerify(reckoner, applyid, passed);
			if(passed){//需要写入uPay数据队列
				BusinessEnumType.UWithdrawStatus current = BusinessEnumType.UWithdrawStatus.WithdrawDoing;
				withdrawApply.addResponseDTO(WithdrawRemoteResponseDTO.build(current.getKey(), current.getName()));
				withdrawApply.setWithdraw_oper(current.getKey());
				User user = UserValidateServiceHelper.validateUser(withdrawApply.getUid(),userWalletFacadeService.getUserService());
				UserWalletConfigs walletConfigs = userWalletFacadeService.getUserWalletConfigsService().userfulWalletConfigs(withdrawApply.getUid());
				UserWithdrawApplyVTO withdrawApplyVTO = withdrawApply.toUserWithdrawApplyVTO(user.getMobileno(), user.getNick(), 
						walletConfigs.getWithdraw_tax_percent(), 
						walletConfigs.getWithdraw_trancost_percent());
				ThirdpartiesPaymentDTO paymentDTO = userWalletFacadeService.fetchThirdpartiesPayment(withdrawApply.getUid(), ThirdpartiesPaymentType.fromType(withdrawApply.getPayment_type()));
				RequestWithdrawNotifyDTO withdrawNotify = RequestWithdrawNotifyDTO.from(withdrawApplyVTO,paymentDTO, System.currentTimeMillis());
				String jsonNotify = JsonHelper.getJSONString(withdrawNotify);
				System.out.println("to Redis prepare:"+jsonNotify);
				{	//保证写入redis后，提现申请设置成为转账中...状态
					//BusinessEnumType.UWithdrawStatus current = BusinessEnumType.UWithdrawStatus.WithdrawDoing;
					CommdityInternalNotifyListService.getInstance().rpushWithdrawAppliesRequestNotify(jsonNotify);
					//withdrawApply.addResponseDTO(WithdrawRemoteResponseDTO.build(current.getKey(), current.getName()));
					//withdrawApply.setWithdraw_oper(current.getKey());
					userWalletFacadeService.getUserWalletWithdrawApplyService().update(withdrawApply);
				}
				System.out.println("to Redis ok:"+jsonNotify);
			}
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(Boolean.TRUE);
		}catch(BusinessI18nCodeException bex){
			return RpcResponseDTOBuilder.builderErrorRpcResponse(bex.getErrorCode(),bex.getPayload());
		}catch(Exception ex){
			ex.printStackTrace(System.out);
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
	}*/
	
	public RpcResponseDTO<RequestWithdrawNotifyDTO> doStartPaymentWithdrawApply(int reckoner,String applyid){
		try{
			User validateUser = UserValidateServiceHelper.validateUser(reckoner,userWalletFacadeService.getUserService());
			if(validateUser.getUtype() != UserType.AgentFinance.getIndex()){
				throw new BusinessI18nCodeException(ResponseErrorCode.USER_TYPE_NOTMATCHED,new String[]{UserType.AgentFinance.getSname()}); 
			}
			UserWalletWithdrawApply withdrawApply = userWalletFacadeService.doStartPaymentWithdrawApply(reckoner, applyid);
			User user = UserValidateServiceHelper.validateUser(withdrawApply.getUid(),userWalletFacadeService.getUserService());
			WithdrawCostInfo calculateApplyCost = userWalletFacadeService.getChargingFacadeService().calculateWithdrawCost(withdrawApply.getUid(),withdrawApply.getId(),withdrawApply.getCash());
			//ApplyCost calculateApplyCost = userWalletFacadeService.getUserWalletConfigsService().calculateApplyCost(withdrawApply.getUid(), withdrawApply.getCash());
			//UserWalletConfigs walletConfigs = userWalletFacadeService.getUserWalletConfigsService().userfulWalletConfigs(withdrawApply.getUid());
			UserWithdrawApplyVTO withdrawApplyVTO = withdrawApply.toUserWithdrawApplyVTO(user.getMobileno(), user.getNick(), 
					calculateApplyCost);
			UserOAuthStateDTO paymentDTO = userWalletFacadeService.getUserOAuthFacadeService().fetchRegisterIndetify(withdrawApply.getUid(),OAuthType.fromType(withdrawApply.getPayment_type()),true);
			//ThirdpartiesPaymentDTO paymentDTO = userWalletFacadeService.fetchThirdpartiesPayment(withdrawApply.getUid(), ThirdpartiesPaymentType.fromType(withdrawApply.getPayment_type()));
			if(paymentDTO == null){
				throw new BusinessI18nCodeException(ResponseErrorCode.USER_WALLET_PAYMENT_WASEMPTY);
			}
			RequestWithdrawNotifyDTO withdrawNotify = RequestWithdrawNotifyDTO.from(withdrawApplyVTO,paymentDTO, System.currentTimeMillis());
			String jsonNotify = JsonHelper.getJSONString(withdrawNotify);
			System.out.println("prepare JsonData:"+jsonNotify);
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(withdrawNotify);
		}catch(BusinessI18nCodeException bex){
			return RpcResponseDTOBuilder.builderErrorRpcResponse(bex.getErrorCode(),bex.getPayload());
		}catch(Exception ex){
			ex.printStackTrace(System.out);
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
	}
	
	public RpcResponseDTO<UserWithdrawApplyVTO> doWithdrawNotifyFromLocal(int reckoner,String applyid,boolean successed){
		try{
			User validateUser = UserValidateServiceHelper.validateUser(reckoner,userWalletFacadeService.getUserService());
			if(validateUser.getUtype() != UserType.AgentFinance.getIndex()){
				throw new BusinessI18nCodeException(ResponseErrorCode.USER_TYPE_NOTMATCHED,new String[]{UserType.AgentFinance.getSname()}); 
			}
			UserWalletWithdrawApply withdrawApply = userWalletFacadeService.doWithdrawNotifyFromLocal(applyid, successed);
			User user = UserValidateServiceHelper.validateUser(withdrawApply.getUid(),userWalletFacadeService.getUserService());
			//UserWalletConfigs walletConfigs = userWalletFacadeService.getUserWalletConfigsService().userfulWalletConfigs(withdrawApply.getUid());
			WithdrawCostInfo calculateApplyCost = userWalletFacadeService.getChargingFacadeService().calculateWithdrawCost(withdrawApply.getUid(),withdrawApply.getId(),withdrawApply.getCash());
			//ApplyCost calculateApplyCost = userWalletFacadeService.getUserWalletConfigsService().calculateApplyCost(withdrawApply.getUid(), withdrawApply.getCash());
			UserWithdrawApplyVTO withdrawApplyVTO = withdrawApply.toUserWithdrawApplyVTO(user.getMobileno(), user.getNick(), 
					calculateApplyCost);
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(withdrawApplyVTO);
		}catch(BusinessI18nCodeException bex){
			return RpcResponseDTOBuilder.builderErrorRpcResponse(bex.getErrorCode(),bex.getPayload());
		}catch(Exception ex){
			ex.printStackTrace(System.out);
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
	}
	
	public RpcResponseDTO<UserWithdrawApplyVTO> withdrawOper(int appid,String payment_type,int uid,
			String pwd, double cash,String remoteip) {
		try{
			//验证appid
			if(!CommdityApplication.supported(appid)){
				return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.VALIDATE_APPID_INVALID,new String[]{String.valueOf(appid)});
			}
			if(!OAuthType.paymentSupported(payment_type)){
				return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.AUTH_COMMON_DATA_PARAM_NOTSUPPORTED,new String[]{String.valueOf(payment_type)});
			}
			User user = UserValidateServiceHelper.validateUser(uid,userWalletFacadeService.getUserService());
			//TODO:验证 用户是否存在对公账户信息，如果存在则只能对公账户提现
			
			//add by Jason 2016-06-07 start
			//根据uid查询当前用户是否存在对公账号
			UserPublishAccount userPublishAccount = userPublishAccountService.getById(uid);
			if(StringUtils.equals(payment_type, "weixin")){
				if(userPublishAccount != null){
					//返回错误码 提示当前用户已绑定对公行号 在app端进行对公账号提现
					return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.USER_WALLET_WITHDRAW_PUBLISHACCOUNT_EXIST);
				}
			}else if(StringUtils.equals(payment_type, "public")){
				if(userPublishAccount == null){
					//查询当前用户是否已绑定微信账号
					UserOAuthStateDTO userOAuthStateDTO = userOAuthFacadeService.fetchRegisterIndetify(uid, OAuthType.fromType("weixin"), true);
					if(userOAuthStateDTO != null){
						//提示当前用户已绑定微信
						return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.USER_WALLET_WITHDRAW_WECHAT_HAS_BEEN_BOUND);
					}
				}
			}
			//add by Jason 2016-06-07 E N D
			
			UserWalletWithdrawApply apply = userWalletFacadeService.doWithdrawApply(appid,OAuthType.fromType(payment_type),uid, pwd, cash, remoteip);
			//UserWalletConfigs walletConfigs = userWalletFacadeService.getUserWalletConfigsService().userfulWalletConfigs(uid);
			WithdrawCostInfo calculateApplyCost = userWalletFacadeService.getChargingFacadeService().calculateWithdrawCost(apply.getUid(),apply.getId(),apply.getCash());
			//ApplyCost calculateApplyCost = userWalletFacadeService.getUserWalletConfigsService().calculateApplyCost(uid, cash);
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(
					apply.toUserWithdrawApplyVTO(
							user.getMobileno(), 
							user.getNick(),
							calculateApplyCost));
		}catch(BusinessI18nCodeException bex){
			bex.printStackTrace(System.out);
			return RpcResponseDTOBuilder.builderErrorRpcResponse(bex.getErrorCode(),bex.getPayload());
		}catch(Exception ex){
			ex.printStackTrace(System.out);
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
	}
	
	public RpcResponseDTO<UserWalletDetailVTO> walletDetail(int uid) {
		try{
			//UserWallet userWallet = userWalletFacadeService.userWallet(uid);
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(userWalletFacadeService.walletDetail(uid));
		}catch(BusinessI18nCodeException bex){
			return RpcResponseDTOBuilder.builderErrorRpcResponse(bex.getErrorCode(),bex.getPayload());
		}catch(Exception ex){
			ex.printStackTrace(System.out);
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
	}
	
/*	public RpcResponseDTO<List<ThirdpartiesPaymentDTO>> fetchUserThirdpartiesPayments(int uid) {
		try{
			List<ThirdpartiesPaymentDTO> payments = userWalletFacadeService.fetchAllThirdpartiesPayment(uid);
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(payments);
		}catch(BusinessI18nCodeException bex){
			return RpcResponseDTOBuilder.builderErrorRpcResponse(bex.getErrorCode(),bex.getPayload());
		}catch(Exception ex){
			ex.printStackTrace(System.out);
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
	}

	public RpcResponseDTO<Boolean> removeUserThirdpartiesPayment(int uid,String payment_type) {
		try{
			if(!ThirdpartiesPaymentType.supported(payment_type)){
				return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.AUTH_COMMON_DATA_PARAM_NOTSUPPORTED);
			}
			userWalletFacadeService.removeThirdpartiesPayment(uid, ThirdpartiesPaymentType.fromType(payment_type));
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(Boolean.TRUE);
		}catch(BusinessI18nCodeException bex){
			return RpcResponseDTOBuilder.builderErrorRpcResponse(bex.getErrorCode(),bex.getPayload());
		}catch(Exception ex){
			ex.printStackTrace(System.out);
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
	}

	public RpcResponseDTO<List<ThirdpartiesPaymentDTO>> createUserThirdpartiesPayment(int uid, String payment_type, String id, String name,String avatar) {
		try{
			if(!ThirdpartiesPaymentType.supported(payment_type)){
				return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.AUTH_COMMON_DATA_PARAM_NOTSUPPORTED,new String[]{"payment_type:".concat(payment_type)});
			}
			ThirdpartiesPaymentType paymenttype = ThirdpartiesPaymentType.fromType(payment_type);
			if(paymenttype == null){
				return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.AUTH_COMMON_DATA_PARAM_NOTSUPPORTED,new String[]{"payment_type:".concat(payment_type)});
			}
			List<ThirdpartiesPaymentDTO> payments = userWalletFacadeService.addThirdpartiesPayment(uid, paymenttype, ThirdpartiesPaymentDTO.build(paymenttype, id, name,avatar));
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(payments);
		}catch(BusinessI18nCodeException bex){
			return RpcResponseDTOBuilder.builderErrorRpcResponse(bex.getErrorCode(),bex.getPayload());
		}catch(Exception ex){
			ex.printStackTrace(System.out);
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
	}*/
	

	
	public RpcResponseDTO<Boolean> withdrawPwdSet(int uid,String captcha, String pwd) {
		try{
			//TODO验证用户是否存在，验证手机号是否存在，此手机号的验证验证码
			User user = UserValidateServiceHelper.validateUser(uid,userWalletFacadeService.getUserService());
			if(StringUtils.isEmpty(user.getMobileno())){
				return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.USER_MOBILE_ATTRIBUTE_NOTEXIST,new String[]{"uid:".concat(String.valueOf(uid))});
			}
			if(!RuntimeConfiguration.SecretInnerTest){
				String accWithCountryCode = PhoneHelper.format(user.getCountrycode(), user.getMobileno());
				if(!BusinessRuntimeConfiguration.isSystemNoneedCaptchaValidAcc(accWithCountryCode)){
					ResponseErrorCode errorCode = userCaptchaCodeService.validCaptchaCode(accWithCountryCode, captcha);
					if(errorCode != null){
						return RpcResponseDTOBuilder.builderErrorRpcResponse(errorCode);
					}
				}
			}
			userWalletFacadeService.doSetWithdrawPwd(uid, pwd);
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(Boolean.TRUE);
		}catch(BusinessI18nCodeException bex){
			return RpcResponseDTOBuilder.builderErrorRpcResponse(bex.getErrorCode(),bex.getPayload());
		}catch(Exception ex){
			ex.printStackTrace(System.out);
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
	}

	
	public RpcResponseDTO<Boolean> directDrawPresent(int uid, String thirdparties_orderid,double cash,String desc) {
		try{
			int ret = userWalletFacadeService.cashToUserWallet(uid, thirdparties_orderid, UWalletTransMode.DrawPresent, 0.00d, cash, desc);
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(ret == 0?Boolean.TRUE:Boolean.FALSE);
		}catch(BusinessI18nCodeException bex){
			return RpcResponseDTOBuilder.builderErrorRpcResponse(bex.getErrorCode(),bex.getPayload());
		}catch(Exception ex){
			ex.printStackTrace(System.out);
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
	}
	
	/*public RpcResponseDTO<Boolean> withdrawPwdUpd(int uid, String pwd, String npwd) {
		try{
			userWalletFacadeService.doChangedWithdrawPwd(uid, pwd, npwd);
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(Boolean.TRUE);
		}catch(BusinessI18nCodeException bex){
			return RpcResponseDTOBuilder.builderErrorRpcResponse(bex.getErrorCode(),bex.getPayload());
		}catch(Exception ex){
			ex.printStackTrace(System.out);
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
	}*/
	
	public RpcResponseDTO<ShareDealWalletSummaryProcedureVTO> walletLogStatistics(
			int uid) {
		try{
			ShareDealWalletSummaryProcedureVTO cacheByUser = businessWalletCacheService.getWalletLogStatisticsDSCacheByUser(uid);
			if(cacheByUser == null){
				cacheByUser = userWalletFacadeService.sharedealSummaryWithProcedure(uid);
				businessWalletCacheService.storeWalletLogStatisticsDSCacheResult(uid, cacheByUser);
			}
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(cacheByUser);
		}catch(BusinessI18nCodeException bex){
			return RpcResponseDTOBuilder.builderErrorRpcResponse(bex.getErrorCode(),bex.getPayload());
		}catch(Exception ex){
			ex.printStackTrace(System.out);
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
	}
	
	public RpcResponseDTO<FincialStatisticsVTO> fincialStatistics(String time) {
		try{
			FincialStatistics fincial = userWalletFacadeService.getFincialStatisticsService().getById(time);
			FincialStatisticsVTO fincialStatisticsVTO=new FincialStatisticsVTO();
			if(fincial == null){
				fincialStatisticsVTO.setId(time);
				return RpcResponseDTOBuilder.builderSuccessRpcResponse(fincialStatisticsVTO);
			}else{
				fincialStatisticsVTO.setId(fincial.getId());
				fincialStatisticsVTO.setaTotal((float)(Math.round(100*(fincial.getCpa()+fincial.getCta())))/100);
				fincialStatisticsVTO.setCpa((float)(Math.round(100*fincial.getCpa()))/100);
				fincialStatisticsVTO.setCpm((float)(Math.round(100*fincial.getCpm()))/100);
				fincialStatisticsVTO.setCpTotal((float)(Math.round(100*(fincial.getCpa()+fincial.getCpm()+fincial.getCpw())))/100);
				fincialStatisticsVTO.setCpw((float)(Math.round(100*fincial.getCpw()))/100);
				fincialStatisticsVTO.setCta((float)(Math.round(100*fincial.getCta()))/100);
				fincialStatisticsVTO.setCtm((float)(Math.round(100*fincial.getCtm()))/100);
				fincialStatisticsVTO.setCtTotal((float)(Math.round(100*(fincial.getCta()+fincial.getCtm()+fincial.getCtw())))/100);
				fincialStatisticsVTO.setCtw((float)(Math.round(100*fincial.getCtw()))/100);
				fincialStatisticsVTO.setmTotal((float)(Math.round(100*(fincial.getCpm()+fincial.getCtm())))/100);
				fincialStatisticsVTO.setwTotal((float)(Math.round(100*(fincial.getCpw()+fincial.getCtw())))/100);
				fincialStatisticsVTO.setTotal((float)(Math.round(100*(fincialStatisticsVTO.getCpTotal()+fincialStatisticsVTO.getCtTotal())))/100);
				fincialStatisticsVTO.setCpow((float)(Math.round(100*(java.lang.Math.abs(fincialStatisticsVTO.getCpTotal()-fincialStatisticsVTO.getCpw()))))/100);
				fincialStatisticsVTO.setCtow((float)(Math.round(100*(java.lang.Math.abs(fincialStatisticsVTO.getCtTotal()-fincialStatisticsVTO.getCtw()))))/100);
				List<FincialStatistics> fincialStatistics=userWalletFacadeService.getFincialStatisticsService().findAll();
				DateFormat df = new SimpleDateFormat("yyyy-MM");
				Date dt1 = df.parse(time);
				if(fincialStatistics!=null&&fincialStatistics.size()>0){
					int owTotal=0;
					int wTotal=0;
					for(FincialStatistics i:fincialStatistics){
						Date dt2 = df.parse(i.getId());
						if(dt1.getTime()>dt2.getTime()){
							if(i.getCtw()>i.getCpw()){
								wTotal+=i.getCtw()-i.getCpw();
							}
							if((i.getCta()-i.getCpa())>(i.getCtm()-i.getCpm())){
								owTotal+=i.getCta()-i.getCpa()+i.getCtm()-i.getCpm();
							}
						}
					}
					fincialStatisticsVTO.setrTotal((float)(Math.round(100*(wTotal+owTotal)))/100);
					fincialStatisticsVTO.setRwTotal((float)(Math.round(100*(wTotal)))/100);
					fincialStatisticsVTO.setRowTotal((float)(Math.round(100*(owTotal)))/100);
					fincialStatisticsVTO.sethTotal((float)(Math.round(100*(java.lang.Math.abs(fincialStatisticsVTO.getrTotal()+fincialStatisticsVTO.getCpTotal()-fincialStatisticsVTO.getCtTotal()))))/100);
					fincialStatisticsVTO.setHwTotal((float)(Math.round(100*(fincialStatisticsVTO.getRwTotal()+fincialStatisticsVTO.getCpTotal()-fincialStatisticsVTO.getCtTotal())))/100);
					fincialStatisticsVTO.setHowTotal((float)(Math.round(100*(java.lang.Math.abs(fincialStatisticsVTO.gethTotal()-fincialStatisticsVTO.getHwTotal()))))/100);
				}
			}
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(fincialStatisticsVTO);
		}catch(BusinessI18nCodeException bex){
			return RpcResponseDTOBuilder.builderErrorRpcResponse(bex.getErrorCode(),bex.getPayload());
		}catch(Exception ex){
			ex.printStackTrace(System.out);
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
	}
	public RpcResponseDTO<RankingListVTO> rankingList(int uid) {
		Date date = new Date();  
        Calendar calendar = Calendar.getInstance();  
        calendar.setTime(date);  
        calendar.add(Calendar.DAY_OF_MONTH, -1);  
        date = calendar.getTime();  
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");  
        String time = "%"+sdf.format(date); 
        time="%2016-06-02";
		try{
			RankingListVTO rankingListVTO=new RankingListVTO();
			List<RankSingle> rankList=new ArrayList<RankSingle>();
			List<DeviceGroupPaymentStatistics> paymentStatistics= deviceGroupPaymentStatisticsService.getRankingList(time);
			if(paymentStatistics != null){
				rankingListVTO.setRankNum(0);
				rankingListVTO.setUserIncome("0");
				for(int i=0;i<paymentStatistics.size();i++){
					RankSingle rankSingle=new RankSingle();
					DeviceGroupPaymentStatistics deviceGroupPaymentStatistics=paymentStatistics.get(i);
					User user=userService.getById(deviceGroupPaymentStatistics.getUid());
					rankSingle.setRankNum(i+1);
					rankSingle.setUserIncome(deviceGroupPaymentStatistics.getTotal_incoming_amount());
					rankSingle.setUserName(user.getNick());
					rankSingle.setAvatar(user.getAvatar());
					if(i+1<=100){
						if(uid==deviceGroupPaymentStatistics.getUid()){
							rankingListVTO.setRankNum(i+1);
						}
						rankList.add(rankSingle);
					}else{
						if(uid==deviceGroupPaymentStatistics.getUid()){
							rankingListVTO.setUserIncome(deviceGroupPaymentStatistics.getTotal_incoming_amount());
						}
					}
				}
				
			}
			rankingListVTO.setRankingList(rankList);
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(rankingListVTO);
		}catch(BusinessI18nCodeException bex){
			return RpcResponseDTOBuilder.builderErrorRpcResponse(bex.getErrorCode(),bex.getPayload());
		}catch(Exception ex){
			ex.printStackTrace(System.out);
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
	}
}
