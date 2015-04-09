package com.bhu.vas.business.ds.user.service;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bhu.vas.api.rpc.user.model.UserCaptchaCode;
import com.bhu.vas.business.ds.user.dao.UserCaptchaCodeDao;
import com.smartwork.msip.business.runtimeconf.RuntimeConfiguration;
import com.smartwork.msip.cores.helper.DateTimeHelper;
import com.smartwork.msip.cores.orm.service.EntityService;
import com.smartwork.msip.exception.BusinessI18nCodeException;
import com.smartwork.msip.jdo.ResponseErrorCode;

@Service
@Transactional("coreTransactionManager")
public class UserCaptchaCodeService extends EntityService<String,UserCaptchaCode, UserCaptchaCodeDao>{
	@Resource
	@Override
	public void setEntityDao(UserCaptchaCodeDao userCaptchaCodeDao) {
		super.setEntityDao(userCaptchaCodeDao);
	}
	
	public ResponseErrorCode validCaptchaCode(String accWithContryCode,String captcha){
		if(StringUtils.isEmpty(captcha)){
			return ResponseErrorCode.AUTH_CAPTCHA_EMPTY;
		}
		UserCaptchaCode code = this.getById(accWithContryCode);
		if(code == null || !code.getCaptcha().equals(captcha)){
			return ResponseErrorCode.AUTH_CAPTCHA_DATA_NOTEXIST;
		}else{
			if(code.wasExpired()){
				return ResponseErrorCode.AUTH_CAPTCHA_DATA_EXPIRED;
			}else{
				return null;
			}
		}
	}
	
	/*public boolean isValidCaptchaCode(String acc,String captcha){
		CaptchaCode code = this.getById(acc);
		if(code == null || !code.getCaptcha().equals(captcha)){
			return false;
		}else{
			if(code.wasExpired()){
				return false;
			}else{
				return true;
			}
		}
	}*/
	public UserCaptchaCode doGenerateCaptchaCode(String accWithContryCode){
		UserCaptchaCode code = this.getById(accWithContryCode);
		if(code == null){
			code = new UserCaptchaCode();
			code.setId(accWithContryCode);
			code.setTimes(1);
			code = this.insert(code);
		}else{
			if(DateTimeHelper.formatDate(DateTimeHelper.FormatPattern5).equals(code.getDate())){//如果是同一天
				if(code.getTimes()>=RuntimeConfiguration.UserCaptchaCodeLimit){//同一天，次数超出限制
					throw new BusinessI18nCodeException(ResponseErrorCode.AUTH_CAPTCHA_TIMES_NOENOUGH);
				}else{
					if(code.wasExpired()){//过期
						code.setTimes(code.getTimes()+1);
						code = this.update(code);
					}else{//还没过期
						throw new BusinessI18nCodeException(ResponseErrorCode.AUTH_CAPTCHA_PATIENT_WAITING);
					}
				}
			}else{//不是同一天
				code.setTimes(1);
				code = this.update(code);
			}
			
			/*if(code.getTimes()>=RuntimeConfiguration.UserCaptchaCodeLimit 
					&& DateTimeHelper.formatDate(DateTimeHelper.FormatPattern5).equals(code.getDate())){
				throw new BusinessI18nCodeException(ResponseErrorCode.AUTH_CAPTCHA_TIMES_NOENOUGH);
				//throw new RuntimeException(acc+"每天申请的码超过限制！");
			}
			if(code.wasExpired()){
				code.setTimes(code.getTimes()+1);
				code = this.update(code);
			}*/
		}
		return code;
	}
}
