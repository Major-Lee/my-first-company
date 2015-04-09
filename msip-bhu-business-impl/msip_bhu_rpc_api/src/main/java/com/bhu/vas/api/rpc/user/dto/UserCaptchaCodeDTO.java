package com.bhu.vas.api.rpc.user.dto;

@SuppressWarnings("serial")
public class UserCaptchaCodeDTO implements java.io.Serializable{
	private int countrycode;
	private String acc;
	private String captcha;
	public int getCountrycode() {
		return countrycode;
	}
	public void setCountrycode(int countrycode) {
		this.countrycode = countrycode;
	}
	public String getAcc() {
		return acc;
	}
	public void setAcc(String acc) {
		this.acc = acc;
	}
	public String getCaptcha() {
		return captcha;
	}
	public void setCaptcha(String captcha) {
		this.captcha = captcha;
	}
	
}
