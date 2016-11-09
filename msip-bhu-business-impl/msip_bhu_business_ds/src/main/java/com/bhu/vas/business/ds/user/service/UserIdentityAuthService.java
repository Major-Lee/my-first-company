package com.bhu.vas.business.ds.user.service;


import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bhu.vas.api.rpc.user.model.UserIdentityAuth;
import com.bhu.vas.business.ds.user.dao.UserIdentityAuthDao;
import com.smartwork.msip.business.runtimeconf.BusinessRuntimeConfiguration;
import com.smartwork.msip.cores.helper.DateTimeHelper;
import com.smartwork.msip.cores.helper.phone.PhoneHelper;
import com.smartwork.msip.cores.orm.service.EntityService;
import com.smartwork.msip.exception.BusinessI18nCodeException;
import com.smartwork.msip.jdo.ResponseErrorCode;

@Service
@Transactional("coreTransactionManager")
public class UserIdentityAuthService extends EntityService<String,UserIdentityAuth,UserIdentityAuthDao>{
	
	@Resource
	@Override
	public void setEntityDao(UserIdentityAuthDao userIdentityAuthDao) {
		super.setEntityDao(userIdentityAuthDao);
	}
	
	/**
	 * portal身份认证
	 * @param countrycode 区号
	 * @param acc 手机号
	 * @param hdmac 终端mac
	 */
	public void generateIdentityAuth(int countrycode ,String acc,String hdmac){	
		
		if(!isDirtyMac(hdmac)){
			String accWithCountryCode = PhoneHelper.format(countrycode, acc);
			
			UserIdentityAuth auth = this.getById(hdmac);
			
			if (auth == null) {
				auth = new UserIdentityAuth();
				auth.setId(hdmac);
				auth.setMobileno(accWithCountryCode);
				auth.setCreated_at(DateTimeHelper.formatDate(DateTimeHelper.FormatPattern1));
			    this.insert(auth);
			}else{
				if (auth.getMobileno() != accWithCountryCode) {
					auth.setMobileno(accWithCountryCode);
					this.update(auth);
				}else{
					throw new BusinessI18nCodeException(ResponseErrorCode.AUTH_CAPTCHA_IDENTITY_EXIST);
				}
			}
		}
	}
	
	public UserIdentityAuth validateIdentity(String hdmac){
		try {
			
			UserIdentityAuth auth = this.getById(hdmac);
			
			if (auth != null) {
				String acc = auth.getMobileno().substring(3);
				auth.setInWhiteList(BusinessRuntimeConfiguration.isCommdityWhileList(acc));
				return auth;
			}else{
				throw new BusinessI18nCodeException(ResponseErrorCode.AUTH_CAPTCHA_IDENTITY_NOT_EXIST);
			}
		} catch (Exception e) {
			throw new BusinessI18nCodeException(ResponseErrorCode.AUTH_CAPTCHA_IDENTITY_NOT_EXIST);
		}
	}
	
	public static  boolean isDirtyMac(String hdmac){
		for(String dirtymac : UserIdentityAuth.dirtyMacs){
			if(hdmac.equals(dirtymac)){
				return true;
			}
		}
		return false;
	}
}
