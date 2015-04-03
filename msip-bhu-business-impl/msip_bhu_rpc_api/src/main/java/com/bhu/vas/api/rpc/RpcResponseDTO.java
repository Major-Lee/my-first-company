package com.bhu.vas.api.rpc;

@SuppressWarnings("serial")
public class RpcResponseDTO<T> implements java.io.Serializable{
	private int resCode;
	private T payload;
	
	public RpcResponseDTO(){
		
	}
	public RpcResponseDTO(int resCode, T payload) {
		super();
		this.resCode = resCode;
		this.payload = payload;
	}
	public int getResCode() {
		return resCode;
	}
	public void setResCode(int resCode) {
		this.resCode = resCode;
	}
	public T getPayload() {
		return payload;
	}
	public void setPayload(T payload) {
		this.payload = payload;
	}
	
	
}
