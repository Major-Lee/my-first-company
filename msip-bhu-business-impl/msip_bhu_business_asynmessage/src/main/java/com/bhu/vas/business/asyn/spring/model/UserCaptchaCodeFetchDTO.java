package com.bhu.vas.business.asyn.spring.model;

import com.bhu.vas.business.asyn.spring.builder.ActionDTO;
import com.bhu.vas.business.asyn.spring.builder.ActionMessageType;

public class UserCaptchaCodeFetchDTO extends ActionDTO{
	private String captcha;
	//ContryCode
	private int countrycode;
	//acc
	private String acc;
	public String getCaptcha() {
		return captcha;
	}
	public void setCaptcha(String captcha) {
		this.captcha = captcha;
	}
	public String getAcc() {
		return acc;
	}
	public void setAcc(String acc) {
		this.acc = acc;
	}
	
	public int getCountrycode() {
		return countrycode;
	}
	public void setCountrycode(int countrycode) {
		this.countrycode = countrycode;
	}
	@Override
	public String getActionType() {
		return ActionMessageType.USERFETCHCAPTCHACODE.getPrefix();
	}
	
}
