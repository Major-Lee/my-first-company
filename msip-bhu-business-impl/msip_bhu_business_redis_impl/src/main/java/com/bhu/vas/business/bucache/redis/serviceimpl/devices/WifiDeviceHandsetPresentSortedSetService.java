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
 *  
 * 1：移动设备上线新增，下线删除，sync更新数据
 * 2：由于移动设备sync消息是多条单item消息，后台服务无法准确的去掉已经下线的移动设备数据，需要后台定时程序来进行维护
 * 例如 后台定时程序每2个小时会以2小时前的时间作为最大阀值，来进行zremrangeByScore来进行删除已下线的移动设备数据
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
	
	private static final long Condition_Offline_TimeGap  = 1*24*60*60*1000l;
	
	public Long presentNotOfflineSize(String wifiId){
		//this.zcard(generateKey(wifiId));
		long ts = System.currentTimeMillis();
		return this.zcount(generateKey(wifiId), ts-Condition_Offline_TimeGap, ts);
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
