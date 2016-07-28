package com.bhu.vas.api.rpc.unifyStatistics.iservice;

import java.util.Map;

import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.unifyStatistics.vto.OnlineStatisticsVTO;
import com.bhu.vas.api.rpc.unifyStatistics.vto.StateStatisticsVTO;

public interface IUnifyStatisticsRpcService {
	public RpcResponseDTO<OnlineStatisticsVTO> onlineStatistics(String category,String queryParam);
	public RpcResponseDTO<StateStatisticsVTO> stateStat();
	
	//add By Jason 2016-07-18 Start
	//查询SSID统计信息
	public String querySSIDStatisticsInfo(Map<String,Object> map);
	//add By Jason 2016-07-18 E N D
}
