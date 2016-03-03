package com.bhu.vas.rpc.facade;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import com.bhu.vas.api.helper.BusinessEnumType.UWithdrawStatus;
import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.RpcResponseDTOBuilder;
import com.bhu.vas.api.rpc.user.model.User;
import com.bhu.vas.api.rpc.user.model.UserWallet;
import com.bhu.vas.api.rpc.user.model.UserWalletWithdrawApply;
import com.bhu.vas.api.vto.wallet.UserWalletDetailVTO;
import com.bhu.vas.api.vto.wallet.UserWithdrawApplyVTO;
import com.bhu.vas.business.ds.user.facade.UserWalletFacadeService;
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
					vtos.add(apply.toUserWithdrawApplyVTO(
							user!=null?user.getMobileno():StringUtils.EMPTY,
							user!=null?user.getNick():StringUtils.EMPTY));
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
	
	public RpcResponseDTO<Boolean> verifyApplies(int reckoner, long applyid,boolean passed) {
		try{
			userWalletFacadeService.doWithdrawVerify(reckoner, applyid, passed);
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(Boolean.TRUE);
		}catch(BusinessI18nCodeException bex){
			return RpcResponseDTOBuilder.builderErrorRpcResponse(bex.getErrorCode(),bex.getPayload());
		}catch(Exception ex){
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
	}
	
	public RpcResponseDTO<UserWithdrawApplyVTO> withdrawOper(int uid,
			String pwd, double cash,String remoteip) {
		try{
			User user = userWalletFacadeService.getUserService().getById(uid);
			userWalletFacadeService.validateUser(uid);
			UserWalletWithdrawApply apply = userWalletFacadeService.doWithdrawApply(uid, pwd, cash, remoteip);
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(apply.toUserWithdrawApplyVTO(user.getMobileno(), user.getNick()));
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
}
