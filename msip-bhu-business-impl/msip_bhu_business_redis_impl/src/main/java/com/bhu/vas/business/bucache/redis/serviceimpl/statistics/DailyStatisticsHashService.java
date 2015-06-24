package com.bhu.vas.business.bucache.redis.serviceimpl.statistics;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;

import redis.clients.jedis.JedisPool;

import com.bhu.vas.api.dto.redis.DailyStatisticsDTO;
import com.bhu.vas.business.bucache.redis.serviceimpl.BusinessKeyDefine;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.RedisKeyEnum;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.RedisPoolManager;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.impl.AbstractRelationHashCache;
import com.smartwork.msip.cores.helper.DateTimeHelper;

/**
 * wifi设备和移动设备 用于显示daily的统计数据
 * 
 * 当日的数据有其中有增量的数据，直接可用于显示
 * 有些数据需要显示的时候进行实时计算,当日内不存入redis中
 * 每晚后台定时程序会计算出当日没有存储的数据，再存入redis中以备后续访问
 * @author tangzichao
 *
 */
public class DailyStatisticsHashService extends AbstractRelationHashCache{
	
	private static class ServiceHolder{ 
		private static DailyStatisticsHashService instance =new DailyStatisticsHashService(); 
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
		if(all == null) return null;
		
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
		return DailyStatisticsHashService.class.getName();
	}
	@Override
	public JedisPool getRedisPool() {
		return RedisPoolManager.getInstance().getPool(RedisKeyEnum.PRESENT);
	}
	
		
	public static void main(String[] argv){
		Map<String,Object> all = new HashMap<String,Object>();
		all.put(DailyStatisticsDTO.Field_News, "55");
		all.put(DailyStatisticsDTO.Field_Actives, 65);
		System.out.println(all.keySet());
		DailyStatisticsDTO dto = new DailyStatisticsDTO();
		try {
			BeanUtils.copyProperties(dto,all);
		} catch (IllegalAccessException | InvocationTargetException e) {
			e.printStackTrace(System.out);
		}
		
		System.out.println(dto.getNews());
		System.out.println(dto.getActives());
		
	}
}
