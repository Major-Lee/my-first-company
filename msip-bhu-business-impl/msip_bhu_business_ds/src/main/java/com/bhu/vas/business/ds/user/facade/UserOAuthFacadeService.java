package com.bhu.vas.business.ds.user.facade;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.bhu.vas.api.helper.BusinessEnumType.OAuthType;
import com.bhu.vas.api.rpc.user.dto.UserOAuthStateDTO;
import com.bhu.vas.api.rpc.user.model.UserOAuthState;
import com.bhu.vas.api.rpc.user.model.pk.UserOAuthStatePK;
import com.bhu.vas.business.ds.user.service.UserOAuthStateService;
import com.bhu.vas.business.ds.user.service.UserService;
import com.smartwork.msip.cores.orm.support.criteria.ModelCriteria;
import com.smartwork.msip.exception.BusinessI18nCodeException;
import com.smartwork.msip.jdo.ResponseErrorCode;

@Service
public class UserOAuthFacadeService {
	@Resource
	private UserService userService;
	
	@Resource
	private UserOAuthStateService userOAuthStateService;

	public List<UserOAuthStateDTO> fetchRegisterIdentifies(Integer uid){
		ModelCriteria mc = new ModelCriteria();
		mc.createCriteria().andColumnEqualTo("uid", uid);
		List<UserOAuthState> models = userOAuthStateService.findModelByModelCriteria(mc);
		List<UserOAuthStateDTO> dtos = new ArrayList<UserOAuthStateDTO>();
		if(!models.isEmpty()){
			for(UserOAuthState model : models){
				dtos.add(model.getInnerModel());
			}
		}
		return dtos;
	}
	
	public List<UserOAuthStateDTO> fetchRegisterPaymentIdentifies(Integer uid){
		List<UserOAuthStateDTO> dtos = fetchRegisterIdentifies(uid);
		Iterator<UserOAuthStateDTO> iter = dtos.iterator();
		while(iter.hasNext()){
			UserOAuthStateDTO next = iter.next();
			if(!OAuthType.paymentSupported(next.getIdentify())){
				iter.remove();
			}
		}
		return dtos;
	}
	
	public UserOAuthStateDTO fetchRegisterIndetify(int uid,OAuthType type,boolean validatePayment){
		if(validatePayment){
			if(!type.isPayment()) return null;
		}
		UserOAuthStatePK pk = new UserOAuthStatePK(uid,type.getType());
		UserOAuthState state = userOAuthStateService.getById(pk);
		if(state != null){
			return state.getInnerModel();
		}
		return null;
	}
	
	
	public boolean removeIdentifies(Integer uid,String identify){
		UserOAuthStatePK pk = new UserOAuthStatePK(uid,identify);
		userOAuthStateService.deleteById(pk);
		return true;
	}
	
	/**
	 * uid不可以为空
	 * @param uid
	 * @param identify
	 * @param auid
	 * @param nick
	 * @param avatar
	 * @return
	 */
	public UserOAuthStateDTO createOrUpdateIdentifies(Integer uid,String identify,String auid,String nick,String avatar){
		if(uid == null || uid.intValue() == 0 || StringUtils.isEmpty(identify))
			throw new BusinessI18nCodeException(ResponseErrorCode.COMMON_DATA_PARAM_ERROR,new String[]{"uid|identify"});
		UserOAuthStatePK pk = new UserOAuthStatePK(uid,identify);
		UserOAuthState oauthState = userOAuthStateService.getById(pk);
		UserOAuthStateDTO dto = null;
		if(oauthState != null){
			dto = new UserOAuthStateDTO();
			dto.setIdentify(identify);
			dto.setAuid(auid);
			dto.setNick(nick);
			dto.setAvatar(avatar);
			oauthState.putInnerModel(dto);
			userOAuthStateService.update(oauthState);
		}else{
			oauthState = new UserOAuthState();
			oauthState.setId(pk);
			oauthState.setAuid(auid);
			dto = new UserOAuthStateDTO();
			dto.setIdentify(identify);
			dto.setAuid(auid);
			dto.setNick(nick);
			dto.setAvatar(avatar);
			oauthState.putInnerModel(dto);
			userOAuthStateService.insert(oauthState);
		}
		return dto;
	}

	public UserOAuthStateService getUserOAuthStateService() {
		return userOAuthStateService;
	}

	
/*	public UserSnsStateDTO updateUserSnsInfo(int uid, ApplicationIdentify identify, GeneralOAuth2AccessToken generalOAuth2AccessToken) throws Exception{
		UserSnsStatePK pk = new UserSnsStatePK(uid,identify.toString());
		UserSnsState model = userSnsStateService.getById(pk);
		System.out.println("++++++++++++++ updateUserSnsInfo :" + model);
		if(model != null){
			System.out.println("++++++++++++++ getJUser :" + generalOAuth2AccessToken.getGeneralOAuth2User().getAuid());
			UserSnsFriendDTO snsUserDto = applicationSupport.getJUser(identify, generalOAuth2AccessToken, generalOAuth2AccessToken.getGeneralOAuth2User().getAuid());
			System.out.println("++++++++++++++ getJUser :" + snsUserDto);
			if(snsUserDto != null){
				UserSnsStateDTO dto = new UserSnsStateDTO();
				dto.setAuid(snsUserDto.getAuid());
				dto.setNick(snsUserDto.getNick());
				dto.setAvatar(snsUserDto.getAvatar());
				dto.setIdentify(identify.toString());
				model.putInnerModel(dto);
				userSnsStateService.update(model);
				return dto;
			}
		}
		return null;
	}*/
}
