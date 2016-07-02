package com.bhu.vas.api.rpc.unifyStatistics.iservice;

import com.bhu.vas.api.vto.statistics.OnlineStatisticsVTO;
import com.bhu.vas.api.vto.statistics.StateStatisticsVTO;

public interface IUnifyStatisticsRpcService {
	public OnlineStatisticsVTO onlineStatistics(String queryParam);
	public StateStatisticsVTO stateStat();
}
