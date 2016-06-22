package com.bhu.vas.rpc.facade;


import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.RpcResponseDTOBuilder;
import com.bhu.vas.api.rpc.user.model.UserPublishAccount;
import com.bhu.vas.api.vto.publishAccount.UserPublishAccountDetailVTO;
import com.bhu.vas.business.bucache.redis.serviceimpl.token.IegalTokenHashService;
import com.bhu.vas.business.bucache.redis.serviceimpl.unique.facade.UniqueFacadeService;
import com.bhu.vas.business.ds.user.facade.UserOAuthFacadeService;
import com.bhu.vas.business.ds.user.facade.UserPublishAccountFacadeService;
import com.bhu.vas.business.ds.user.facade.UserSignInOrOnFacadeService;
import com.bhu.vas.business.ds.user.service.UserCaptchaCodeService;
import com.bhu.vas.business.ds.user.service.UserService;
import com.bhu.vas.business.ds.user.service.UserTokenService;
import com.bhu.vas.exception.TokenValidateBusinessException;
import com.smartwork.msip.exception.BusinessI18nCodeException;
import com.smartwork.msip.jdo.ResponseErrorCode;

@Service
public class UserPublishAccountUnitFacadeService {
	@Resource
	private UserService userService;
	@Resource
	private UserTokenService userTokenService;
	@Resource
	private UserSignInOrOnFacadeService userSignInOrOnFacadeService;
	@Resource
	private UserOAuthFacadeService userOAuthFacadeService;
	@Resource
	private UserCaptchaCodeService userCaptchaCodeService;
	@Resource
	private UserPublishAccountFacadeService userPublishAccountFacadeService;
	/**
	 * 需要兼容uidParam为空的情况
	 * @param uidParam
	 * @param token
	 * @param d_uuid
	 * @return
	 */
	public RpcResponseDTO<Boolean> tokenValidate(String uidParam, String token,String d_uuid) {
		//if(StringUtils.isEmpty(uidParam)) 
		//	return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.AUTH_UID_EMPTY);
		try{
			int uid = -1;
			if(StringUtils.isNotEmpty(uidParam)){
				uid = Integer.parseInt(uidParam);
			}
			boolean validate = IegalTokenHashService.getInstance().validateUserToken(token,uid);
			/*//token 验证正确，需要进行uuid比对
			if(!validate && uid>0 && StringUtils.isNotEmpty(d_uuid)){//验证不通过，则需要通过uuid进行比对，看是否uuid变更
				User user  = userService.getById(uid);
				if(user != null && StringUtils.isNotEmpty(user.getLastlogindevice_uuid()) && !user.getLastlogindevice_uuid().equals(d_uuid)){
					return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.AUTH_UUID_VALID_SELFOTHER_HANDSET_CHANGED);
				}
			}*/
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(validate?Boolean.TRUE:Boolean.FALSE);
		}catch(TokenValidateBusinessException bex){
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.AUTH_TOKEN_INVALID);
		}catch(NumberFormatException fex){
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.AUTH_TOKEN_INVALID);
		}
	}
	/**
	 * 检查手机号是否注册过
	 * @param countrycode
	 * @param acc
	 * @return 
	 * 		true 系统不存在此手机号  
	 * 		false系统存在此手机号 ，并带有错误码
	 */
	public RpcResponseDTO<Boolean> checkAcc(int countrycode, String acc){
		if(UniqueFacadeService.checkMobilenoExist(countrycode,acc)){
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.AUTH_MOBILENO_DATA_EXIST,Boolean.FALSE);
		}else{
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(Boolean.TRUE);
		}
	}
	
	public RpcResponseDTO<UserPublishAccountDetailVTO> addUserPublishAccount(int uid, String companyName, String business_license_number,
			String business_license_address, String address, String mobile,
			String business_license_pic, String account_name,
			String publish_account_number, String opening_bank, String city,
			String bank_branch_name){
		try {
			UserPublishAccount userPublishAccount = userPublishAccountFacadeService.insertUserPublishAccount(uid, companyName, business_license_number, business_license_address, address, mobile, business_license_pic, account_name, publish_account_number, opening_bank, city, bank_branch_name);
			System.out.println("#######userPublishAccount【"+userPublishAccount+"】#######");
			if(userPublishAccount == null){
				throw new BusinessI18nCodeException(ResponseErrorCode.USER_WALLET_WITHDRAW_PUBLISHACCOUNT_EXIST,new String[]{"绑定对公账号",String.valueOf(uid)});
			}
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(userPublishAccount.toUserPulishAccountDetailVTO());
		}catch(BusinessI18nCodeException bex){
			bex.printStackTrace(System.out);
			return RpcResponseDTOBuilder.builderErrorRpcResponse(bex.getErrorCode(),bex.getPayload());
		}catch(Exception ex){
			ex.printStackTrace(System.out);
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
	}
	
	public RpcResponseDTO<UserPublishAccountDetailVTO> queryUserPublishAccount(int uid){
		try{
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(userPublishAccountFacadeService.publicAccountDetail(uid));
		}catch(BusinessI18nCodeException bex){
			return RpcResponseDTOBuilder.builderErrorRpcResponse(bex.getErrorCode(),bex.getPayload());
		}catch(Exception ex){
			ex.printStackTrace(System.out);
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
	}
}
