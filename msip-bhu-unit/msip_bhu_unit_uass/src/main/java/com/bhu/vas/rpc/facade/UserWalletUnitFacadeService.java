package com.bhu.vas.rpc.facade;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import com.bhu.vas.api.dto.UserType;
import com.bhu.vas.api.dto.commdity.internal.pay.RequestWithdrawNotifyDTO;
import com.bhu.vas.api.helper.BusinessEnumType.CommdityApplication;
import com.bhu.vas.api.helper.BusinessEnumType.ThirdpartiesPaymentType;
import com.bhu.vas.api.helper.BusinessEnumType.UWalletTransMode;
import com.bhu.vas.api.helper.BusinessEnumType.UWalletTransType;
import com.bhu.vas.api.helper.BusinessEnumType.UWithdrawStatus;
import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.RpcResponseDTOBuilder;
import com.bhu.vas.api.rpc.user.dto.ThirdpartiesPaymentDTO;
import com.bhu.vas.api.rpc.user.model.User;
import com.bhu.vas.api.rpc.user.model.UserWalletConfigs;
import com.bhu.vas.api.rpc.user.model.UserWalletLog;
import com.bhu.vas.api.rpc.user.model.UserWalletWithdrawApply;
import com.bhu.vas.api.vto.wallet.UserWalletDetailVTO;
import com.bhu.vas.api.vto.wallet.UserWalletLogVTO;
import com.bhu.vas.api.vto.wallet.UserWithdrawApplyVTO;
import com.bhu.vas.business.ds.user.facade.UserValidateServiceHelper;
import com.bhu.vas.business.ds.user.facade.UserWalletFacadeService;
import com.smartwork.msip.cores.helper.JsonHelper;
import com.smartwork.msip.cores.orm.support.page.CommonPage;
import com.smartwork.msip.cores.orm.support.page.TailPage;
import com.smartwork.msip.exception.BusinessI18nCodeException;
import com.smartwork.msip.jdo.ResponseErrorCode;

