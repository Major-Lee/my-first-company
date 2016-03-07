package com.bhu.vas.rpc.facade;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import com.bhu.vas.api.dto.commdity.internal.pay.RequestWithdrawNotifyDTO;
import com.bhu.vas.api.helper.BusinessEnumType;
import com.bhu.vas.api.helper.BusinessEnumType.CommdityApplication;
import com.bhu.vas.api.helper.BusinessEnumType.ThirdpartiesPaymentMode;
import com.bhu.vas.api.helper.BusinessEnumType.UWithdrawStatus;
import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.RpcResponseDTOBuilder;
import com.bhu.vas.api.rpc.user.dto.ThirdpartiesPaymentDTO;
import com.bhu.vas.api.rpc.user.dto.WithdrawRemoteResponseDTO;
import com.bhu.vas.api.rpc.user.model.User;
import com.bhu.vas.api.rpc.user.model.UserWallet;
import com.bhu.vas.api.rpc.user.model.UserWalletConfigs;
import com.bhu.vas.api.rpc.user.model.UserWalletWithdrawApply;
import com.bhu.vas.api.vto.wallet.UserWalletDetailVTO;
import com.bhu.vas.api.vto.wallet.UserWithdrawApplyVTO;
import com.bhu.vas.business.bucache.redis.serviceimpl.commdity.CommdityInternalNotifyListService;
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

	public RpcResponseDTO<TailPage<UserWithdrawApplyVTO>> pagesWithdrawApplies(
			int uid, int tuid, String withdraw_status, int pageNo, int pageSize) {
		try{
			UWithdrawStatus status = null;
			if(StringUtils.isNotEmpty(withdraw_status)){
				status = UWithdrawStatus.fromKey(withdraw_status);
			}
			TailPage<UserWalletWithdrawApply> pageApplies = userWalletFacadeService.pageWithdrawApplies(uid, status, pageNo, pageSize);
			TailPage<UserWithdrawApplyVTO> result_pages = null;
			List<UserWithdrawApplyVTO> vtos = new ArrayList<>();
			if(!pageApplies.isEmpty()){
				List<Integer> uids = new ArrayList<>();
				for(UserWalletWithdrawApply apply:pageApplies.getItems()){
					uids.add(apply.getUid());
				}
				List<User> users = userWalletFacadeService.getUserService().findByIds(uids, true, true);
				int index = 0;
				for(UserWalletWithdrawApply apply:pageApplies.getItems()){
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
			result_pages = new CommonPage<UserWithdrawApplyVTO>(pageApplies.getPageNumber(), pageApplies.getPageSize(), pageApplies.getTotalItemsCount(), vtos);
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(result_pages);
		}catch(BusinessI18nCodeException bex){
			return RpcResponseDTOBuilder.builderErrorRpcResponse(bex.getErrorCode(),bex.getPayload());
		}catch(Exception ex){
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
	}
	
	public RpcResponseDTO<Boolean> verifyApplies(int reckoner, String applyid,boolean passed) {
		try{
			UserWalletWithdrawApply withdrawApply = userWalletFacadeService.doWithdrawVerify(reckoner, applyid, passed);
			if(passed){//需要写入uPay数据队列
				BusinessEnumType.UWithdrawStatus current = BusinessEnumType.UWithdrawStatus.WithdrawDoing;
				withdrawApply.addResponseDTO(WithdrawRemoteResponseDTO.build(current.getKey(), current.getName()));
				withdrawApply.setWithdraw_oper(current.getKey());
				//User user = userWalletFacadeService.getUserService().getById(withdrawApply.getUid());
				User user =userWalletFacadeService.validateUser(withdrawApply.getUid());
				UserWalletConfigs walletConfigs = userWalletFacadeService.getUserWalletConfigsService().userfulWalletConfigs(withdrawApply.getUid());
				UserWithdrawApplyVTO withdrawApplyVTO = withdrawApply.toUserWithdrawApplyVTO(user.getMobileno(), user.getNick(), 
						walletConfigs.getWithdraw_tax_percent(), 
						walletConfigs.getWithdraw_trancost_percent());
				ThirdpartiesPaymentDTO paymentDTO = userWalletFacadeService.fetchThirdpartiesPayment(withdrawApply.getUid(), ThirdpartiesPaymentMode.fromMode(withdrawApply.getPaymode()));
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
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
	}
	
	
	public RpcResponseDTO<UserWithdrawApplyVTO> withdrawOper(int appid,String paymode,int uid,
			String pwd, double cash,String remoteip) {
		try{
			//验证appid
			if(!CommdityApplication.supported(appid)){
				return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.VALIDATE_APPID_INVALID,new String[]{String.valueOf(appid)});
			}
			if(!ThirdpartiesPaymentMode.supported(paymode)){
				return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.AUTH_COMMON_DATA_PARAM_NOTSUPPORTED);
			}
			
			if(StringUtils.isEmpty(paymode)){
				//如果没有指定则去除用户定义的第一个
				ThirdpartiesPaymentDTO payment = userWalletFacadeService.fetchFirstThirdpartiesPayment(uid);
				if(payment != null){
					paymode = payment.getMode();
				}
			}
			//User user = userWalletFacadeService.getUserService().getById(uid);
			User user = userWalletFacadeService.validateUser(uid);
			UserWalletWithdrawApply apply = userWalletFacadeService.doWithdrawApply(appid,ThirdpartiesPaymentMode.fromMode(paymode),uid, pwd, cash, remoteip);
			UserWalletConfigs walletConfigs = userWalletFacadeService.getUserWalletConfigsService().userfulWalletConfigs(uid);
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(
					apply.toUserWithdrawApplyVTO(
							user.getMobileno(), 
							user.getNick(),
							walletConfigs.getWithdraw_tax_percent(),
							walletConfigs.getWithdraw_trancost_percent()));
		}catch(BusinessI18nCodeException bex){
			return RpcResponseDTOBuilder.builderErrorRpcResponse(bex.getErrorCode(),bex.getPayload());
		}catch(Exception ex){
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
	}
	
	public RpcResponseDTO<UserWalletDetailVTO> walletDetail(int uid) {
		try{
			UserWallet userWallet = userWalletFacadeService.userWallet(uid);
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(userWallet.toUserWalletDetailVTO());
		}catch(BusinessI18nCodeException bex){
			return RpcResponseDTOBuilder.builderErrorRpcResponse(bex.getErrorCode(),bex.getPayload());
		}catch(Exception ex){
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
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
	}

	public RpcResponseDTO<Boolean> removeUserThirdpartiesPayment(int uid,String paymode) {
		try{
			userWalletFacadeService.removeThirdpartiesPayment(uid, ThirdpartiesPaymentMode.fromMode(paymode));
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(Boolean.TRUE);
		}catch(BusinessI18nCodeException bex){
			return RpcResponseDTOBuilder.builderErrorRpcResponse(bex.getErrorCode(),bex.getPayload());
		}catch(Exception ex){
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
	}

	public RpcResponseDTO<List<ThirdpartiesPaymentDTO>> createUserThirdpartiesPayment(int uid, String paymode, String id, String name) {
		try{
			ThirdpartiesPaymentMode paymentmode = ThirdpartiesPaymentMode.fromMode(paymode);
			List<ThirdpartiesPaymentDTO> payments = userWalletFacadeService.addThirdpartiesPayment(uid, paymentmode, ThirdpartiesPaymentDTO.build(paymentmode, id, name));
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(payments);
		}catch(BusinessI18nCodeException bex){
			return RpcResponseDTOBuilder.builderErrorRpcResponse(bex.getErrorCode(),bex.getPayload());
		}catch(Exception ex){
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
	}
}
