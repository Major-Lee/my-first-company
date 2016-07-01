package com.bhu.vas.rpc.facade;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.springframework.stereotype.Service;

import com.bhu.vas.api.dto.statistics.DeviceStateStatisticsDTO;
import com.bhu.vas.api.dto.statistics.UserStateStatisticsDTO;
import com.bhu.vas.api.vto.statistics.StateStatisticsVTO;
import com.bhu.vas.business.bucache.redis.serviceimpl.BusinessKeyDefine;
import com.bhu.vas.business.bucache.redis.serviceimpl.statistics.DeviceStateStatisticsHashService;
import com.bhu.vas.business.bucache.redis.serviceimpl.statistics.UserStateStatisticsHashService;
import com.smartwork.msip.cores.helper.DateTimeExtHelper;
import com.smartwork.msip.cores.helper.JsonHelper;

/**
 * @author xiaowei by 16/04/12
 */
@Service
public class UnifyStatisticsFacadeRpcSerivce {

	public StateStatisticsVTO stateStat() {
		Calendar cal = Calendar.getInstance();
		StateStatisticsVTO vto = buildStateStatisticsVTO(cal);
		return vto;
	}

	private StateStatisticsVTO buildStateStatisticsVTO(Calendar cal) {

		StateStatisticsVTO vto = new StateStatisticsVTO();
		List<String> fragments = DateTimeExtHelper.generateServalDateFormat(cal.getTime());
		String str = null;

		// ********************用户状态统计************************
		str = UserStateStatisticsHashService.getInstance().fetchStatistics(
				fragments.get(DateTimeExtHelper.YEAR_MONTH_DD),
				BusinessKeyDefine.Statistics.FragmentOnlineDailySuffixKey, fragments.get(DateTimeExtHelper.HH));
		// 若没有当前时段信息，则取上一时段
		if (str == null || str.isEmpty()) {
			cal.add(Calendar.HOUR_OF_DAY, -1);
			List<String> lastHourUserFrag = DateTimeExtHelper.generateServalDateFormat(cal.getTime());
			str = UserStateStatisticsHashService.getInstance().fetchStatistics(
					lastHourUserFrag.get(DateTimeExtHelper.YEAR_MONTH_DD),
					BusinessKeyDefine.Statistics.FragmentOnlineDailySuffixKey,
					lastHourUserFrag.get(DateTimeExtHelper.HH));
			cal.add(Calendar.HOUR_OF_DAY, 1);
		}
		UserStateStatisticsDTO userDto = JsonHelper.getDTO(str, UserStateStatisticsDTO.class);
		vto.setU_counts(userDto.getCountsUser());
		vto.setU_current(userDto.getCurrentUser());
		vto.setU_newInc(userDto.getNewInc());
		vto.setU_online(userDto.getOnline_max());

		// 获取昨日23点信息
		cal.add(Calendar.DATE, -1);
		List<String> lastDayUserFrag = DateTimeExtHelper.generateServalDateFormat(cal.getTime());
		str = UserStateStatisticsHashService.getInstance().fetchStatistics(
				lastDayUserFrag.get(DateTimeExtHelper.YEAR_MONTH_DD),
				BusinessKeyDefine.Statistics.FragmentOnlineDailySuffixKey, "23");
		cal.add(Calendar.DATE, 1);
		UserStateStatisticsDTO lastDayUserDto = JsonHelper.getDTO(str, UserStateStatisticsDTO.class);
		vto.setU_yestInc(lastDayUserDto != null ? lastDayUserDto.getNewInc() : null);

		// ********************设备状态统计************************
		str = DeviceStateStatisticsHashService.getInstance().fetchStatistics(
				fragments.get(DateTimeExtHelper.YEAR_MONTH_DD),
				BusinessKeyDefine.Statistics.FragmentOnlineDailySuffixKey, fragments.get(DateTimeExtHelper.HH));
		// 若没有当前时段信息，则取上一时段
		if (str == null || str.isEmpty()) {
			cal.add(Calendar.HOUR_OF_DAY, -1);
			List<String> lastHourDeviceFrag = DateTimeExtHelper.generateServalDateFormat(cal.getTime());
			str = DeviceStateStatisticsHashService.getInstance().fetchStatistics(
					lastHourDeviceFrag.get(DateTimeExtHelper.YEAR_MONTH_DD),
					BusinessKeyDefine.Statistics.FragmentOnlineDailySuffixKey,
					lastHourDeviceFrag.get(DateTimeExtHelper.HH));
			cal.add(Calendar.HOUR_OF_DAY, 1);
		}
		DeviceStateStatisticsDTO deviceDto = JsonHelper.getDTO(str, DeviceStateStatisticsDTO.class);
		vto.setD_counts(deviceDto.getCountsDevices());
		vto.setD_online(deviceDto.getOnline_max());
		vto.setD_newInc(deviceDto.getNewInc());

		// 获取昨日23点信息
		cal.add(Calendar.DATE, -1);
		List<String> lastDayDeviceFrag = DateTimeExtHelper.generateServalDateFormat(cal.getTime());
		str = DeviceStateStatisticsHashService.getInstance().fetchStatistics(
				lastDayDeviceFrag.get(DateTimeExtHelper.YEAR_MONTH_DD),
				BusinessKeyDefine.Statistics.FragmentOnlineDailySuffixKey, "23");
		cal.add(Calendar.DATE, 1);
		DeviceStateStatisticsDTO lastDayDeviceDto = JsonHelper.getDTO(str, DeviceStateStatisticsDTO.class);
		vto.setD_yestInc(lastDayDeviceDto != null ? lastDayDeviceDto.getNewInc() : null);

		// 获取7日活跃率和30日活跃率
		vto.setD_weeklive(statLivelyCount(7,cal));
		vto.setD_monthlive(statLivelyCount(30,cal));
		return vto;
	}
	
	private int statLivelyCount(int day,Calendar cal){
		int count = 0;
		List<String> fragments = new ArrayList<String>();
		for (int i = 0; i < day; i++) {
			cal.add(Calendar.DATE, -1);
			List<String> lastDayDeviceFrag = DateTimeExtHelper.generateServalDateFormat(cal.getTime());
			fragments.add(lastDayDeviceFrag.get(DateTimeExtHelper.YEAR_MONTH_DD));
		}
		
		List<Object> dtoList=  DeviceStateStatisticsHashService.getInstance().batchFetchValue(fragments,
				BusinessKeyDefine.Statistics.FragmentOnlineDailySuffixKey, "23");
		
		for (Object obj : dtoList) {
			String str = String.valueOf(obj);
			if (str != null) {
				count += JsonHelper.getDTO(str,DeviceStateStatisticsDTO.class).getLiveness();
			}
		}
		cal.add(Calendar.DATE, day-1);
		return count;
	}
}