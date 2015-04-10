package com.bhu.vas.api.rpc;

import com.smartwork.msip.jdo.ResponseErrorCode;

public class RpcResponseDTOBuilder {
	public static <T extends java.io.Serializable> RpcResponseDTO<T> builderErrorRpcResponse(ResponseErrorCode errorCode){//,Class<T> classz){
		/*RpcResponseDTO<T> res = new RpcResponseDTO<T>();
		res.setErrorCode(errorCode);
		res.setPayload(null);
		return res;*/
		return builderErrorRpcResponse(errorCode,null);
	}
	
	public static <T extends java.io.Serializable> RpcResponseDTO<T> builderErrorRpcResponse(ResponseErrorCode errorCode,T payload){//,Class<T> classz){
		RpcResponseDTO<T> res = new RpcResponseDTO<T>();
		res.setErrorCode(errorCode);
		res.setPayload(payload);
		return res;
	}
	
	public static <T> RpcResponseDTO<T> builderSuccessRpcResponse(T payload){
		RpcResponseDTO<T> res = new RpcResponseDTO<T>();
		res.setErrorCode(null);
		res.setPayload(payload);
		return res;
	}
}
