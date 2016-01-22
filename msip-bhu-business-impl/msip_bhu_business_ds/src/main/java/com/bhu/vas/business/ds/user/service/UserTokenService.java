package com.bhu.vas.business.ds.user.service;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bhu.vas.api.rpc.user.model.UserToken;
import com.bhu.vas.business.ds.user.dao.UserTokenDao;
import com.bhu.vas.exception.TokenValidateBusinessException;
import com.smartwork.msip.business.token.ITokenService;
import com.smartwork.msip.business.token.UserTokenDTO;
import com.smartwork.msip.business.token.service.TokenServiceHelper;
import com.smartwork.msip.cores.orm.service.EntityService;

@Service
@Transactional("coreTransactionManager")
public class UserTokenService extends EntityService<Integer,UserToken, UserTokenDao> implements ITokenService{
	@Resource
	@Override
	public void setEntityDao(UserTokenDao userTokenDao) {
		super.setEntityDao(userTokenDao);
	}
	
	/**
	 * 
	 * @param uid
	 * @param ifExistThenGenerated 已经存在是否替换新的
	 */
	@Override
	public UserTokenDTO generateUserAccessToken(Integer uid,boolean ifExpiredThenReplaced,boolean ifExistThenReplaced){
		if(uid == null || uid.intValue() ==0) return null;
		UserToken userToken = this.getById(uid);
		if(userToken == null){
			userToken = new UserToken();
			userToken.setId(uid);
			this.insert(userToken);
		}else{
			if(ifExistThenReplaced){
				userToken.doTokenRefresh();
				this.update(userToken);
			}else{
				if(ifExpiredThenReplaced){
					if(!TokenServiceHelper.isNotExpiredAccessToken4User(userToken.getAccess_token())){
						userToken.doTokenRefresh();
						this.update(userToken);
					}
				}
			}
		}
		return userToken.toUserTokenDTO();
	}
	@Override
	public UserTokenDTO validateUserAccessToken(String accessToken){
		try{
			System.out.println("validateUserAccessToken a accessToken:"+accessToken);
			if(StringUtils.isEmpty(accessToken)) throw new TokenValidateBusinessException(Access_Token_Illegal_Format);//return Access_Token_Illegal_Format;
			if(TokenServiceHelper.isNotExpiredAccessToken4User(accessToken)){
				System.out.println("validateUserAccessToken b accessToken:"+accessToken);
				Integer uid = TokenServiceHelper.parserAccessToken4User(accessToken);
				System.out.println("validateUserAccessToken c uid:"+uid);
				if(uid == null || uid.intValue() <=0){
					throw new TokenValidateBusinessException(Access_Token_Illegal_Format);
				}
				System.out.println("validateUserAccessToken d uid:"+uid);
				UserToken userToken = this.getById(uid);
				if(userToken == null) throw new TokenValidateBusinessException(uid,Access_Token_NotExist);
				System.out.println("validateUserAccessToken e userToken:"+userToken.getAccess_token());
				if(accessToken.equals(userToken.getAccess_token())){
					return userToken.toUserTokenDTO();//Access_Token_Matched;
				}
				else throw new TokenValidateBusinessException(uid,Access_Token_NotMatch);
			}else{
				throw new TokenValidateBusinessException(Access_Token_Expired);
			}
		}catch(Exception ex){
			System.out.println("validateUserAccessToken f accessToken:"+accessToken);
			ex.printStackTrace(System.out);
			throw new TokenValidateBusinessException(Access_Token_Illegal_Format);
		}
	}
	@Override
	public UserTokenDTO doRefreshUserAccessToken(String refreshToken){
		try{
			if(StringUtils.isEmpty(refreshToken)) return null;
			Integer uid = TokenServiceHelper.parserRefreshToken4User(refreshToken);
			UserToken userToken = this.getById(uid);
			if(userToken == null) return null;
			if(refreshToken.equals(userToken.getRefresh_token())){
				userToken.doTokenRefresh();
				this.update(userToken);
			}else return null;
			return userToken.toUserTokenDTO();
		}catch(Exception ex){
			//throw new RuntimeException("refreshToken exception");
			return null;
		}
	}
}
