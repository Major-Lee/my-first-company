package com.bhu.vas.business.bucache.redis.serviceimpl.handset;

import java.util.List;

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
public class HandsetStatisticsService extends AbstractRelationHashCache{
	
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
	
	
	public long online(String mac){
		return 0l;//this.hincrby(generateKey(), , value);
		//this.hset(generateKey(), wifiId, ctx);
	}
	
	public long offline(List<String> wifiIds, String ctx){
		return 0l;
		//String[][] keyAndFields = generateKeyAndFieldsAndValues(wifiIds,ctx);
		//this.pipelineHSet_diffKeyWithDiffFieldValue(keyAndFields[0], keyAndFields[1], keyAndFields[2]);
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
