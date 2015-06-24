package com.bhu.vas.business.bucache.redis.serviceimpl.marker;

import org.apache.commons.lang.StringUtils;

import redis.clients.jedis.JedisPool;

import com.bhu.vas.business.bucache.redis.serviceimpl.BusinessKeyDefine;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.RedisKeyEnum;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.RedisPoolManager;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.impl.AbstractRelationHashCache;
import com.smartwork.msip.cores.helper.HashAlgorithmsHelper;
import com.smartwork.msip.cores.helper.StringHelper;

/**
 * 设备、用户 痕迹 等 通用标记类 具体标记的内容由业务定制
 * 数据量很大 所以需要散列hash
 * 并且通过程序可以获取所有的数据进行维护清除
 * field 存储 具体的业务 value 具体业务标记值
 * @author Edmond
 *
 */
public class BusinessMarkerService extends AbstractRelationHashCache{
	private static class ServiceHolder{ 
		private static BusinessMarkerService instance =new BusinessMarkerService(); 
	}
	public static final int hasPrimeValue = 2000;
	/**
	 * 获取工厂单例
	 * @return
	 */
	public static BusinessMarkerService getInstance() { 
		return ServiceHolder.instance; 
	}
	private BusinessMarkerService(){
		
	}
	private static String generateMarkPrefixKey(String businessKey,String businessField){
		int hashvalue = HashAlgorithmsHelper.additiveHash(businessField, hasPrimeValue);
		StringBuilder sb = new StringBuilder(BusinessKeyDefine.Present.MarkPrefixKey);
		sb.append(StringHelper.POINT_CHAR_GAP).append(businessKey);
		sb.append(StringHelper.POINT_CHAR_GAP).append(String.format("%04d", hashvalue));
		return sb.toString();
	}
	
	//设备使用状况查询标记 时间间隔内 不继续发起请求 timeout则继续发起请求的标记---------------------------------------------------
	private static final String DeviceUsedStatusPrefixKey = "DUS";
	private static final long timeout = 1800*1000;//半个小时
	public void deviceUsedMarker(String mac){
		this.hset(generateMarkPrefixKey(DeviceUsedStatusPrefixKey,mac), mac, String.valueOf(System.currentTimeMillis()));
	}

	public boolean needNewRequestAndMarker(String mac){
		String key = generateMarkPrefixKey(DeviceUsedStatusPrefixKey,mac);
		String value = this.hget(key, mac);
		long current = System.currentTimeMillis();
		if(StringUtils.isNotEmpty(value)){
			long previous_time = Long.parseLong(value);
			if( current - previous_time < timeout){
				return false;
			}
		}
		this.hset(key, mac, String.valueOf(current));
		return true;
	}
	
	
	@Override
	public String getRedisKey() {
		return null;
	}
	@Override
	public String getName() {
		return BusinessMarkerService.class.getName();
	}
	@Override
	public JedisPool getRedisPool() {
		return RedisPoolManager.getInstance().getPool(RedisKeyEnum.PRESENT);
	}
}
