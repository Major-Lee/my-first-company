package com.bhu.vas.business.ds.device.facade;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.beanutils.BeanUtils;
import org.springframework.stereotype.Service;

import com.bhu.vas.api.dto.redis.DailyStatisticsDTO;
import com.bhu.vas.api.dto.redis.SystemStatisticsDTO;
import com.bhu.vas.business.bucache.redis.serviceimpl.BusinessKeyDefine;
import com.bhu.vas.business.bucache.redis.serviceimpl.statistics.DailyStatisticsHashService;
import com.bhu.vas.business.bucache.redis.serviceimpl.statistics.SystemStatisticsHashService;
import com.bhu.vas.business.ds.device.service.HandsetDeviceService;
import com.bhu.vas.business.ds.device.service.WifiDeviceService;
import com.smartwork.msip.cores.helper.ArithHelper;

@Service
public class DeviceFacadeService {
	//private final Logger logger = LoggerFactory.getLogger(DeviceFacadeService.class);
	
	@Resource
	private WifiDeviceService wifiDeviceService;
	
	@Resource
	private HandsetDeviceService handsetDeviceService;
	
	
	/**
	 * 用于填充当日daily的统计数据中的实际计算数据
	 * 1:设备接入次数平均（3/(1+2)）(实时计算)
	 * 2:设备活跃率（1+2）/总设备 (实时计算)
	 * 3:设备接入时长平均（4/(1+2)）(实时计算)
	 * 4:新设备占比（1/(1+2)）(实时计算)
	 * @return
	 */
	public DailyStatisticsDTO dailyHandsetStatisticsArith(){
		DailyStatisticsDTO dailyStatisticsDto = DailyStatisticsHashService.getInstance().getStatistics(
				BusinessKeyDefine.Statistics.DailyStatisticsHandsetInnerPrefixKey);
		if(dailyStatisticsDto == null) {
			return new DailyStatisticsDTO();
		}
		return dailyStatisticsArith(dailyStatisticsDto);
	}
	
	/**
	 * 用于填充任何日期daily的统计数据中的实际计算数据
	 * 1:设备接入次数平均（3/(1+2)）(实时计算)
	 * 2:设备活跃率（1+2）/总设备 (实时计算)
	 * 3:设备接入时长平均（4/(1+2)）(实时计算)
	 * 4:新设备占比（1/(1+2)）(实时计算)
	 * @param dailyStatisticsDto 任何日期的dailyDto
	 * @return
	 */
	public DailyStatisticsDTO dailyStatisticsArith(DailyStatisticsDTO dailyStatisticsDto){
		if(dailyStatisticsDto == null) return null;
		
		//(1+2)
		long news_add_actives = dailyStatisticsDto.getNews() + dailyStatisticsDto.getActives();
		//1:设备接入次数平均（3/(1+2)）
		if(news_add_actives > 0){
			String accesscount_avg = String.valueOf(ArithHelper.div(dailyStatisticsDto.getAccesscount(),
					news_add_actives, 1));
			dailyStatisticsDto.setAccesscount_avg(accesscount_avg);
		}

		//2:设备活跃率（1+2）/总设备
		SystemStatisticsDTO systemStatisticsDto = SystemStatisticsHashService.getInstance().getStatistics();
		long total_handsets = 0;
		
		if(systemStatisticsDto != null && systemStatisticsDto.getHandsets() > 0){
			total_handsets = systemStatisticsDto.getHandsets();
		}else{
			//只有系统运行第一天可能会出现此情况
			total_handsets = news_add_actives;
		}
		
		if(total_handsets > 0){
			String active_pet = ArithHelper.percent(news_add_actives, total_handsets, 0);
			dailyStatisticsDto.setActive_pet(active_pet);
		}

		//3:设备接入时长平均（4/(1+2)）
		if(news_add_actives > 0){
			String duration_avg = String.valueOf(ArithHelper.div(dailyStatisticsDto.getDuration(),
					news_add_actives, 1));
			dailyStatisticsDto.setDuration_avg(duration_avg);
		}
		//4:新设备占比（1/(1+2)）
		if(news_add_actives > 0){
			String news_pet = ArithHelper.percent(dailyStatisticsDto.getNews(),news_add_actives, 0);
			dailyStatisticsDto.setNews_pet(news_pet);
		}
		
		return dailyStatisticsDto;
	}
	
