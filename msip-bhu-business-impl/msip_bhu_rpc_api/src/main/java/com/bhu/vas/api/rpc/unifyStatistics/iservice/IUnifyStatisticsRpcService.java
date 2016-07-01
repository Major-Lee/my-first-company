package com.bhu.vas.api.rpc.unifyStatistics.iservice;

import java.util.List;
import java.util.Map;

import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.statistics.dto.UserAccessStatisticsDTO;
import com.bhu.vas.api.rpc.statistics.dto.UserBrandDTO;
import com.bhu.vas.api.rpc.statistics.dto.UserBrandStatisticsDTO;
import com.bhu.vas.api.rpc.statistics.dto.UserUrlDTO;
import com.bhu.vas.api.vto.statistics.OnlineStatisticsVTO;
import com.smartwork.msip.cores.orm.support.page.TailPage;

public interface IUnifyStatisticsRpcService {
	public OnlineStatisticsVTO onlineStatistics(String queryParam);
}
