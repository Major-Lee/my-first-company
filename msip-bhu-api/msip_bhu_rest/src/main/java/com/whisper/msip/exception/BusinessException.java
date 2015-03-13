package com.whisper.msip.exception;

import com.smartwork.msip.jdo.ResponseErrorCode;

@SuppressWarnings("serial")
public class BusinessException extends RuntimeException{
	private ResponseErrorCode errorCode;
	private String[] payload;
	
	public ResponseErrorCode getErrorCode() {
		return errorCode;
	}


	public void setErrorCode(ResponseErrorCode errorCode) {
		this.errorCode = errorCode;
	}


	public BusinessException(ResponseErrorCode errorCode){
		this.errorCode = errorCode;
	}
	public BusinessException(ResponseErrorCode errorCode,String[] payload){
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
