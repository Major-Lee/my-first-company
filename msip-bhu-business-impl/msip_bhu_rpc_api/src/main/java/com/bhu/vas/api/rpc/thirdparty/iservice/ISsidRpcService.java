package com.bhu.vas.api.rpc.thirdparty.iservice;

import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.wifi.model.SsidInfo;


public interface ISsidRpcService {
	public RpcResponseDTO<Boolean> reportSsidInfo(String bssid, String ssid, String mode, String pwd, Double lat, Double lon);
	public RpcResponseDTO<SsidInfo> querySsidInfo(String bssid, String ssid, String mode);
}
