package com.bhu.vas.business.bucache.redis.serviceimpl.statistics;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;

import redis.clients.jedis.JedisPool;

import com.bhu.vas.api.dto.redis.SystemStatisticsDTO;
import com.bhu.vas.business.bucache.redis.serviceimpl.BusinessKeyDefine;
import com.bhu.vas.business.bucache.redis.serviceimpl.updown.UserBuinessMarkHashService;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.RedisKeyEnum;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.RedisPoolManager;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.impl.AbstractRelationHashCache;

/**
 * 承载业务 系统统计数据 通过后台定时程序生成这些数据
 * 1:总wifi设备数
 * 2:总移动设备数
 * 3:在线wifi设备数
 * 4:在线移动设备数
 * 5:总移动设备接入次数
 * 6:总移动设备访问时长
 * @author tangzichao
 *
 */
public class SystemStatisticsHashService extends AbstractRelationHashCache{
	
	private static class ServiceHolder{ 
		private static SystemStatisticsHashService instance =new SystemStatisticsHashService(); 
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
	}

	public SystemStatisticsDTO getStatistics(){
		Map<String,String> all = this.hgetall(generatePrefixKey());
		if(all != null) return null;
		
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
	
	@SuppressWarnings("unchecked")
	public String initOrReset2Statistics(SystemStatisticsDTO dto){
		Map<String,String> all = null;
		try {
			all = BeanUtils.describe(dto);
		} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
			e.printStackTrace(System.out);
		}
		return initOrReset2Statistics(all);
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
		return RedisPoolManager.getInstance().getPool(RedisKeyEnum.STATISTICS);
	}
}
