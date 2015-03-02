package com.smartwork.msip.exception;

import com.smartwork.msip.jdo.ResponseErrorCode;

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
}
