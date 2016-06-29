package com.bhu.vas.business.bucache.redis.serviceimpl.statistics;

import java.util.List;

import com.bhu.vas.api.dto.statistics.DeviceStateStatisticsDTO;
import com.bhu.vas.business.bucache.redis.serviceimpl.BusinessKeyDefine;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.RedisKeyEnum;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.RedisPoolManager;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.impl.AbstractRelationHashCache;
import com.smartwork.msip.cores.helper.DateTimeExtHelper;
import com.smartwork.msip.cores.helper.JsonHelper;

import redis.clients.jedis.JedisPool;

/**
 * 设备状态统计 （总设备数、当前在线、今日新增、昨日新增、最大在线数、今日活跃率）
 * 
 * @author xiaowei
 *
 */
public class DeviceStateStatisticsHashService extends AbstractRelationHashCache {

	private static class ServiceHolder {
		private static DeviceStateStatisticsHashService instance = new DeviceStateStatisticsHashService();
	}

	/**
	 * 获取工厂单例
	 * 
	 * @return
	 */
	public static DeviceStateStatisticsHashService getInstance() {
		return ServiceHolder.instance;
	}
	
	private static String generateKey(String fragment, String buPrefixKey) {
		StringBuilder sb = new StringBuilder(BusinessKeyDefine.Statistics.DeviceStateStatistics);
		sb.append(buPrefixKey).append(fragment);
		return sb.toString();
	}
	
	//当前时段统计信息不存在则新建，否则返回已存在数据进行比较更新
	public String setOrGetValue(String fragment, String buPrefixKey, String field, DeviceStateStatisticsDTO dto) {

		String value = null;
		boolean flag = this.hexists(generateKey(fragment, buPrefixKey), field);
		if (!flag) {
			this.hset(generateKey(fragment, buPrefixKey), field, JsonHelper.getJSONString(dto));
		} else {
			value = this.hget(generateKey(fragment, buPrefixKey), field);
		}
		return value;
	}
	
	public String fetchStatistics(String fragment, String buPrefixKey, String field){
		return this.hget(generateKey(fragment, buPrefixKey), field);
	}
	
	public void timeIntervalAllSet(List<String> fragments, DeviceStateStatisticsDTO dto) {
		
		//每日
		String dailyValue = setOrGetValue(fragments.get(DateTimeExtHelper.YEAR_MONTH_DD),
				BusinessKeyDefine.Statistics.FragmentOnlineDailySuffixKey, 
				fragments.get(DateTimeExtHelper.HH), dto);

		if (dailyValue != null) {
			update(fragments.get(DateTimeExtHelper.YEAR_MONTH_DD),
					BusinessKeyDefine.Statistics.FragmentOnlineDailySuffixKey, fragments.get(DateTimeExtHelper.HH), dto,
					dailyValue);
		}
		//每周
		String weekilyValue = setOrGetValue(fragments.get(DateTimeExtHelper.YEAR_WHICH_WEEK),
				BusinessKeyDefine.Statistics.FragmentOnlineWeeklySuffixKey,
				fragments.get(DateTimeExtHelper.YEAR_MONTH_DD), dto);
		
		if (weekilyValue != null) {
			update(fragments.get(DateTimeExtHelper.YEAR_WHICH_WEEK),
					BusinessKeyDefine.Statistics.FragmentOnlineWeeklySuffixKey,
					fragments.get(DateTimeExtHelper.YEAR_MONTH_DD), dto,
					dailyValue);
		}
		
		//每月
		String monthlyValue = setOrGetValue(fragments.get(DateTimeExtHelper.YEAR_MONTH),
				BusinessKeyDefine.Statistics.FragmentOnlineMonthlySuffixKey, 
				fragments.get(DateTimeExtHelper.YEAR_MONTH_DD),dto);
		
		if (monthlyValue != null) {
			update(fragments.get(DateTimeExtHelper.YEAR_MONTH),
					BusinessKeyDefine.Statistics.FragmentOnlineMonthlySuffixKey, 
					fragments.get(DateTimeExtHelper.YEAR_MONTH_DD),dto,
					monthlyValue);
		}
		
	}
	
	//更新当前时段信息
	public void update(String fragment, String buPrefixKey, String field, DeviceStateStatisticsDTO dto, String value) {
		DeviceStateStatisticsDTO oldDto = JsonHelper.getDTO(value, DeviceStateStatisticsDTO.class);
		DeviceStateStatisticsDTO newDto = upDateStatisticsDto(dto, oldDto);
		// update
		this.hset(generateKey(fragment, buPrefixKey), field, JsonHelper.getJSONString(newDto));
	}
	
	//对比信息，返回各个值较大的信息dto
	public DeviceStateStatisticsDTO upDateStatisticsDto(DeviceStateStatisticsDTO dto, DeviceStateStatisticsDTO oldDto) {
		DeviceStateStatisticsDTO newDto = new DeviceStateStatisticsDTO();
		newDto.setCountsDevices(dto.getCountsDevices() > oldDto.getCountsDevices() ? dto.getCountsDevices(): oldDto.getCountsDevices());
		newDto.setOnline(dto.getOnline() > oldDto.getOnline() ? dto.getOnline() : dto.getOnline());
		newDto.setNewInc(dto.getNewInc() > oldDto.getNewInc() ? dto.getNewInc() : dto.getNewInc());
		newDto.setLiveness(dto.getLiveness() > oldDto.getLiveness() ? dto.getLiveness() : dto.getLiveness());
		newDto.setOnline_max(
				dto.getOnline_max() > oldDto.getOnline_max() ? dto.getOnline_max() : oldDto.getOnline_max());
		return newDto;
	}

	@Override
	public String getName() {
		return null;
	}

	@Override
	public String getRedisKey() {
		return null;
	}

	@Override
	public JedisPool getRedisPool() {
		return RedisPoolManager.getInstance().getPool(RedisKeyEnum.STATISTICS);
	}
}
