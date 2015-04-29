package com.bhu.vas.rpc.service.device;

import java.util.Map;

import javax.annotation.Resource;

import com.bhu.vas.api.rpc.statistics.UserAccessStatisticsDTO;
import com.bhu.vas.api.rpc.statistics.model.UserAccessStatistics;
import com.bhu.vas.rpc.facade.UserAccessStatisticsFacadeService;
import com.smartwork.msip.cores.orm.support.page.TailPage;
import org.springframework.stereotype.Service;

import com.alibaba.dubbo.common.logger.Logger;
import com.alibaba.dubbo.common.logger.LoggerFactory;
import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.RpcResponseDTOBuilder;
import com.bhu.vas.api.rpc.statistics.iservice.IStatisticsRpcService;
import com.bhu.vas.rpc.facade.StatisticsDeviceMaxOnlineService;
import com.bhu.vas.rpc.facade.StatisticsUserMaxOnlineService;

@Service("statisticsRpcService")
public class StatisticsRpcService implements IStatisticsRpcService{
	private final Logger logger = LoggerFactory.getLogger(StatisticsRpcService.class);
	@Resource
	private StatisticsUserMaxOnlineService statisticsUserMaxOnlineService;

	@Resource
	private StatisticsDeviceMaxOnlineService statisticsDeviceMaxOnlineService;

	@Resource
	private UserAccessStatisticsFacadeService userAccessStatisticsFacadeService;

	
	@Override
	public RpcResponseDTO<Map<String, Object>> buildHandsetOnline4Chart(
			int type, int ml) {
		logger.info(String.format("buildHandsetOnline4Chart with type[%s] ml[%s]",type,ml));
		Map<String, Object> build4Chart = statisticsUserMaxOnlineService.build4Chart(type, ml);
		return RpcResponseDTOBuilder.builderSuccessRpcResponse(build4Chart);
	}

	@Override
	public RpcResponseDTO<Map<String, Object>> buildDeviceOnline4Chart(
			int type, int ml) {
		logger.info(String.format("buildDeviceOnline4Chart with type[%s] ml[%s]",type,ml));
		Map<String, Object> build4Chart = statisticsDeviceMaxOnlineService.build4Chart(type, ml);
		return RpcResponseDTOBuilder.builderSuccessRpcResponse(build4Chart);
	}

	@Override
	public TailPage<UserAccessStatisticsDTO> fetchUserAccessStatistics(String date, int pageNo, int pageSize) {
		return userAccessStatisticsFacadeService.fetchUserAccessStatistics(date, pageNo, pageSize);
	}

	@Override
	public boolean createUserAccessStatistics(String filepath) {
		return userAccessStatisticsFacadeService.createUserAccessStatistics(filepath);
	}
}
