package com.bhu.vas.rpc.service.device;

import com.bhu.vas.api.rpc.unifyStatistics.iservice.IUnifyStatisticsRpcService;
import com.bhu.vas.api.vto.statistics.OnlineStatisticsVTO;
import com.bhu.vas.rpc.facade.UnifyStatisticsFacadeRpcSerivce;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

/**
 * Created by xiaowei on 4/13/16.
 */
@Service("unifyStatisticsRpcService")
public class UnifyStatisticsRpcService implements IUnifyStatisticsRpcService {
	@Resource
	private UnifyStatisticsFacadeRpcSerivce unifyStatisticsFacadeRpcSerivce;
	@Override
	public OnlineStatisticsVTO onlineStatistics(String queryParam) {
		OnlineStatisticsVTO vto = unifyStatisticsFacadeRpcSerivce.onlineStatistics(queryParam);
		return vto;
	}
	
}
