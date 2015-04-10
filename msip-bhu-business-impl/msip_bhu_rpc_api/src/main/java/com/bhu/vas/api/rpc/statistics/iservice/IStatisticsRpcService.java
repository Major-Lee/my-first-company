package com.bhu.vas.api.rpc.statistics.iservice;

import java.util.Map;

import com.bhu.vas.api.rpc.RpcResponseDTO;

public interface IStatisticsRpcService {
	public RpcResponseDTO<Map<String,Object>> buildHandsetOnline4Chart(int type,int ml);
	public RpcResponseDTO<Map<String,Object>> buildDeviceOnline4Chart(int type,int ml);
}