	/**
	 * 以当日daily的统计数据与system的统计数据求和 
	 * 1：总移动设备接入次数 (当前数量+当日daily数量) 
	 * 2：总移动设备访问时长 (当前数量+当日daily数量)
	 * @param systemStatisticsDto
	 * @return
	 */
	public SystemStatisticsDTO systemStatisticsArith(SystemStatisticsDTO systemStatisticsDto){
		DailyStatisticsDTO dailyStatisticsDto = DailyStatisticsHashService.getInstance().getStatistics(
				BusinessKeyDefine.Statistics.DailyStatisticsHandsetInnerPrefixKey);
		if(dailyStatisticsDto == null) {
			return systemStatisticsDto;
		}
		return systemStatisticsArith(systemStatisticsDto, dailyStatisticsDto);
	}
	
	/**
	 * 以daily的统计数据与system的统计数据求和 
	 * 1：总移动设备接入次数 (当前数量+daily数量) 
	 * 2：总移动设备访问时长 (当前数量+daily数量)
	 * @param systemStatisticsDto
	 * @param dailyStatisticsDto
	 * @return
	 */
	public SystemStatisticsDTO systemStatisticsArith(SystemStatisticsDTO systemStatisticsDto, DailyStatisticsDTO dailyStatisticsDto){
		if(dailyStatisticsDto == null || systemStatisticsDto == null) return systemStatisticsDto;

		long total_handset_accesscount = dailyStatisticsDto.getAccesscount() + systemStatisticsDto.getHandset_accesscount();
		long total_handset_duration = dailyStatisticsDto.getDuration() + systemStatisticsDto.getHandset_duration();
		systemStatisticsDto.setHandset_accesscount(total_handset_accesscount);
		systemStatisticsDto.setHandset_duration(total_handset_duration);
		return systemStatisticsDto;
	}
	
	/**
	 * 以最新的数据生成系统统计dto 
	 * 1:总wifi设备数 通过查询wifi设备基础信息表来获取
	 * 2:总移动设备数 通过查询移动设备基础信息表来获取
	 * 3:在线wifi设备数 通过查询wifi设备基础信息表来获取
	 * 4:在线移动设备数 通过查询移动设备基础信息表来获取
	 * 生成的数据中不包含
	 * 1:总移动设备接入次数
	 * 2:总移动设备访问时长
	 * @return
	 */
	public SystemStatisticsDTO buildSystemStatisticsDto() throws IllegalAccessException, InvocationTargetException{
		SystemStatisticsDTO dto = new SystemStatisticsDTO();
		BeanUtils.copyProperties(dto, buildSystemStatisticsMap());
		return dto;
	}
	
	
	/**
	 * 以最新的数据生成系统统计map 
	 * 1:总wifi设备数 通过查询wifi设备基础信息表来获取
	 * 2:总移动设备数 通过查询移动设备基础信息表来获取
	 * 3:在线wifi设备数 通过查询wifi设备基础信息表来获取
	 * 4:在线移动设备数 通过查询移动设备基础信息表来获取
	 * 生成的数据中不包含
	 * 5:总移动设备接入次数
	 * 6:总移动设备访问时长
	 * @return
	 */
	public Map<String,String> buildSystemStatisticsMap(){
		Map<String,String> system_statistics_map = new HashMap<String,String>();
		system_statistics_map.put(SystemStatisticsDTO.Field_Devices, String.valueOf(wifiDeviceService.count()));
		system_statistics_map.put(SystemStatisticsDTO.Field_Handsets, String.valueOf(handsetDeviceService.count()));
		system_statistics_map.put(SystemStatisticsDTO.Field_OnlineDevices, String.valueOf(wifiDeviceService.countByOnline()));
		system_statistics_map.put(SystemStatisticsDTO.Field_OnlineHandsets, String.valueOf(handsetDeviceService.countByOnline()));
		return system_statistics_map;
	}
}
