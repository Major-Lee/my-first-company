package com.bhu.vas.api.rpc.statistics.iservice;

import java.util.List;
import java.util.Map;

import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.statistics.dto.UserAccessStatisticsDTO;
import com.bhu.vas.api.rpc.statistics.dto.UserBrandDTO;
import com.bhu.vas.api.rpc.statistics.dto.UserBrandStatisticsDTO;
import com.smartwork.msip.cores.orm.support.page.TailPage;

public interface IStatisticsRpcService {
	public RpcResponseDTO<Map<String,Object>> buildHandsetOnline4Chart(int type,int ml);
	public RpcResponseDTO<Map<String,Object>> buildDeviceOnline4Chart(int type,int ml);
	TailPage<UserAccessStatisticsDTO> fetchUserAccessStatistics(String date, int pageNo, int pageSize);
	TailPage<UserAccessStatisticsDTO> fetchUserAccessStatisticsWithDeviceMac(String date, String device_mac,
																			 int pageNo, int pageSize);

	RpcResponseDTO<List<UserBrandDTO>> fetchUserBrandStatistics(String date);

	TailPage<UserBrandStatisticsDTO> fetchUserBrandStatistics(int pageNo, int pageSize);

}
