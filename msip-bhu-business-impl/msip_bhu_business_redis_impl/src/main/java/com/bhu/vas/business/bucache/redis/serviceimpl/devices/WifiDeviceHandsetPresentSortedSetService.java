package com.bhu.vas.business.bucache.redis.serviceimpl.devices;

import java.util.Collections;
import java.util.Set;

import org.springframework.util.StringUtils;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Tuple;

import com.bhu.vas.business.bucache.redis.serviceimpl.BusinessKeyDefine;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.RedisKeyEnum;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.RedisPoolManager;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.impl.AbstractRelationSortedSetCache;
import com.smartwork.msip.cores.helper.StringHelper;

/**
 *  wifi设备对应的在线移动设备列表
 *  ZSET 
 *  	key：wifiId 
 *  	score 移动设备上线时间
 *  	value 移动设备的mac
 *  包括	
 *  	聊天离线消息
 * @author lawliet
 *
 */
public class WifiDeviceHandsetPresentSortedSetService extends AbstractRelationSortedSetCache{
	
	private static class ServiceHolder{ 
		private static WifiDeviceHandsetPresentSortedSetService instance =new WifiDeviceHandsetPresentSortedSetService(); 
	}
	/**
	 * 获取工厂单例
	 * @return
	 */
	public static WifiDeviceHandsetPresentSortedSetService getInstance() { 
		return ServiceHolder.instance; 
	}
	
	private WifiDeviceHandsetPresentSortedSetService(){
	}
	
	private static String generateKey(String wifiId){
		StringBuilder sb = new StringBuilder(BusinessKeyDefine.Present.WifiDeviceHandsetPresentPrefixKey);
		sb.append(StringHelper.POINT_CHAR_GAP).append(wifiId);
		return sb.toString();
	}
	
	public void addPresent(String wifiId, String handsetId, long login_at){
		super.zadd(generateKey(wifiId), login_at, handsetId);
	}
	
	public void removePresent(String wifiId, String handsetId){
		super.zrem(generateKey(wifiId), handsetId);
	}
	
	public void clearPresents(String wifiId){
		super.del(generateKey(wifiId));
	}
	
	public void clearPresents(String wifiId, long max_login_at){
		super.zremrangeByScore(generateKey(wifiId), 0, max_login_at);
	}
	
	/**
	 * 按移动设备接入时间，从大到小排序
	 * @param wifiId wifi mac
	 * @param start
	 * @param size
	 * @return 包含移动设备的接入时间的set集合
	 */
	public Set<Tuple> fetchPresents(String wifiId,int start,int size){
		if(StringUtils.isEmpty(wifiId)) return Collections.emptySet();
		return super.zrevrangeWithScores(generateKey(wifiId), start, start+size-1);
	}

	
	@Override
	public String getRedisKey() {
		return null;
	}
	
	@Override
	public String getName() {
		return WifiDeviceHandsetPresentSortedSetService.class.getName();
	}
	
	@Override
	public JedisPool getRedisPool() {
		return RedisPoolManager.getInstance().getPool(RedisKeyEnum.PRESENT);
	}
}
