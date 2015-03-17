package com.bhu.vas.business.bucache.redis.serviceimpl.statistics;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;

import redis.clients.jedis.JedisPool;

import com.bhu.vas.business.bucache.redis.serviceimpl.BusinessKeyDefine;
import com.bhu.vas.business.bucache.redis.serviceimpl.updown.UserBuinessMarkHashService;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.RedisKeyEnum;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.RedisPoolManager;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.impl.AbstractRelationHashCache;
import com.smartwork.msip.cores.helper.DateTimeHelper;

/**
 * 承载业务 用户和设备
 * 	1、用户每天的新增A、活跃用户（非今天注册的但今天登录了的用户）B、启动次数C、使用时长（分钟）D
 *  2、人均启动次数F=（启动次数/(A+B)） 日活跃率 G=（A+B）/总用户
 * 	2、文章评星业务
 * @author edmond
 *
 */
public class DailyStatisticsHashService extends AbstractRelationHashCache{
	
	private static class ServiceHolder{ 
		private static DailyStatisticsHashService instance =new DailyStatisticsHashService(); 
		//BeanUtils
	}
	/**
	 * 获取工厂单例
	 * @return
	 */
	public static DailyStatisticsHashService getInstance() { 
		return ServiceHolder.instance; 
	}
	private DailyStatisticsHashService(){
		
	}
	private static String generateDailyPrefixKey(String businessKey,String date){
		StringBuilder sb = new StringBuilder(BusinessKeyDefine.Statistics.DailyStatistics);
		sb.append(businessKey).append(date);
		return sb.toString();
	}
	private static String generateDailyPrefixKey(String businessKey){
		return generateDailyPrefixKey(businessKey,DateTimeHelper.formatDate( DateTimeHelper.FormatPattern5));
	}
	
	public Long incrStatistics(String businessKey,String field,long increment){
		return this.hincrby(generateDailyPrefixKey(businessKey), field, increment);
	}
	
	public DailyStatisticsDTO getStatistics(String businessKey){
		return getStatistics(businessKey,DateTimeHelper.formatDate( DateTimeHelper.FormatPattern5));
	}
	public DailyStatisticsDTO getStatistics(String businessKey,String date){
		Map<String,String> all = this.hgetall(generateDailyPrefixKey(businessKey,date));
		DailyStatisticsDTO dto = new DailyStatisticsDTO();
		try {
			BeanUtils.copyProperties(dto,all);
		} catch (IllegalAccessException | InvocationTargetException e) {
			e.printStackTrace(System.out);
		}
		return dto;
	}
	
	public String initOrReset2Statistics(String businessKey,String date,Map<String,String> map){
		return this.hmset(generateDailyPrefixKey(businessKey,date), map);
	}
	
	@Override
	public String getRedisKey() {
		return null;
	}
	@Override
	public String getName() {
		return UserBuinessMarkHashService.class.getName();
	}
	@Override
	public JedisPool getRedisPool() {
		return RedisPoolManager.getInstance().getPool(RedisKeyEnum.PRESENT);
	}
	
		
	public static void main(String[] argv){
		Map<String,Object> all = new HashMap<String,Object>();
		all.put(DailyStatisticsDTO.Field_News, "55");
		all.put(DailyStatisticsDTO.Field_Actives, 65);
		all.put(DailyStatisticsDTO.Field_Startups, "1055");
		all.put(DailyStatisticsDTO.Field_Times, "55009");
		DailyStatisticsDTO dto = new DailyStatisticsDTO();
		try {
			BeanUtils.copyProperties(dto,all);
		} catch (IllegalAccessException | InvocationTargetException e) {
			e.printStackTrace(System.out);
		}
		
		System.out.println(dto.getNews());
		System.out.println(dto.getActives());
		System.out.println(dto.getTimes());
		System.out.println(dto.getStartups());
	}
}
