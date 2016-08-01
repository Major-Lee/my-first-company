package com.bhu.vas.rpc.service.device;

import java.util.HashMap;
import java.util.Map;

import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.RpcResponseDTOBuilder;
import com.bhu.vas.api.rpc.unifyStatistics.iservice.IUnifyStatisticsRpcService;
import com.bhu.vas.api.rpc.unifyStatistics.vto.OnlineStatisticsVTO;
import com.bhu.vas.api.rpc.unifyStatistics.vto.StateStatisticsVTO;
import com.bhu.vas.rpc.facade.SSIDStatisticFacadeRpcService;
import com.bhu.vas.rpc.facade.UnifyStatisticsFacadeRpcSerivce;
import com.smartwork.msip.exception.BusinessI18nCodeException;

import javax.annotation.Resource;

import org.elasticsearch.common.lang3.StringUtils;
import org.springframework.stereotype.Service;

/**
 * Created by xiaowei on 4/13/16.
 */
@Service("unifyStatisticsRpcService")
public class UnifyStatisticsRpcService implements IUnifyStatisticsRpcService {
	@Resource
	private UnifyStatisticsFacadeRpcSerivce unifyStatisticsFacadeRpcSerivce;
	
	@Resource
	private SSIDStatisticFacadeRpcService  statisticFacadeRpcService;
	@Override
	public RpcResponseDTO<OnlineStatisticsVTO> onlineStatistics(String category,String queryParam) {
		try {
			OnlineStatisticsVTO vto = unifyStatisticsFacadeRpcSerivce.onlineStatistics(category,queryParam);
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(vto);
		} catch (BusinessI18nCodeException i18nex) {
			return RpcResponseDTOBuilder.builderErrorRpcResponse(i18nex.getErrorCode(), i18nex.getPayload());
		}
	}
	@Override
	public RpcResponseDTO<StateStatisticsVTO> stateStat() {
		try {
			StateStatisticsVTO vto = unifyStatisticsFacadeRpcSerivce.stateStat();
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(vto);
		} catch (BusinessI18nCodeException i18nex) {
			return RpcResponseDTOBuilder.builderErrorRpcResponse(i18nex.getErrorCode(), i18nex.getPayload());
		}
	}
	@Override
	public Map<String,Object> querySSIDStatisticsInfo(Map<String,Object> map) {
		//返回结果
		Map<String,Object> result = new HashMap<String,Object>();
		result = statisticFacadeRpcService.querySSIDStatisticInfo(map);
		return result;
	}
}
