package com.bhu.vas.api.rpc.thirdparty.iservice;

import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.thirdparty.dto.GomeConfigDTO;
import com.bhu.vas.api.rpc.thirdparty.dto.GomeDeviceDTO;


public interface IThirdPartyRpcService {
	public RpcResponseDTO<Boolean> gomeBindDevice(String mac);
	public RpcResponseDTO<Boolean> gomeUnbindDevice(String mac);
	public RpcResponseDTO<Boolean> gomeDeviceControl(String mac, GomeConfigDTO dto);
	public RpcResponseDTO<GomeDeviceDTO> gomeDeviceOnlineGet(String mac);
}
