package com.bhu.vas.rpc.facade;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
//import org.elasticsearch.common.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.bhu.vas.api.dto.UserType;
import com.bhu.vas.api.helper.BusinessEnumType;
import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.RpcResponseDTOBuilder;
import com.bhu.vas.api.rpc.user.dto.UserInnerExchangeDTO;
import com.bhu.vas.api.rpc.user.dto.UserOAuthStateDTO;
import com.bhu.vas.api.rpc.user.model.User;
import com.bhu.vas.api.rpc.user.model.UserOAuthState;
import com.bhu.vas.business.ds.user.facade.UserDeviceFacadeService;
import com.bhu.vas.business.ds.user.facade.UserOAuthFacadeService;
import com.bhu.vas.business.ds.user.facade.UserSignInOrOnFacadeService;
import com.bhu.vas.business.ds.user.service.UserService;
import com.smartwork.msip.cores.helper.StringHelper;
import com.smartwork.msip.cores.orm.support.criteria.ModelCriteria;
import com.smartwork.msip.exception.BusinessI18nCodeException;
import com.smartwork.msip.jdo.ResponseErrorCode;

@Service
public class UserOAuthUnitFacadeService {
	@Resource
	private UserService userService;
	@Resource
	private UserSignInOrOnFacadeService userSignInOrOnFacadeService;
	
	@Resource
	private UserDeviceFacadeService userDeviceFacadeService;
	
	@Resource
	private UserOAuthFacadeService userOAuthFacadeService;
	
