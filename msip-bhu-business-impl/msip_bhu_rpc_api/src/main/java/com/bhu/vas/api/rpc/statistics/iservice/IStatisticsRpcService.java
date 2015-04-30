package com.bhu.vas.api.rpc.statistics.iservice;

import java.util.Map;

import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.statistics.UserAccessStatisticsDTO;
import com.bhu.vas.api.rpc.statistics.model.UserAccessStatistics;
import com.bhu.vas.api.vto.HandsetDeviceVTO;
import com.smartwork.msip.cores.orm.support.page.TailPage;

public interface IStatisticsRpcService {
	public RpcResponseDTO<Map<String,Object>> buildHandsetOnline4Chart(int type,int ml);
	public RpcResponseDTO<Map<String,Object>> buildDeviceOnline4Chart(int type,int ml);
	TailPage<UserAccessStatisticsDTO> fetchUserAccessStatistics(String date, int pageNo, int pageSize);
}
