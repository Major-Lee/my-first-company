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
 *  用于展示最繁忙的wifi设备TOP5
 *  ZSET 
 *  	key：固定 
 *  	score 总接入用户数
 *  	value wifiId
 *  包括	
 *  	聊天离线消息
 * @author lawliet
 *
 */
public class WifiDeviceMaxHandsetStatisticsSortedSetService extends AbstractRelationSortedSetCache{
	
	private static class ServiceHolder{ 
		private static WifiDeviceMaxHandsetStatisticsSortedSetService instance =new WifiDeviceMaxHandsetStatisticsSortedSetService(); 
	}
	/**
	 * 获取工厂单例
	 * @return
	 */
	public static WifiDeviceMaxHandsetStatisticsSortedSetService getInstance() { 
		return ServiceHolder.instance; 
	}
	
	private WifiDeviceMaxHandsetStatisticsSortedSetService(){
	}
	
	private static final String WifiDeviceMaxHandsetPrefixKey = "MHP";
	
	private static String generateKey(){
		StringBuilder sb = new StringBuilder(BusinessKeyDefine.Statistics.WifiDeviceStatistics);
		sb.append(StringHelper.POINT_CHAR_GAP).append(WifiDeviceMaxHandsetPrefixKey);
		return sb.toString();
	}
	
	public void addWifiDeviceMaxHandset(String wifiId, long handset_count){
		super.zadd(generateKey(), handset_count, wifiId);
	}
	
	public void clearWifiDeviceMaxHandset(){
		super.del(generateKey());
	}
	
	/**
	 * 返回所有统计的最繁忙的设备列表
	 */
	public Set<String> allWifiDeviceMaxHandset(){
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
		return WifiDeviceMaxHandsetStatisticsSortedSetService.class.getName();
	}
	
	@Override
	public JedisPool getRedisPool() {
		return RedisPoolManager.getInstance().getPool(RedisKeyEnum.STATISTICS);
	}
}
