package com.bhu.vas.msip.exception;

import com.smartwork.msip.jdo.ResponseErrorCode;
import com.smartwork.msip.jdo.ResponseStatus;

@SuppressWarnings("serial")
public class BusinessException extends RuntimeException{
	private ResponseStatus response_status;
	private ResponseErrorCode errorCode;
	private String[] payload;
	
	public BusinessException(ResponseStatus response_status,ResponseErrorCode errorCode){
		this.response_status = response_status;
		this.errorCode = errorCode;
	}
	
	public BusinessException(ResponseStatus response_status,ResponseErrorCode errorCode,Throwable cause){
		this.response_status = response_status;
		this.errorCode = errorCode;
		this.initCause(cause);
	}
	public BusinessException(ResponseStatus response_status,ResponseErrorCode errorCode,String[] payload,Throwable cause){
		this.response_status = response_status;
		this.errorCode = errorCode;
		this.payload = payload;
		this.initCause(cause);
	}
	
	public ResponseErrorCode getErrorCode() {
		return errorCode;
	}


	public void setErrorCode(ResponseErrorCode errorCode) {
		this.errorCode = errorCode;
	}

	public String[] getPayload() {
		return payload;
	}


	public void setPayload(String[] payload) {
		this.payload = payload;
	}
	
	public ResponseStatus getResponse_status() {
		return response_status;
	}
	
	public void setResponse_status(ResponseStatus response_status) {
		this.response_status = response_status;
	}
}
