package com.smartwork.rpc.service.device;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.bhu.vas.api.rpc.devices.dto.DeviceDTO;
import com.bhu.vas.api.rpc.devices.iservice.IDeviceRpcService;

/**
 * 去除掉token存储在db中？只使用redis会比较好？
 * @author Edmond
 *
 */
@Service("deviceRpcService")
public class DeviceRpcService implements IDeviceRpcService {
	private final Logger logger = LoggerFactory.getLogger(DeviceRpcService.class);

	@Override
	public boolean deviceRegister(DeviceDTO dto) {
		System.out.println(dto.toString());
		return false;
	}

	/*//@Resource
	//private UserTokenService userTokenService;
	@Override
	public UserToken generateUserAccessToken(Integer uid,boolean ifExpiredThenReplaced, boolean ifExistThenReplaced) {
		logger.info(String.format("generateUserAccessToken with params: uid[%s] ifExpiredThenReplaced[%s] ifExistThenReplaced[%s]",uid,ifExpiredThenReplaced,ifExistThenReplaced));
		if(uid == null || uid.intValue() ==0) return null;
		boolean up2redis = false;
		UserToken userToken = userTokenService.getById(uid);
		if(userToken == null){
			userToken = new UserToken();
			userToken.setId(uid);
			userTokenService.insert(userToken);
			up2redis = true;
		}else{
			if(ifExistThenReplaced){
				userToken.doTokenRefresh();
				userTokenService.update(userToken);
				up2redis = true;
			}else{
				if(ifExpiredThenReplaced){
					if(!TokenServiceHelper.isNotExpiredAccessToken4User(userToken.getAccess_token())){
						userToken.doTokenRefresh();
						userTokenService.update(userToken);
						up2redis = true;
					}
				}
			}
		}
		if(up2redis){
			IegalTokenHashService.getInstance().userTokenRegister(uid.intValue(), userToken.getAccess_token());
		}
		return userToken;
	}

	@Override
	public UserToken validateUserAccessToken(String accessToken) {
		logger.info(String.format("validateUserAccessToken with params: accessToken[%s]",accessToken));
		try{
			if(StringUtils.isEmpty(accessToken)) throw new TokenValidateBusinessException(UserTokenService.Access_Token_Illegal_Format);//return Access_Token_Illegal_Format;
			if(TokenServiceHelper.isNotExpiredAccessToken4User(accessToken)){
				Integer uid = TokenServiceHelper.parserAccessToken4User(accessToken);
				UserToken userToken = userTokenService.getById(uid);
				if(userToken == null) throw new TokenValidateBusinessException(UserTokenService.Access_Token_NotExist);
				if(accessToken.equals(userToken.getAccess_token())){
					return userToken;//Access_Token_Matched;
				}
				else throw new TokenValidateBusinessException(UserTokenService.Access_Token_NotMatch);
			}else{
				throw new TokenValidateBusinessException(UserTokenService.Access_Token_Expired);
			}
		}catch(Exception ex){
			throw new TokenValidateBusinessException(UserTokenService.Access_Token_Illegal_Format);
		}
	}

	@Override
	public UserToken doRefreshUserAccessToken(String refreshToken) {
		logger.info(String.format("doRefreshUserAccessToken with params: refreshToken[%s]",refreshToken));
		try{
			if(StringUtils.isEmpty(refreshToken)) return null;
			Integer uid = TokenServiceHelper.parserRefreshToken4User(refreshToken);
			UserToken userToken = userTokenService.getById(uid);
			if(userToken == null) return null;
			if(refreshToken.equals(userToken.getRefresh_token())){
				userToken.doTokenRefresh();
				userTokenService.update(userToken);
				IegalTokenHashService.getInstance().userTokenRegister(uid.intValue(), userToken.getAccess_token());
			}
			else return null;
			return userToken;
		}catch(Exception ex){
			return null;
		}
	}

	
	*//**
	 * 用户访问url时的token验证
	 * @param token
	 * @param uidParam
	 * @return
	 *//*
	public boolean validateUserTokenWhenAccess(String token,String uidParam){
		logger.info(String.format("validateUserTokenWhenAccess with params: token[%s] uidParam[%s]",token,uidParam));
		return IegalTokenHashService.getInstance().validateUserToken(token, uidParam);
	}*/
}
