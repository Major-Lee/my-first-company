package com.bhu.vas.rpc.facade;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.bag.SynchronizedSortedBag;
import org.springframework.stereotype.Service;

import com.bhu.vas.api.dto.statistics.DeviceStateStatisticsDTO;
import com.bhu.vas.api.dto.statistics.UserStateStatisticsDTO;
import com.bhu.vas.api.rpc.RpcResponseDTOBuilder;
import com.bhu.vas.api.rpc.unifyStatistics.dto.OnlineStatisticsDTO;
import com.bhu.vas.api.rpc.unifyStatistics.vto.OnlineStatisticsVTO;
import com.bhu.vas.api.rpc.unifyStatistics.vto.StateStatisticsVTO;
import com.bhu.vas.business.bucache.redis.serviceimpl.BusinessKeyDefine;
import com.bhu.vas.business.bucache.redis.serviceimpl.statistics.DeviceStateStatisticsHashService;
import com.bhu.vas.business.bucache.redis.serviceimpl.statistics.UserStateStatisticsHashService;
import com.smartwork.msip.cores.helper.DateTimeExtHelper;
import com.smartwork.msip.cores.helper.JsonHelper;
import com.smartwork.msip.exception.BusinessI18nCodeException;
import com.smartwork.msip.jdo.ResponseErrorCode;

/**
 * @author xiaowei by 16/04/12
 */
@Service
public class UnifyStatisticsFacadeRpcSerivce {
	// ********************author shibo************************
	public OnlineStatisticsVTO onlineStatistics(String category, String queryParam) {
		OnlineStatisticsVTO vto = null;
		vto = queryStatistics(category, queryParam);

		return vto;
	}

