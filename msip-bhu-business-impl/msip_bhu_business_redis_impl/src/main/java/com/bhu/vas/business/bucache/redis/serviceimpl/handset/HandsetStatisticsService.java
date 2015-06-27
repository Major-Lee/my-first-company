package com.bhu.vas.business.bucache.redis.serviceimpl.handset;

import java.util.Map;

import org.apache.commons.lang.StringUtils;

import redis.clients.jedis.JedisPool;

import com.bhu.vas.business.bucache.redis.serviceimpl.BusinessKeyDefine;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.RedisKeyEnum;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.RedisPoolManager;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.impl.AbstractRelationHashCache;

/**
 * 设备终端数量增量计数
 * 在线终端数量
 * 所有终端数量
 * @author edmond
 *
 */
public class HandsetStatisticsService extends AbstractRelationHashCache {
	
	private static class ServiceHolder{ 
		private static HandsetStatisticsService instance =new HandsetStatisticsService(); 
	}
	/**
	 * 获取工厂单例
	 * @return
	 */
	public static HandsetStatisticsService getInstance() { 
		return ServiceHolder.instance; 
	}
	
	private HandsetStatisticsService(){
	}
	
	private static final String Field_Online = "O";
	private static final String Field_Total = "T";
	
	private static String generateKey(){
		return BusinessKeyDefine.HandsetPresent.StatisticsPrefixKey;
	}
	
	public long online(boolean isOnline){
		return online(isOnline,1);
	}
	public long online(boolean isOnline,int incr){
		return this.hincrby(generateKey(), Field_Online, isOnline?incr:-incr);
	}
	
	public int[] statistics(){
		Map<String, String> all = this.hgetall(generateKey());
		int[] result = new int[2];
		String online = all.get(Field_Online);
		if(StringUtils.isNotEmpty(online)) 
			result[0] = Integer.parseInt(online);
		else
			result[0] = 0;
		String total = all.get(Field_Total);
		if(StringUtils.isNotEmpty(total)) 
			result[1] = Integer.parseInt(total);
		else
			result[1] = 0;
		return result;
	}
	
	@Override
	public String getRedisKey() {
		return null;
	}
	
	@Override
	public String getName() {
		return HandsetStatisticsService.class.getName();
	}
	@Override
	public JedisPool getRedisPool() {
		return RedisPoolManager.getInstance().getPool(RedisKeyEnum.HANDSETPRESENT);
	}
}
