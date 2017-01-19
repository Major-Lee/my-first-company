package com.bhu.vas.api.rpc.devices.iservice;

import com.bhu.vas.api.rpc.RpcResponseDTO;


public interface IThirdPartyRpcService {
	public RpcResponseDTO<Boolean> gomeBindDevice(String mac);
}
