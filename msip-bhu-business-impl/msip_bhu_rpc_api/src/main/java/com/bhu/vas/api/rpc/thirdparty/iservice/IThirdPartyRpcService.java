package com.bhu.vas.api.rpc.thirdparty.iservice;

import com.bhu.vas.api.rpc.RpcResponseDTO;


public interface IThirdPartyRpcService {
	public RpcResponseDTO<Boolean> gomeBindDevice(String mac);
	public RpcResponseDTO<Boolean> gomeUnbindDevice(String mac);
}