@Service
public class UserWalletUnitFacadeService {
	@Resource
	private UserWalletFacadeService userWalletFacadeService;

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
	public RpcResponseDTO<TailPage<UserWithdrawApplyVTO>> pageWithdrawApplies(int reckoner, int tuid, String withdraw_status, int pageNo, int pageSize) {
		try{
			User validateUser = UserValidateServiceHelper.validateUser(reckoner,userWalletFacadeService.getUserService());
			if(validateUser.getUtype() != UserType.AgentFinance.getIndex()){
				throw new BusinessI18nCodeException(ResponseErrorCode.USER_TYPE_NOTMATCHED,new String[]{UserType.AgentFinance.getSname()}); 
			}
			UWithdrawStatus status = null;
			if(StringUtils.isNotEmpty(withdraw_status)){
				status = UWithdrawStatus.fromKey(withdraw_status);
			}
			TailPage<UserWalletWithdrawApply> pages = userWalletFacadeService.pageWithdrawApplies(tuid, status, pageNo, pageSize);
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
					UserWalletConfigs walletConfigs = userWalletFacadeService.getUserWalletConfigsService().userfulWalletConfigs(user.getId());
					vtos.add(apply.toUserWithdrawApplyVTO(
							user!=null?user.getMobileno():StringUtils.EMPTY,
							user!=null?user.getNick():StringUtils.EMPTY,
									walletConfigs.getWithdraw_tax_percent(),
									walletConfigs.getWithdraw_trancost_percent()));
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
			UserWalletConfigs walletConfigs = userWalletFacadeService.getUserWalletConfigsService().userfulWalletConfigs(withdrawApply.getUid());
			UserWithdrawApplyVTO withdrawApplyVTO = withdrawApply.toUserWithdrawApplyVTO(user.getMobileno(), user.getNick(), 
					walletConfigs.getWithdraw_tax_percent(), 
					walletConfigs.getWithdraw_trancost_percent());
			ThirdpartiesPaymentDTO paymentDTO = userWalletFacadeService.fetchThirdpartiesPayment(withdrawApply.getUid(), ThirdpartiesPaymentType.fromType(withdrawApply.getPayment_type()));
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
			UserWalletConfigs walletConfigs = userWalletFacadeService.getUserWalletConfigsService().userfulWalletConfigs(withdrawApply.getUid());
			UserWithdrawApplyVTO withdrawApplyVTO = withdrawApply.toUserWithdrawApplyVTO(user.getMobileno(), user.getNick(), 
					walletConfigs.getWithdraw_tax_percent(), 
					walletConfigs.getWithdraw_trancost_percent());
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
			
			//payment_type为空的情况下直接取用户绑定过的第一个账户
			if(StringUtils.isEmpty(payment_type)){
				//如果没有指定则去除用户定义的第一个
				ThirdpartiesPaymentDTO payment = userWalletFacadeService.fetchFirstThirdpartiesPayment(uid);
				if(payment != null){
					payment_type = payment.getType();
				}
			}else{
				//不为空的情况下需要判定是否支持此参数payment_type
				if(!ThirdpartiesPaymentType.supported(payment_type)){
					return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.AUTH_COMMON_DATA_PARAM_NOTSUPPORTED);
				}
			}
			//User user = userWalletFacadeService.getUserService().getById(uid);
			//User user = userWalletFacadeService.validateUser(uid);
			User user = UserValidateServiceHelper.validateUser(uid,userWalletFacadeService.getUserService());
			UserWalletWithdrawApply apply = userWalletFacadeService.doWithdrawApply(appid,ThirdpartiesPaymentType.fromType(payment_type),uid, pwd, cash, remoteip);
			UserWalletConfigs walletConfigs = userWalletFacadeService.getUserWalletConfigsService().userfulWalletConfigs(uid);
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(
					apply.toUserWithdrawApplyVTO(
							user.getMobileno(), 
							user.getNick(),
							walletConfigs.getWithdraw_tax_percent(),
							walletConfigs.getWithdraw_trancost_percent()));
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
	
	public RpcResponseDTO<List<ThirdpartiesPaymentDTO>> fetchUserThirdpartiesPayments(int uid) {
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

	public RpcResponseDTO<List<ThirdpartiesPaymentDTO>> createUserThirdpartiesPayment(int uid, String payment_type, String id, String name) {
		try{
			if(!ThirdpartiesPaymentType.supported(payment_type)){
				return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.AUTH_COMMON_DATA_PARAM_NOTSUPPORTED);
			}
			ThirdpartiesPaymentType paymenttype = ThirdpartiesPaymentType.fromType(payment_type);
			List<ThirdpartiesPaymentDTO> payments = userWalletFacadeService.addThirdpartiesPayment(uid, paymenttype, ThirdpartiesPaymentDTO.build(paymenttype, id, name));
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(payments);
		}catch(BusinessI18nCodeException bex){
			return RpcResponseDTOBuilder.builderErrorRpcResponse(bex.getErrorCode(),bex.getPayload());
		}catch(Exception ex){
			ex.printStackTrace(System.out);
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
	}
	
	public RpcResponseDTO<Boolean> withdrawPwdSet(int uid, String pwd) {
		try{
			userWalletFacadeService.doFirstSetWithdrawPwd(uid, pwd);
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(Boolean.TRUE);
		}catch(BusinessI18nCodeException bex){
			return RpcResponseDTOBuilder.builderErrorRpcResponse(bex.getErrorCode(),bex.getPayload());
		}catch(Exception ex){
			ex.printStackTrace(System.out);
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
	}

	public RpcResponseDTO<Boolean> withdrawPwdUpd(int uid, String pwd, String npwd) {
		try{
			userWalletFacadeService.doChangedWithdrawPwd(uid, pwd, npwd);
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(Boolean.TRUE);
		}catch(BusinessI18nCodeException bex){
			return RpcResponseDTOBuilder.builderErrorRpcResponse(bex.getErrorCode(),bex.getPayload());
		}catch(Exception ex){
			ex.printStackTrace(System.out);
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
	}
}
