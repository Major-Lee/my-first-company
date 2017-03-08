package com.bhu.vas.api.rpc.thirdparty.iservice;

import java.util.List;

import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.thirdparty.dto.SsidInfoDTO;
import com.bhu.vas.api.vto.SsidInfoVTO;


public interface ISsidRpcService {
	public RpcResponseDTO<Boolean> reportSsidInfo(String bssid, String ssid, String mode, String pwd, Double lat, Double lon);
	public RpcResponseDTO<SsidInfoVTO> querySsidInfo(String bssid, String ssid, String mode);
	public RpcResponseDTO<List<SsidInfoVTO>> batchQuerySsidInfo(List<SsidInfoDTO> queryObj);
}
