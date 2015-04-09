package com.bhu.vas.api.rpc.captcha.model;

import com.smartwork.msip.business.runtimeconf.RuntimeConfiguration;
import com.smartwork.msip.cores.helper.DateTimeHelper;
import com.smartwork.msip.cores.helper.MobileCaptchaCodeHelper;
import com.smartwork.msip.cores.orm.model.BaseStringModel;

@SuppressWarnings("serial")
public class CaptchaCode extends BaseStringModel{
	private String captcha;
	//基于多少秒后+updated_at过期
	private int expired_sec;
	
	private String date;
	private int times;
	
	public String getCaptcha() {
		return captcha;
	}
	
	public void setCaptcha(String captcha) {
		this.captcha = captcha;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public int getExpired_sec() {
		return expired_sec;
	}

	public void setExpired_sec(int expired_sec) {
		this.expired_sec = expired_sec;
	}

	public int getTimes() {
		return times;
	}

	public void setTimes(int times) {
		this.times = times;
	}

	@Override
	public void preUpdate() {
		this.setExpired_sec(RuntimeConfiguration.UserCaptchaCodeExpired);
		this.setCaptcha(MobileCaptchaCodeHelper.generateCaptchaCode());
		this.setDate(DateTimeHelper.formatDate(DateTimeHelper.FormatPattern5));
		super.preUpdate();
	}
	@Override
	public void preInsert() {
		this.setExpired_sec(RuntimeConfiguration.UserCaptchaCodeExpired);
		this.setCaptcha(MobileCaptchaCodeHelper.generateCaptchaCode());
		this.setDate(DateTimeHelper.formatDate(DateTimeHelper.FormatPattern5));
		super.preInsert();
	}
	
	public boolean wasExpired(){
		long gap = System.currentTimeMillis() - this.getUpdated_at().getTime();
		if(gap > this.expired_sec*1000) return true;
		
		return false;
	}
	
	/*public static void main(String[] argv) throws InterruptedException{
		CaptchaCode code = new CaptchaCode();
		code.setId("18612272825");
		code.setExpired(RuntimeConfiguration.UserCaptchaCodeExpired);
		code.setCaptcha(MobileCaptchaCodeHelper.generateCaptchaCode());
		code.setUpdated_at(new Date());
		
		System.out.println("1"+code.wasExpired());
		Thread.sleep(1000);
		System.out.println("2"+code.wasExpired());
		
		Thread.sleep(20000);
		System.out.println("3"+code.wasExpired());
		Thread.sleep(9000);
		System.out.println("4"+code.wasExpired());
		Thread.sleep(1000);
		System.out.println("5"+code.wasExpired());
		Thread.sleep(1000);
		System.out.println("6"+code.wasExpired());
		Thread.sleep(5000);
		System.out.println("7"+code.wasExpired());
	}*/
}