package com.bhu.vas.business.bucache.redis.serviceimpl.statistics;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Map;

import redis.clients.jedis.JedisPool;

import com.bhu.vas.business.bucache.redis.serviceimpl.BusinessKeyDefine;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.RedisKeyEnum;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.RedisPoolManager;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.impl.AbstractRelationHashCache;

import org.apache.commons.lang.StringUtils;
/**
 * 设备信息缓存【设备在线数，设备总数，SSID关联总次数，SSID连接总人数】
 * @author Jason
 *
 */
public class DeviceStatisticsHashService extends AbstractRelationHashCache{
	
	private static DeviceStatisticsHashService instance = null;
	public static DeviceStatisticsHashService getInstance(){
		if(instance == null){
			instance = new DeviceStatisticsHashService();
		}
		return instance;
	}
	private DeviceStatisticsHashService(){
		
	}
	
	/**
	 * key
	 * @param dateTime 日期  如：2016-07-05
	 * @return
	 */
	private static String generatePrefixKey(String dateTime){
		StringBuilder sb = new StringBuilder(BusinessKeyDefine.Statistics.UserDeviceStatistics);
		sb.append(dateTime);
		return sb.toString();
	}
	
	public long deviceMacHset(String key,String field,String value){
		Long n = this.hset(generatePrefixKey(key), field, value);
		return n;
	}
	public String deviceMacHget(String key,String field){
		String result = StringUtils.EMPTY;
		result = this.hget(generatePrefixKey(key), field);
		return result;
	}
	
	public Map<String, String> getDeviceMacByKey(String key){
		return this.hgetall(key);
	}
	
	@Override
	public String getName() {
		return DeviceStatisticsHashService.class.getName();
	}

	@Override
	public String getRedisKey() {
		return null;
	}

	@Override
	public JedisPool getRedisPool() {
		return RedisPoolManager.getInstance().getPool(RedisKeyEnum.STATISTICS);
	}
}