	/**
	 * 通过用户id获取其绑定或注册的第三方类型和帐号
	 * @param uid
	 * @return
	 */
	public RpcResponseDTO<List<UserOAuthStateDTO>> fetchRegisterIdentifies(Integer uid,boolean payment){
		try{
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(userOAuthFacadeService.fetchRegisterIdentifies(uid,payment));
		}catch(BusinessI18nCodeException bex){
			return RpcResponseDTOBuilder.builderErrorRpcResponse(bex.getErrorCode(),bex.getPayload());
		}catch(Exception ex){
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
	}
	
	/**
	 * 移除相关第三方的帐号绑定
	 * @param uid
	 * @param identify
	 * @return
	 */
	public RpcResponseDTO<Boolean> removeIdentifies(Integer uid,String identify){
		try{
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(userOAuthFacadeService.removeIdentifies(uid,identify));
		}catch(BusinessI18nCodeException bex){
			return RpcResponseDTOBuilder.builderErrorRpcResponse(bex.getErrorCode(),bex.getPayload());
		}catch(Exception ex){
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
	}
	
	/**
	 * 创建或更新第三方帐号关联信息
	 * 首先判定identify是否被支持
	 * 根据identify 和 auid去查询如果存在则更新，如果不存在则创建新用户，并关联
	 * @param uid
	 * @param identify
	 * @param auid
	 * @param nick
	 * @param avatar
	 * @return
	 */
	public RpcResponseDTO<Map<String, Object>> createOrUpdateIdentifies(Integer uid,String identify,String auid,String nick,String avatar,String device,String regIp,String deviceuuid, String ut){
		UserInnerExchangeDTO userExchange = null;
		try{
			if(StringUtils.isEmpty(identify) || !BusinessEnumType.OAuthType.supported(identify)){
				throw new BusinessI18nCodeException(ResponseErrorCode.AUTH_COMMON_DATA_PARAM_NOTSUPPORTED,new String[]{"identify:".concat(identify)});
			}
			ModelCriteria mc = new ModelCriteria();
			mc.createCriteria().andColumnEqualTo("identify", identify).andColumnEqualTo("auid", auid);
			List<UserOAuthState> models = userOAuthFacadeService.getUserOAuthStateService().findModelByModelCriteria(mc);
			if(uid != null && uid.intValue() >0){
				//第一步确认identify 和 auid 是否不存在
				if(models != null && !models.isEmpty()){
					throw new BusinessI18nCodeException(ResponseErrorCode.USER_OAUTH_DATA_ALREADY_EXIST,new String[]{identify,auid});
				}
				//第二步确认用户已经存在
				User user = userService.getById(uid);
				if(user == null){
					throw new BusinessI18nCodeException(ResponseErrorCode.USER_DATA_NOT_EXIST,new String[]{uid.toString()});
				}
				userExchange = userSignInOrOnFacadeService.commonUserLogin(user,device,regIp,deviceuuid, UserType.getBySName(ut));
			}else{
				/*ModelCriteria mc = new ModelCriteria();
				mc.createCriteria().andColumnEqualTo("identify", identify).andColumnEqualTo("auid", auid);
				List<UserOAuthState> models = userOAuthFacadeService.getUserOAuthStateService().findModelByModelCriteria(mc);*/
				if(models == null || models.isEmpty()){//创建新用户
					String userNick = null;
					if(nick != null && StringUtils.isNotEmpty(nick))
						userNick = nick.concat(StringHelper.AT_STRING_GAP).concat(identify);
					userExchange = userSignInOrOnFacadeService.commonOAuthUserCreate(userNick, device, regIp, deviceuuid, UserType.getBySName(ut));
					//exchangeDTO.setOauths(oauths);
					//不进行异步消息发送通知
					//deliverMessageService.sendUserRegisteredActionMessage(exchangeDTO.getUser().getId(),acc, null, device,regIp);
				}else{
					UserOAuthState userOAuthState = models.get(0);
					User user = userService.getById(userOAuthState.getUid());
					userExchange = userSignInOrOnFacadeService.commonUserLogin(user,device,regIp,deviceuuid, UserType.getBySName(ut));
					//exchangeDTO.setOauths(userOAuthFacadeService.fetchRegisterIdentifies(exchangeDTO.getUser().getId()));
				}
				//UserOAuthStateDTO oauthStateDTO = 
			}
			userOAuthFacadeService.createOrUpdateIdentifies(userExchange.getUser().getId(), identify, auid, nick, avatar);
			userExchange.setOauths(userOAuthFacadeService.fetchRegisterIdentifies(userExchange.getUser().getId(),false));
			Map<String, Object> rpcPayload = RpcResponseDTOBuilder.builderUserRpcPayload(
					userExchange,userDeviceFacadeService.fetchBindDevices(userExchange.getUser().getId()));
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(rpcPayload);
		}catch(BusinessI18nCodeException bex){
			bex.printStackTrace(System.out);
			return RpcResponseDTOBuilder.builderErrorRpcResponse(bex.getErrorCode(),bex.getPayload());
		}catch(Exception ex){
			ex.printStackTrace(System.out);
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
	}
	
	/**
	 * 根据auid填充相关信息
	 * @param identify
	 * @param auid
	 * @param openid
	 * @return
	 */
	public RpcResponseDTO<Boolean> fullfillOpenid(String identify, String auid,String openid){
		try{
			if(StringUtils.isEmpty(identify) || !BusinessEnumType.OAuthType.supported(identify)){
				throw new BusinessI18nCodeException(ResponseErrorCode.AUTH_COMMON_DATA_PARAM_NOTSUPPORTED,new String[]{"identify:".concat(identify)});
			}
			if(StringUtils.isEmpty(auid)){
				throw new BusinessI18nCodeException(ResponseErrorCode.AUTH_COMMON_DATA_PARAM_NOTSUPPORTED,new String[]{"auid:".concat(auid)});
			}
			if(StringUtils.isEmpty(openid)){
				throw new BusinessI18nCodeException(ResponseErrorCode.AUTH_COMMON_DATA_PARAM_NOTSUPPORTED,new String[]{"openid:".concat(openid)});
			}
			ModelCriteria mc = new ModelCriteria();
			mc.createCriteria().andColumnEqualTo("identify", identify).andColumnEqualTo("auid", auid);
			List<UserOAuthState> models = userOAuthFacadeService.getUserOAuthStateService().findModelByModelCriteria(mc);
			
			if(models == null || models.isEmpty()){
				throw new BusinessI18nCodeException(ResponseErrorCode.COMMON_DATA_NOTEXIST,new String[]{identify,auid});
			}
			UserOAuthState userOAuthState = models.get(0);
			UserOAuthStateDTO innerModel = userOAuthState.getInnerModel();
			innerModel.setAuid(auid);
			innerModel.setOpenid(openid);
			userOAuthState.replaceInnerModel(innerModel);
			userOAuthFacadeService.getUserOAuthStateService().update(userOAuthState);
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(Boolean.TRUE);
		}catch(BusinessI18nCodeException bex){
			bex.printStackTrace(System.out);
			return RpcResponseDTOBuilder.builderErrorRpcResponse(bex.getErrorCode(),bex.getPayload());
		}catch(Exception ex){
			ex.printStackTrace(System.out);
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
	}
}
