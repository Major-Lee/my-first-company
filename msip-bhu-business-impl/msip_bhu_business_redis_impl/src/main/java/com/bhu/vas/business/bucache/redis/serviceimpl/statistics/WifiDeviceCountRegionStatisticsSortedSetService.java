package com.bhu.vas.business.bucache.redis.serviceimpl.statistics;

import java.util.Collections;
import java.util.Set;

import redis.clients.jedis.JedisPool;

import com.bhu.vas.business.bucache.redis.serviceimpl.BusinessKeyDefine;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.RedisKeyEnum;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.RedisPoolManager;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.impl.AbstractRelationSortedSetCache;
import com.smartwork.msip.cores.helper.StringHelper;

/**
 *  记录统计的地域wifi设备数量信息
 *  ZSET 
 *  	key：固定 
 *  	score 该地域的wifi设备数量
 *  	value 该地域的wifi设备数量信息 (json) example:{"count":1000,"region":"北京"}
 *  包括	
 *  	聊天离线消息
 * @author lawliet
 *
 */
public class WifiDeviceCountRegionStatisticsSortedSetService extends AbstractRelationSortedSetCache{
	
	private static class ServiceHolder{ 
		private static WifiDeviceCountRegionStatisticsSortedSetService instance =new WifiDeviceCountRegionStatisticsSortedSetService(); 
	}
	/**
	 * 获取工厂单例
	 * @return
	 */
	public static WifiDeviceCountRegionStatisticsSortedSetService getInstance() { 
		return ServiceHolder.instance; 
	}
	
	private WifiDeviceCountRegionStatisticsSortedSetService(){
	}
	
	private static final String WifiDeviceCountRegionPrefixKey = "CRP";
	
	private static String generateKey(){
		StringBuilder sb = new StringBuilder(BusinessKeyDefine.Statistics.WifiDeviceStatistics);
		sb.append(StringHelper.POINT_CHAR_GAP).append(WifiDeviceCountRegionPrefixKey);
		return sb.toString();
	}
	
	public void addWifiDeviceCountRegion(String value, long count){
		super.zadd(generateKey(), count, value);
	}
	
	public void clearWifiDeviceCountRegion(){
		super.del(generateKey());
	}
	
	/**
	 * 返回所有统计的地域wifi设备数量信息
	 */
	public Set<String> allWifiDeviceCountRegion(){
		String key = generateKey();
		long count = super.zcard(key);
		if(count > 0){
			return super.zrevrange(generateKey(), 0, (int)count);
		}
		return Collections.emptySet();
	}

	
	@Override
	public String getRedisKey() {
		return null;
	}
	
	@Override
	public String getName() {
		return WifiDeviceCountRegionStatisticsSortedSetService.class.getName();
	}
	
	@Override
	public JedisPool getRedisPool() {
		return RedisPoolManager.getInstance().getPool(RedisKeyEnum.STATISTICS);
	}
}
