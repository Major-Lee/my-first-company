package com.smartwork.msip.exception;

import com.smartwork.msip.cores.i18n.LocalI18NMessageSource;
import com.smartwork.msip.jdo.ResponseErrorCode;

/**
 * i18n集成异常类处理
 * 把i18n的特性和异常的提示消息合成
 * @author Edmond
 *
 */
@SuppressWarnings("serial")
public class BusinessI18nCodeException extends RuntimeException{
	private ResponseErrorCode errorCode;
	private String[] payload;
	
	public ResponseErrorCode getErrorCode() {
		return errorCode;
	}


	public void setErrorCode(ResponseErrorCode errorCode) {
		this.errorCode = errorCode;
	}


	public BusinessI18nCodeException(ResponseErrorCode errorCode){
		this.errorCode = errorCode;
	}
	public BusinessI18nCodeException(ResponseErrorCode errorCode,String[] payload){
		this.errorCode = errorCode;
		this.payload = payload;
	}

	public String[] getPayload() {
		return payload;
	}


	public void setPayload(String[] payload) {
		this.payload = payload;
	}


	@Override
	public String getMessage() {
		return LocalI18NMessageSource.getInstance().getMessage(this.errorCode.i18n(),this.payload);
		//return (this.errorCode.i18n());
	}
	
	
}
