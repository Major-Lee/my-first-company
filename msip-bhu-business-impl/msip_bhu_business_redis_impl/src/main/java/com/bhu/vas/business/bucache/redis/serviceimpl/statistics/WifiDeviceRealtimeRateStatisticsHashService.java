package com.bhu.vas.business.bucache.redis.serviceimpl.statistics;

import java.util.HashMap;
import java.util.Map;

import redis.clients.jedis.JedisPool;

import com.bhu.vas.business.bucache.redis.serviceimpl.BusinessKeyDefine;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.RedisKeyEnum;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.RedisPoolManager;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.impl.AbstractRelationHashCache;
import com.smartwork.msip.cores.helper.StringHelper;

/**
 * 	用于设备的实时上下行速率
 * @author lawliet
 *
 */
public class WifiDeviceRealtimeRateStatisticsHashService extends AbstractRelationHashCache{
	
	private static class ServiceHolder{ 
		private static WifiDeviceRealtimeRateStatisticsHashService instance =new WifiDeviceRealtimeRateStatisticsHashService(); 
	}
	/**
	 * 获取工厂单例
	 * @return
	 */
	public static WifiDeviceRealtimeRateStatisticsHashService getInstance() { 
		return ServiceHolder.instance; 
	}
	
	private WifiDeviceRealtimeRateStatisticsHashService(){
	}
	
	private static final int exprie_seconds = 60 * 60;//1小时
	
	private static String generateKey(String mac){
		StringBuilder sb = new StringBuilder(BusinessKeyDefine.Statistics.WifiDeviceStatistics);
		sb.append(StringHelper.POINT_CHAR_GAP).append(BusinessKeyDefine.Statistics.WifiDeviceStatistics_RealtimeRate);
		sb.append(StringHelper.POINT_CHAR_GAP).append(mac);
		return sb.toString();
	}
	
	/**
	 * 设置实时上下行速率 过期时间1个小时
	 * @param mac
	 * @param tx_rate 上行速率
	 * @param rx_rate 下行速率
	 */
	public void addRate(String mac, String tx_rate, String rx_rate){
		String key = generateKey(mac);
		Map<String,String> rate_map = new HashMap<String,String>();
		rate_map.put("tx_rate", tx_rate);
		rate_map.put("rx_rate", rx_rate);
		super.hmset(key, rate_map);
		super.expire(key, exprie_seconds);
	}

	/**
	 * 获取实时上下行速率
	 * @param mac
	 * @return
	 */
	public Map<String,String> getRate(String mac){
		return super.hgetall(generateKey(mac));
	}
	
	@Override
	public String getRedisKey() {
		return null;
	}
	
	@Override
	public String getName() {
		return WifiDeviceRealtimeRateStatisticsHashService.class.getName();
	}
	
	@Override
	public JedisPool getRedisPool() {
		return RedisPoolManager.getInstance().getPool(RedisKeyEnum.STATISTICS);
	}
}