	private OnlineStatisticsVTO queryStatistics(String category, String queryParam) {
		Calendar cal = Calendar.getInstance();
		List<String> fragments = DateTimeExtHelper.generateServalDateFormat(cal.getTime());
		OnlineStatisticsVTO vto = new OnlineStatisticsVTO();
		List<OnlineStatisticsDTO> list = new ArrayList<OnlineStatisticsDTO>();
		vto.setList(list);
		OnlineStatisticsDTO dto0 = new OnlineStatisticsDTO();
		OnlineStatisticsDTO dto1 = new OnlineStatisticsDTO();
		OnlineStatisticsDTO dto2 = new OnlineStatisticsDTO();
		
		Map<String, String> map0 = new HashMap<String, String>();
		Map<String, String> map1 = new HashMap<String, String>();
		Map<String, String> map2 = new HashMap<String, String>();
		
		Map<String, Long> vmap0 = new HashMap<String, Long>();
		Map<String, Long> vmap1 = new HashMap<String, Long>();
		Map<String, Long> vmap2 = new HashMap<String, Long>();
		
		if (category.equals("device")) {
			vto.setName(category);
			switch (queryParam) {
			case "D":
				//读取今天在线设备数据
				map0 = DeviceStateStatisticsHashService.getInstance().fetchAll(
						fragments.get(DateTimeExtHelper.YEAR_MONTH_DD),
						BusinessKeyDefine.Statistics.FragmentOnlineDailySuffixKey);
				if (!map0.isEmpty() || map0 != null) {
					dto0.setName("0");
					for (Map.Entry<String, String> entry : map0.entrySet()) {
						DeviceStateStatisticsDTO data = JsonHelper.getDTO(entry.getValue(), DeviceStateStatisticsDTO.class);
						vmap0.put(entry.getKey(), data.getOnline_max());
					}
					dto0.setMap(vmap0);
					vto.getList().add(dto0);
				}
				
				//读取昨天在线设备数据
				cal.add(Calendar.DATE, -1);
				map1 = DeviceStateStatisticsHashService.getInstance().fetchAll(
						fragments.get(DateTimeExtHelper.YEAR_MONTH_DD),
						BusinessKeyDefine.Statistics.FragmentOnlineDailySuffixKey);
				if (!map1.isEmpty() || map1 != null){
					dto1.setName("1");
					for (Map.Entry<String, String> entry : map1.entrySet()) {
						DeviceStateStatisticsDTO data = JsonHelper.getDTO(entry.getValue(), DeviceStateStatisticsDTO.class);
						vmap1.put(entry.getKey(), data.getOnline_max());
					}
					dto1.setMap(vmap1);
					vto.getList().add(dto1);
				}
				//读取前天在线设备数据
				cal.add(Calendar.DATE, -1);
				map2 = DeviceStateStatisticsHashService.getInstance().fetchAll(
						fragments.get(DateTimeExtHelper.YEAR_MONTH_DD),
						BusinessKeyDefine.Statistics.FragmentOnlineDailySuffixKey);
				if (!map2.isEmpty() || map2 != null){
					dto2.setName("2");
					for (Map.Entry<String, String> entry : map2.entrySet()) {
						DeviceStateStatisticsDTO data = JsonHelper.getDTO(entry.getValue(), DeviceStateStatisticsDTO.class);
						vmap2.put(entry.getKey(), data.getOnline_max());
					}
					dto2.setMap(vmap2);
					vto.getList().add(dto2);
				}
				cal.add(Calendar.DATE, 2);
				break;
			case "W":
				//读取本周在线设备数据
				map0 = DeviceStateStatisticsHashService.getInstance().fetchAll(
						fragments.get(DateTimeExtHelper.YEAR_WHICH_WEEK),
						BusinessKeyDefine.Statistics.FragmentOnlineWeeklySuffixKey);
				if (!map0.isEmpty() || map0 != null){
					dto0.setName("0");
					for (Map.Entry<String, String> entry : map0.entrySet()) {
						DeviceStateStatisticsDTO data = JsonHelper.getDTO(entry.getValue(), DeviceStateStatisticsDTO.class);
						vmap0.put(entry.getKey(), data.getOnline_max());
					}
					dto0.setMap(vmap0);
					vto.getList().add(dto0);
				}
				//读取上周在线设备数据
				cal.add(Calendar.DATE, -1);
				map1 = DeviceStateStatisticsHashService.getInstance().fetchAll(
						fragments.get(DateTimeExtHelper.YEAR_WHICH_WEEK),
						BusinessKeyDefine.Statistics.FragmentOnlineWeeklySuffixKey);
				if (!map1.isEmpty() || map1 != null){
					dto1.setName("1");
					for (Map.Entry<String, String> entry : map1.entrySet()) {
						DeviceStateStatisticsDTO data = JsonHelper.getDTO(entry.getValue(), DeviceStateStatisticsDTO.class);
						vmap1.put(entry.getKey(), data.getOnline_max());
					}
					dto1.setMap(vmap1);
					vto.getList().add(dto1);
				}
				//读取上上周的在线设备数据
				cal.add(Calendar.WEEK_OF_YEAR, -1);
				map2= DeviceStateStatisticsHashService.getInstance().fetchAll(
						fragments.get(DateTimeExtHelper.YEAR_WHICH_WEEK),
						BusinessKeyDefine.Statistics.FragmentOnlineWeeklySuffixKey);
				if (!map2.isEmpty() || map2 != null){
					dto2.setName("2");
					for (Map.Entry<String, String> entry : map2.entrySet()) {
						DeviceStateStatisticsDTO data = JsonHelper.getDTO(entry.getValue(), DeviceStateStatisticsDTO.class);
						vmap2.put(entry.getKey(), data.getOnline_max());
					}
					dto2.setMap(vmap2);
					vto.getList().add(dto2);
				}
				cal.add(Calendar.WEEK_OF_YEAR, 2);
				
				break;
			case "M":
				//读取当月在线设备数据
				map0 = DeviceStateStatisticsHashService.getInstance().fetchAll(
						fragments.get(DateTimeExtHelper.YEAR_MONTH),
						BusinessKeyDefine.Statistics.FragmentOnlineMonthlySuffixKey);
				if (!map0.isEmpty() || map0 != null){
					dto0.setName("0");
					for (Map.Entry<String, String> entry : map0.entrySet()) {
						DeviceStateStatisticsDTO data = JsonHelper.getDTO(entry.getValue(), DeviceStateStatisticsDTO.class);
						vmap0.put(entry.getKey(), data.getOnline_max());
					}
					dto0.setMap(vmap0);
					vto.getList().add(dto0);
				}
				//读取前一月在线设备数据
				cal.add(Calendar.MONTH, -1);
				map1 = DeviceStateStatisticsHashService.getInstance().fetchAll(
						fragments.get(DateTimeExtHelper.YEAR_MONTH),
						BusinessKeyDefine.Statistics.FragmentOnlineMonthlySuffixKey);
				if (!map1.isEmpty() || map1 != null){
					dto1.setName("1");
					for (Map.Entry<String, String> entry : map1.entrySet()) {
						DeviceStateStatisticsDTO data = JsonHelper.getDTO(entry.getValue(), DeviceStateStatisticsDTO.class);
						vmap1.put(entry.getKey(), data.getOnline_max());
					}
					dto1.setMap(vmap1);
					vto.getList().add(dto1);
				}
				//读取前二月在线设备数据
				cal.add(Calendar.MONTH, -1);
				map2 = DeviceStateStatisticsHashService.getInstance().fetchAll(
						fragments.get(DateTimeExtHelper.YEAR_MONTH),
						BusinessKeyDefine.Statistics.FragmentOnlineMonthlySuffixKey);
				if (!map2.isEmpty() || map2 != null){
					dto2.setName("2");
					for (Map.Entry<String, String> entry : map2.entrySet()) {
						DeviceStateStatisticsDTO data = JsonHelper.getDTO(entry.getValue(), DeviceStateStatisticsDTO.class);
						vmap2.put(entry.getKey(), data.getOnline_max());
					}
					dto2.setMap(vmap2);
					vto.getList().add(dto2);
				}
				cal.add(Calendar.MONTH, 2);
				break;
			default:
				return null;
			}
		} else if (category.equals("user")) {
			vto.setName(category);
			switch (queryParam) {
			case "D":
				//读取今天在线用户数据
				map0 = UserStateStatisticsHashService.getInstance().fetchAll(
						fragments.get(DateTimeExtHelper.YEAR_MONTH_DD),
						BusinessKeyDefine.Statistics.FragmentOnlineDailySuffixKey);
				if (!map0.isEmpty() || map0 != null){
					dto0.setName("0");
					for (Map.Entry<String, String> entry : map0.entrySet()) {
						UserStateStatisticsDTO data = JsonHelper.getDTO(entry.getValue(), UserStateStatisticsDTO.class);
						vmap0.put(entry.getKey(), data.getOnline_max());
					}
					dto0.setMap(vmap0);
					vto.getList().add(dto0);
				}
				//读取昨天在线用户数据
				cal.add(Calendar.DAY_OF_YEAR, -1);
				map1 = UserStateStatisticsHashService.getInstance().fetchAll(
						fragments.get(DateTimeExtHelper.YEAR_MONTH_DD),
						BusinessKeyDefine.Statistics.FragmentOnlineDailySuffixKey);
				if (!map1.isEmpty() || map1 != null){	
					dto1.setName("1");
					for (Map.Entry<String, String> entry : map1.entrySet()) {
						UserStateStatisticsDTO data = JsonHelper.getDTO(entry.getValue(), UserStateStatisticsDTO.class);
						vmap1.put(entry.getKey(), data.getOnline_max());
					}
					dto1.setMap(vmap1);
					vto.getList().add(dto1);
				}
				//读取前天在线用户数据
				cal.add(Calendar.DAY_OF_YEAR, -1);
				map2 = UserStateStatisticsHashService.getInstance().fetchAll(
							fragments.get(DateTimeExtHelper.YEAR_MONTH_DD),
							BusinessKeyDefine.Statistics.FragmentOnlineDailySuffixKey);
				if (!map2.isEmpty() || map2 != null){
					dto2.setName("2");
					for (Map.Entry<String, String> entry : map2.entrySet()) {
						UserStateStatisticsDTO data = JsonHelper.getDTO(entry.getValue(), UserStateStatisticsDTO.class);
						vmap2.put(entry.getKey(), data.getOnline_max());
					}
					dto2.setMap(vmap2);
					vto.getList().add(dto2);
				}
				cal.add(Calendar.DAY_OF_YEAR, 2);
				break;
			case "W":
				//读取本周在线用户数据
				map0 = UserStateStatisticsHashService.getInstance().fetchAll(
						fragments.get(DateTimeExtHelper.YEAR_WHICH_WEEK),
						BusinessKeyDefine.Statistics.FragmentOnlineWeeklySuffixKey);
				if (!map0.isEmpty() || map0 != null){	
					dto0.setName("0");
					for (Map.Entry<String, String> entry : map0.entrySet()) {
						UserStateStatisticsDTO data = JsonHelper.getDTO(entry.getValue(), UserStateStatisticsDTO.class);
						vmap0.put(entry.getKey(), data.getOnline_max());
					}
					dto0.setMap(vmap0);
					vto.getList().add(dto0);
				}
				//读取上周在线用户数据
				cal.add(Calendar.WEEK_OF_YEAR, -1);
				map1 = UserStateStatisticsHashService.getInstance().fetchAll(
						fragments.get(DateTimeExtHelper.YEAR_WHICH_WEEK),
						BusinessKeyDefine.Statistics.FragmentOnlineWeeklySuffixKey);
				if (!map1.isEmpty() || map1 != null){	
					dto1.setName("1");
					for (Map.Entry<String, String> entry : map1.entrySet()) {
						UserStateStatisticsDTO data = JsonHelper.getDTO(entry.getValue(), UserStateStatisticsDTO.class);
						vmap1.put(entry.getKey(), data.getOnline_max());
					}
					dto1.setMap(vmap1);
					vto.getList().add(dto1);
				}
				//读取前周在线用户数据
				cal.add(Calendar.WEEK_OF_YEAR, -1);
				map2 = UserStateStatisticsHashService.getInstance().fetchAll(
						fragments.get(DateTimeExtHelper.YEAR_WHICH_WEEK),
						BusinessKeyDefine.Statistics.FragmentOnlineWeeklySuffixKey);
				if (!map2.isEmpty() || map2 != null){
					dto2.setName("2");
					for (Map.Entry<String, String> entry : map2.entrySet()) {
						UserStateStatisticsDTO data = JsonHelper.getDTO(entry.getValue(), UserStateStatisticsDTO.class);
						vmap2.put(entry.getKey(), data.getOnline_max());
					}
					dto2.setMap(vmap2);
					vto.getList().add(dto2);
				}
				cal.add(Calendar.WEEK_OF_YEAR, 2);
				break;
			case "M":
				//读取本月在线用户数据
				map0 = UserStateStatisticsHashService.getInstance().fetchAll(fragments.get(DateTimeExtHelper.YEAR_MONTH),
						BusinessKeyDefine.Statistics.FragmentOnlineMonthlySuffixKey);
				if (!map0.isEmpty() || map0 != null){	
					dto0.setName("0");
					for (Map.Entry<String, String> entry : map0.entrySet()) {
						UserStateStatisticsDTO data = JsonHelper.getDTO(entry.getValue(), UserStateStatisticsDTO.class);
						vmap0.put(entry.getKey(), data.getOnline_max());
					}
					dto0.setMap(vmap0);
					vto.getList().add(dto0);
				}
				//读取上月在线用户数据
				cal.add(Calendar.MONTH, -1);
				map1 = UserStateStatisticsHashService.getInstance().fetchAll(fragments.get(DateTimeExtHelper.YEAR_MONTH),
						BusinessKeyDefine.Statistics.FragmentOnlineMonthlySuffixKey);
				if (!map1.isEmpty() || map1 != null){	
					dto1.setName("1");
					for (Map.Entry<String, String> entry : map1.entrySet()) {
						UserStateStatisticsDTO data = JsonHelper.getDTO(entry.getValue(), UserStateStatisticsDTO.class);
						vmap1.put(entry.getKey(), data.getOnline_max());
					}
					dto1.setMap(vmap1);
					vto.getList().add(dto1);
				}
				//读取上上月在线用户数据
				cal.add(Calendar.MONTH, -1);
				map2 = UserStateStatisticsHashService.getInstance().fetchAll(fragments.get(DateTimeExtHelper.YEAR_MONTH),
						BusinessKeyDefine.Statistics.FragmentOnlineMonthlySuffixKey);
				if (!map2.isEmpty() || map2 != null){	
					dto2.setName("2");
					for (Map.Entry<String, String> entry : map2.entrySet()) {
						UserStateStatisticsDTO data = JsonHelper.getDTO(entry.getValue(), UserStateStatisticsDTO.class);
						vmap2.put(entry.getKey(), data.getOnline_max());
					}
					dto2.setMap(vmap2);
					vto.getList().add(dto2);
				}
				cal.add(Calendar.MONTH, 2);
				break;
			default:
				return null;
			}
		}
		return vto;
	}
	// ********************author shibo************************

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
		vto.setD_weeklive(statLivelyCount(7, cal));
		vto.setD_monthlive(statLivelyCount(30, cal));
		return vto;
	}

	/**
	 * 活跃度统计
	 * 
	 * @param day
	 * @param cal
	 * @return
	 */
	private int statLivelyCount(int day, Calendar cal) {
		int count = 0;

		List<String> fragments = new ArrayList<String>();
		for (int i = 0; i < day; i++) {
			cal.add(Calendar.DATE, -(i == 0 ? i : 1));
			List<String> lastDayDeviceFrag = DateTimeExtHelper.generateServalDateFormat(cal.getTime());
			fragments.add(lastDayDeviceFrag.get(DateTimeExtHelper.YEAR_MONTH_DD));
		}
		List<Object> dtoList = null;
		try {
			dtoList = DeviceStateStatisticsHashService.getInstance().batchFetchValue(fragments,
					BusinessKeyDefine.Statistics.FragmentOnlineDailySuffixKey, "23");
		} catch (Exception e) {
			throw new BusinessI18nCodeException(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}

		for (Object obj : dtoList) {
			String str = String.valueOf(obj);
			if (str != null) {
				DeviceStateStatisticsDTO dto =JsonHelper.getDTO(str, DeviceStateStatisticsDTO.class);
				if (dto != null) {
					count += dto.getLiveness();
				}
			}
		}
		cal.add(Calendar.DATE, day - 1);
		return count;
	}
}