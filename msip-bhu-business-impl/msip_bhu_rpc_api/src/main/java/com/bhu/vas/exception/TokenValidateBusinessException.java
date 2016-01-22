package com.bhu.vas.exception;


@SuppressWarnings("serial")
public class TokenValidateBusinessException extends RuntimeException{
	private int uid;
	private int validateCode;

	public TokenValidateBusinessException(int uid,int validateCode){
		this.uid = uid;
		this.validateCode = validateCode;
	}
	
	public TokenValidateBusinessException(int validateCode){
		this.validateCode = validateCode;
	}

	public int getValidateCode() {
		return validateCode;
	}

	public void setValidateCode(int validateCode) {
		this.validateCode = validateCode;
	}

	public int getUid() {
		return uid;
	}

	public void setUid(int uid) {
		this.uid = uid;
	}

	
}
