package com.bhu.vas.exception;


@SuppressWarnings("serial")
public class TokenValidateBusinessException extends RuntimeException{
	private int validateCode;

	public TokenValidateBusinessException(int validateCode){
		this.validateCode = validateCode;
	}

	public int getValidateCode() {
		return validateCode;
	}

	public void setValidateCode(int validateCode) {
		this.validateCode = validateCode;
	}

	
}
