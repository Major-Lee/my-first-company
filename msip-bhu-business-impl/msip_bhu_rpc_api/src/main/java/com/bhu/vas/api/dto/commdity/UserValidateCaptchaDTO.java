package com.bhu.vas.api.dto.commdity;

@SuppressWarnings("serial")
public class UserValidateCaptchaDTO implements java.io.Serializable{
	//验证码是否正确
	private boolean validate_captcha;
	//是否可以上网
	private boolean isAuthorize;
	
	public boolean isValidate_captcha() {
		return validate_captcha;
	}
	public void setValidate_captcha(boolean validate_captcha) {
		this.validate_captcha = validate_captcha;
	}
	public boolean isAuthorize() {
		return isAuthorize;
	}
	public void setAuthorize(boolean isAuthorize) {
		this.isAuthorize = isAuthorize;
	}
	
	public static UserValidateCaptchaDTO buildUserValidateCaptchaDTO(boolean validate_captcha, boolean isAuthorize){
		UserValidateCaptchaDTO dto = new UserValidateCaptchaDTO();
		dto.setValidate_captcha(validate_captcha);
		dto.setAuthorize(isAuthorize);
		return dto;
	}
}