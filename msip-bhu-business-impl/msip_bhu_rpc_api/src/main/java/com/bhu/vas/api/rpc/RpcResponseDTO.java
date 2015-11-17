package com.bhu.vas.api.rpc;

import com.smartwork.msip.jdo.ResponseErrorCode;

@SuppressWarnings("serial")
public class RpcResponseDTO<T> implements java.io.Serializable{
	private ResponseErrorCode errorCode;
	//用于描述errorCode 对应的i18n里面的动态字符{1}-{2}
	private String[] errorCodeAttach;
	private T payload;
	
	public RpcResponseDTO(){
		
	}
	public RpcResponseDTO(ResponseErrorCode errorCode, T payload) {
		super();
		this.errorCode = errorCode;
		this.payload = payload;
	}
	
	public RpcResponseDTO(ResponseErrorCode errorCode,String[] errorCodeAttach, T payload) {
		super();
		this.errorCode = errorCode;
		this.errorCodeAttach = errorCodeAttach;
		this.payload = payload;
	}
	
	public ResponseErrorCode getErrorCode() {
		return errorCode;
	}
	public void setErrorCode(ResponseErrorCode errorCode) {
		this.errorCode = errorCode;
	}
	/*public int getResCode() {
		return resCode;
	}
	public void setResCode(int resCode) {
		this.resCode = resCode;
	}*/
	public T getPayload() {
		return payload;
	}
	public void setPayload(T payload) {
		this.payload = payload;
	}
	
	public boolean hasError(){
		return getErrorCode() != null;
	}
	public String[] getErrorCodeAttach() {
		return errorCodeAttach;
	}
	public void setErrorCodeAttach(String[] errorCodeAttach) {
		this.errorCodeAttach = errorCodeAttach;
	}
}
