package com.bhu.vas.business.bucache.redis.serviceimpl.statistics;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.bhu.vas.api.dto.statistics.DeviceStateStatisticsDTO;
import com.bhu.vas.api.dto.statistics.UserStateStatisticsDTO;
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
public class UserStateStatisticsHashService extends AbstractRelationHashCache {

	private static class ServiceHolder {
		private static UserStateStatisticsHashService instance = new UserStateStatisticsHashService();
	}

	/**
	 * 获取工厂单例
	 * 
	 * @return
	 */
	public static UserStateStatisticsHashService getInstance() {
		return ServiceHolder.instance;
	}
	
	private static String generateKey(String fragment, String buPrefixKey) {
		StringBuilder sb = new StringBuilder(BusinessKeyDefine.Statistics.UserStateStatistics);
		sb.append(buPrefixKey).append(fragment);
		return sb.toString();
	}
	
	//当前时段统计信息不存在则新建，否则返回已存在数据进行比较更新
	public String setOrGetValue(String fragment, String buPrefixKey, String field, UserStateStatisticsDTO dto) {

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
	
	public Map<String, String> fetchAll(String fragment, String buPrefixKey){
		return this.hgetall(generateKey(fragment, buPrefixKey));
	}
	
	public void timeIntervalAllSet(Date date, UserStateStatisticsDTO dto) {
		
		List<String> fragments = DateTimeExtHelper.generateServalDateFormat(date);
		
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
				DateTimeExtHelper.getWeekDay(date)+"", dto);
		
		if (weekilyValue != null) {
			update(fragments.get(DateTimeExtHelper.YEAR_WHICH_WEEK),
					BusinessKeyDefine.Statistics.FragmentOnlineWeeklySuffixKey,
					DateTimeExtHelper.getWeekDay(date)+"", dto,
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
	public void update(String fragment, String buPrefixKey, String field, UserStateStatisticsDTO dto, String value) {
		UserStateStatisticsDTO oldDto = JsonHelper.getDTO(value, UserStateStatisticsDTO.class);
		UserStateStatisticsDTO newDto = upDateStatisticsDto(dto, oldDto);
		// update
		this.hset(generateKey(fragment, buPrefixKey), field, JsonHelper.getJSONString(newDto));
	}
	
	//对比信息，返回各个值较大的信息dto
	public UserStateStatisticsDTO upDateStatisticsDto(UserStateStatisticsDTO dto, UserStateStatisticsDTO oldDto) {
		UserStateStatisticsDTO newDto = new UserStateStatisticsDTO();
		newDto.setCountsUser(dto.getCountsUser() > oldDto.getCountsUser() ? dto.getCountsUser(): oldDto.getCountsUser());
		newDto.setOnline(dto.getOnline() > oldDto.getOnline() ? dto.getOnline() : dto.getOnline());
		newDto.setNewInc(dto.getNewInc() > oldDto.getNewInc() ? dto.getNewInc() : dto.getNewInc());
		newDto.setCurrentUser(dto.getCurrentUser() > oldDto.getCurrentUser() ? dto.getCurrentUser() : dto.getCurrentUser());
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
