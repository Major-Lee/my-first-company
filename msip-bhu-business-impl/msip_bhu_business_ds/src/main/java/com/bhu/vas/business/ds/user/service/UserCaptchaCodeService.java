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
	
	/**
	 * 策略：
	 * 	1、如果不存在记录则创建一条新纪录
	 *  2、如果存在记录
	 *  	a、对于当天的记录
	 *  		需要首先判断验证码生成次数达到上限，如果没有则看上次生成的验证码是否过期，如果没有过期则返回错误码，让客户端继续等待，如果过期则重新生成一个
	 *      b、如果不是当天记录 则重新生成一个
	 *      
	 * @param accWithContryCode
	 * @param foreceGen 如果为true，则不考虑约束条件 下面一个参数失效
	 * @param igonreExpired 如果为true，则不考虑是否过期,或是否可以获取next
	 * @return
	 */
	public UserCaptchaCode doGenerateCaptchaCode(String accWithContryCode,boolean foreceGen,boolean igonreExpired){
		UserCaptchaCode code = this.getById(accWithContryCode);
		if(code == null){
			code = new UserCaptchaCode();
			code.setId(accWithContryCode);
			code.setTimes(1);
			code = this.insert(code);
			return code;
		}else{
			if(foreceGen){
				if(DateTimeHelper.formatDate(DateTimeHelper.FormatPattern5).equals(code.getDate())){//如果是同一天
					code.setTimes(code.getTimes()+1);
				}else{
					code.setTimes(1);
				}
				code = this.update(code);
				return code;
			}else{
				if(DateTimeHelper.formatDate(DateTimeHelper.FormatPattern5).equals(code.getDate())){//如果是同一天
					if(code.getTimes()>=RuntimeConfiguration.UserCaptchaCodeLimit){//同一天，次数超出限制
						throw new BusinessI18nCodeException(ResponseErrorCode.AUTH_CAPTCHA_TIMES_NOENOUGH);
					}else{
						/*if(igonreExpired){
							code.setTimes(code.getTimes()+1);
							code = this.update(code);
							return code;
						}else{
							
						}*/
						if(igonreExpired || code.canFetchNext()/* || code.wasExpired()*/){//过期
							code.setTimes(code.getTimes()+1);
							code = this.update(code);
							return code;
						}else{//还没过期
							throw new BusinessI18nCodeException(ResponseErrorCode.AUTH_CAPTCHA_PATIENT_WAITING);
						}
					}
				}else{
					code.setTimes(1);
					code = this.update(code);
					return code;
				}
			}
		}
	}
	public UserCaptchaCode doGenerateCaptchaCode(String accWithContryCode){
		return this.doGenerateCaptchaCode(accWithContryCode, false, false);
	}
	
}
