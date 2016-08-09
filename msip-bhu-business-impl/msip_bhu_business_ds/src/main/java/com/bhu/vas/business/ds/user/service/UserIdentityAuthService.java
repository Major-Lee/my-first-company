package com.bhu.vas.business.ds.user.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bhu.vas.api.rpc.user.model.UserIdentityAuth;
import com.bhu.vas.business.ds.user.dao.UserIdentityAuthDao;
import com.smartwork.msip.cores.helper.DateTimeHelper;
import com.smartwork.msip.cores.helper.phone.PhoneHelper;
import com.smartwork.msip.cores.orm.service.EntityService;
import com.smartwork.msip.cores.orm.support.criteria.ModelCriteria;
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
		String accWithCountryCode = PhoneHelper.format(countrycode, acc);
		UserIdentityAuth userId = this.getById(accWithCountryCode);
		if (userId == null) {
			userId = new UserIdentityAuth();
			userId.setId(accWithCountryCode);
			userId.setHdmac(hdmac);
			userId.setCreated_at(DateTimeHelper.formatDate(DateTimeHelper.FormatPattern1));
		    this.insert(userId);
		}else{
			if (userId.getHdmac() != hdmac) {
				userId.setHdmac(hdmac);
				this.update(userId);
			}else{
				throw new BusinessI18nCodeException(ResponseErrorCode.AUTH_CAPTCHA_IDENTITY_EXIST);
			}
		}
	}
	
	public UserIdentityAuth validateIdentity(String hdmac){
		try {
			
			List<UserIdentityAuth> auth = null;
			ModelCriteria mc = new ModelCriteria();
			mc.createCriteria().andSimpleCaulse("1=1").andColumnEqualTo("hdmac", hdmac.trim());
			auth = this.findModelByModelCriteria(mc);
			
			if (auth != null) {
				return auth.get(0);
			}else{
				throw new BusinessI18nCodeException(ResponseErrorCode.AUTH_CAPTCHA_IDENTITY_NOT_EXIST);
			}
		} catch (Exception e) {
			throw new BusinessI18nCodeException(ResponseErrorCode.AUTH_CAPTCHA_IDENTITY_NOT_EXIST);
		}
	}
	
}
