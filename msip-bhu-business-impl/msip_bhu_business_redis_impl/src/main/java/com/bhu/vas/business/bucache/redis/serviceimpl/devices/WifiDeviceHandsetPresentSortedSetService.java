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
 *  	score 在线状态基础数值(在线是100亿，100,0000,0000 离线是0) + 终端下行速率(百兆每秒速率是亿级别)
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
	//在线初始score数值 100亿 
	public static final double OnlineBaseScore = 10000000000d;
	
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
		return this.zcard(generateKey(wifiId));
		/*long ts = System.currentTimeMillis();
		return this.zcount(generateKey(wifiId), ts-Condition_Offline_TimeGap, ts);*/
	}
	
	/**
	 * 获取该设备的在线终端数量
	 * @param wifiId
	 * @return
	 */
	public Long presentOnlineSize(String wifiId){
		return super.zcount(generateKey(wifiId), OnlineBaseScore, Long.MAX_VALUE);
	}
	
	public Long presentOfflineSize(String wifiId){
		return super.zcount(generateKey(wifiId), 0, (OnlineBaseScore-1));
	}
	
	public long addOnlinePresent(String wifiId, String handsetId, long rx_rate){
		return super.zadd(generateKey(wifiId), OnlineBaseScore+rx_rate, handsetId);
	}
	
	public long addOfflinePresent(String wifiId, String handsetId, long rx_rate){
		return super.zadd(generateKey(wifiId), rx_rate, handsetId);
	}
	
	public Set<Tuple> fetchOnlinePresents(String wifiId,int start,int size){
		if(StringUtils.isEmpty(wifiId)) return Collections.emptySet();
		return super.zrevrangeByScoreWithScores(generateKey(wifiId), OnlineBaseScore, Long.MAX_VALUE, start, size);
	}
	
	public Set<Tuple> fetchOfflinePresents(String wifiId,int start,int size){
		if(StringUtils.isEmpty(wifiId)) return Collections.emptySet();
		return super.zrevrangeByScoreWithScores(generateKey(wifiId), 0, (OnlineBaseScore-1), start, size);
	}
	
	public Set<String> fetchPresents(String wifiId){
		if(StringUtils.isEmpty(wifiId)) return Collections.emptySet();
		//return super.zrevrangeWithScores(generateKey(wifiId), 0, 10);
		return super.zrevrangeByScore(generateKey(wifiId), 0, 10000, 0, 10);
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
	
	public static void main(String[] args){
		String wifiId = "tt:tt:tt:tt:tt:t";
		for(int i = 0;i<20;i++){
			WifiDeviceHandsetPresentSortedSetService.getInstance().addOnlinePresent(wifiId, 
					"hh:hh:hh:hh:hh:h".concat(String.valueOf(i)), 1024+i);
		}
		for(int i = 0;i<20;i++){
			WifiDeviceHandsetPresentSortedSetService.getInstance().addOfflinePresent(wifiId, 
					"oo:oo:oo:oo:oo:o".concat(String.valueOf(i)), 1024+i);
		}
		Set<Tuple> result = WifiDeviceHandsetPresentSortedSetService.getInstance().fetchOnlinePresents(wifiId, 0, 10);
		for(Tuple tuple : result){
			System.out.println("online="+tuple.getElement() + "=" + tuple.getScore()+"="+(tuple.getScore() - OnlineBaseScore));
		}
		result = WifiDeviceHandsetPresentSortedSetService.getInstance().fetchOfflinePresents(wifiId, 0, 10);
		for(Tuple tuple : result){
			System.out.println("offline="+tuple.getElement() + "=" + tuple.getScore()+"="+(tuple.getScore()));
		}
		System.out.println("online:"+WifiDeviceHandsetPresentSortedSetService.getInstance().presentOnlineSize(wifiId));
		System.out.println("offline:"+WifiDeviceHandsetPresentSortedSetService.getInstance().presentOfflineSize(wifiId));
	}
}
