package com.bhu.vas.rpc.service.device;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.bhu.vas.api.rpc.statistics.dto.UserAccessStatisticsDTO;
import com.bhu.vas.api.rpc.statistics.dto.UserBrandDTO;
import com.bhu.vas.api.rpc.statistics.dto.UserBrandStatisticsDTO;
import com.bhu.vas.api.rpc.statistics.dto.UserUrlDTO;
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
		logger.info(String.format("fetchUserAccessStatistics with date[%s] pageNo[%s] pageSize[%s]",
				date, pageNo, pageSize));
		return userAccessStatisticsFacadeService.fetchUserAccessStatistics(date, pageNo, pageSize);
	}

	@Override
	public TailPage<UserAccessStatisticsDTO> fetchUserAccessStatisticsWithDeviceMac(String date, String device_mac,
																					int pageNo, int pageSize) {
		logger.info(String.format("fetchUserAccessStatisticsWithDeviceMac with " +
						"date[%s] device_amc[%s] pageNo[%s] pageSize[%s]",
				date, device_mac, pageNo, pageSize));
		return userAccessStatisticsFacadeService.fetchUserAccessStatisticsWithDeviceMac(date,
				device_mac, pageNo, pageSize);
	}

	@Override
	public RpcResponseDTO<List<UserBrandDTO>> fetchUserBrandStatistics(String date) {
		logger.info(String.format("fetchUserBrandStatistics with date[%s] ", date));
		return userAccessStatisticsFacadeService.fetchUserBrandStatistics(date);
	}

	@Override
	public RpcResponseDTO<List<UserUrlDTO>> fetchUserUrlStatistics(String date) {
		logger.info(String.format("fetchUserUrlStatistics with date[%s] ", date));
		return userAccessStatisticsFacadeService.fetchUserUrlStatistics(date);
	}

	@Override
	public TailPage<UserBrandStatisticsDTO> fetchUserBrandStatistics(int pageNo, int pageSize) {
		logger.info(String.format("fetchUserBrandStatistics with pageNo[%s] pageSize[%s] ", pageNo, pageSize));
		return userAccessStatisticsFacadeService.fetchUserBrandStatistics(pageNo, pageSize);
	}
}
