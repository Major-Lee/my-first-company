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

/**
 * 承载业务 系统统计数据
 * 	1、总设备数
 *  2、总用户数
 * 	3、当前在线设备数
 *  4、当前在线用户数
 *  5、总平均访问时长
 * @author edmond
 *
 */
public class SystemStatisticsHashService extends AbstractRelationHashCache{
	
	private static class ServiceHolder{ 
		private static SystemStatisticsHashService instance =new SystemStatisticsHashService(); 
		//BeanUtils
	}
	/**
	 * 获取工厂单例
	 * @return
	 */
	public static SystemStatisticsHashService getInstance() { 
		return ServiceHolder.instance; 
	}
	private SystemStatisticsHashService(){
		
	}
	private static String generatePrefixKey(){
		return BusinessKeyDefine.Statistics.SystemStatistics;
		/*StringBuilder sb = new StringBuilder(BusinessKeyDefine.Statistics.SystemStatistics);
		return sb.toString();*/
	}
	/*private static String generateDailyPrefixKey(String businessKey){
		return generateDailyPrefixKey(businessKey,DateTimeHelper.formatDate( DateTimeHelper.FormatPattern5));
	}
	
	public Long incrStatistics(String businessKey,String field,long increment){
		return this.hincrby(generateDailyPrefixKey(businessKey), field, increment);
	}
	
	public SystemStatisticsDTO getSystemStatistics(){
		return getStatistics(generatePrefixKey());
	}*/
	public SystemStatisticsDTO getStatistics(){
		Map<String,String> all = this.hgetall(generatePrefixKey());
		SystemStatisticsDTO dto = new SystemStatisticsDTO();
		try {
			BeanUtils.copyProperties(dto,all);
		} catch (IllegalAccessException | InvocationTargetException e) {
			e.printStackTrace(System.out);
		}
		return dto;
	}
	
	public String initOrReset2Statistics(Map<String,String> map){
		return this.hmset(generatePrefixKey(), map);
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
