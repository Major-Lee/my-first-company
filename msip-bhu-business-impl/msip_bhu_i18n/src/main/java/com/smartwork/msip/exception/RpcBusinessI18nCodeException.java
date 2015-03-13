package com.smartwork.msip.exception;

import com.smartwork.msip.jdo.ResponseErrorCode;

@SuppressWarnings("serial")
public class RpcBusinessI18nCodeException extends RuntimeException{
	private String errorCode;
	public RpcBusinessI18nCodeException(String errorCode){
		this.errorCode = errorCode;
	}
	public String getErrorCode() {
		return errorCode;
	}
	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public ResponseErrorCode locateResponseErrorCode(){
		return ResponseErrorCode.getResponseErrorCodeByCode(errorCode);
	}
	
	@Override
	public String getMessage() {
		//return LocalI18NMessageSource.getInstance().getMessage(this.errorCode.i18n());
		return ResponseErrorCode.getResponseErrorCodeByCode(errorCode).i18n();
	}
	
	/*private ResponseErrorCode errorCode;
	private String[] payload;*/
	
/*	public ResponseErrorCode getErrorCode() {
		return errorCode;
	}


	public void setErrorCode(ResponseErrorCode errorCode) {
		this.errorCode = errorCode;
	}


	public RpcBusinessException(ResponseErrorCode errorCode){
		this.errorCode = errorCode;
	}
	public RpcBusinessException(ResponseErrorCode errorCode,String[] payload){
		this.errorCode = errorCode;
		this.payload = payload;
	}

	public String[] getPayload() {
		return payload;
	}


	public void setPayload(String[] payload) {
		this.payload = payload;
	}*/
}
