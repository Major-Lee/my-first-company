package com.bhu.vas.api.rpc.unifyStatistics.iservice;

import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.unifyStatistics.vto.OnlineStatisticsVTO;
import com.bhu.vas.api.rpc.unifyStatistics.vto.StateStatisticsVTO;

public interface IUnifyStatisticsRpcService {
	public RpcResponseDTO<OnlineStatisticsVTO> onlineStatistics(String category,String queryParam);
	public RpcResponseDTO<StateStatisticsVTO> stateStat();
}
