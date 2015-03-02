package com.bhu.vas.exception;


@SuppressWarnings("serial")
public class UDIDValidateBusinessException extends RuntimeException{
	private int validateCode;

	public UDIDValidateBusinessException(int validateCode){
		this.validateCode = validateCode;
	}

	public int getValidateCode() {
		return validateCode;
	}

	public void setValidateCode(int validateCode) {
		this.validateCode = validateCode;
	}

	
}
