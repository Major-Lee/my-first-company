package com.bhu.vas.business.bucache.redis.serviceimpl.statistics;

import redis.clients.jedis.JedisPool;

import com.bhu.vas.business.bucache.redis.serviceimpl.BusinessKeyDefine;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.RedisKeyEnum;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.RedisPoolManager;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.impl.AbstractRelationStringCache;
import com.smartwork.msip.cores.helper.StringHelper;

/**
 * 	用于展示设备地域分布图
 *  记录统计的地域wifi设备数量信息
 	(json) example:[{"region":"北京市","count":1000},{"region":"上海市","count":2000}]
 * @author lawliet
 *
 */
public class WifiDeviceCountRegionStatisticsStringService extends AbstractRelationStringCache{
	
	private static class ServiceHolder{ 
		private static WifiDeviceCountRegionStatisticsStringService instance =new WifiDeviceCountRegionStatisticsStringService(); 
	}
	/**
	 * 获取工厂单例
	 * @return
	 */
	public static WifiDeviceCountRegionStatisticsStringService getInstance() { 
		return ServiceHolder.instance; 
	}
	
	private WifiDeviceCountRegionStatisticsStringService(){
	}
	
	private static final int exprie_seconds = 60 * 60;//1小时
	private static final String WifiDeviceCountRegionKey = "CRP";
	
	private static String generateKey(){
		StringBuilder sb = new StringBuilder(BusinessKeyDefine.Statistics.WifiDeviceStatistics);
		sb.append(StringHelper.POINT_CHAR_GAP).append(WifiDeviceCountRegionKey);
		return sb.toString();
	}
	
	public void setWifiDeviceCountRegion(String value){
		String key = generateKey();
		super.set(key, value);
		super.expire(key, exprie_seconds);
	}

	public String getWifiDeviceCountRegion(){
		return super.get(generateKey());
	}

	
	@Override
	public String getRedisKey() {
		return null;
	}
	
	@Override
	public String getName() {
		return WifiDeviceCountRegionStatisticsStringService.class.getName();
	}
	
	@Override
	public JedisPool getRedisPool() {
		return RedisPoolManager.getInstance().getPool(RedisKeyEnum.STATISTICS);
	}
}
