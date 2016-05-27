package com.bhu.vas.business.bucache.redis.serviceimpl.devices;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.springframework.util.StringUtils;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Tuple;

import com.bhu.vas.business.bucache.redis.serviceimpl.BusinessKeyDefine;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.RedisKeyEnum;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.RedisPoolManager;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.impl.AbstractRelationSortedSetCache;
import com.smartwork.msip.cores.helper.StringHelper;
import com.smartwork.msip.cores.orm.support.page.PageHelper;

/**
 *  wifi设备对应的在线移动设备列表
 *  ZSET 
 *  	key：wifiId 
 *  	score 在线状态基础数值(在线是100亿，100,0000,0000 离线是0) + 终端下行速率(百兆每秒速率是亿级别)
 *  	value 移动设备的mac
 *  
 * 1：移动设备上线新增，下线删除，sync更新数据
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
	
	private static String[] generateKeys(List<String> wifiIds){
		if(wifiIds == null || wifiIds.isEmpty()) return null;
		String[] keys = new String[wifiIds.size()];
		int cursor = 0;
		for(String wifiId : wifiIds){
			keys[cursor] = generateKey(wifiId);
			cursor++;
		}
		return keys;
	}
	
//	public void addPresent(String wifiId, String handsetId, long login_at){
//		super.zadd(generateKey(wifiId), login_at, handsetId);
//	}
	
//	public void removePresent(String wifiId, String handsetId){
//		super.zrem(generateKey(wifiId), handsetId);
//	}
	
//	public void clearPresents(String wifiId){
//		super.del(generateKey(wifiId));
//	}
//	
//	public void clearPresents(String wifiId, long max_login_at){
//		super.zremrangeByScore(generateKey(wifiId), 0, max_login_at);
//	}
	
//	private static final long Condition_Offline_TimeGap  = 1*24*60*60*1000l;
	
//	public Long presentNotOfflineSize(String wifiId){
//		return this.zcard(generateKey(wifiId));
//		/*long ts = System.currentTimeMillis();
//		return this.zcount(generateKey(wifiId), ts-Condition_Offline_TimeGap, ts);*/
//	}
	
	/**
	 * 获取该设备的在线终端数量
	 * @param wifiId
	 * @return
	 */
	public Long presentOnlineSize(String wifiId){
		return super.zcount(generateKey(wifiId), OnlineBaseScore, Long.MAX_VALUE);
	}
	
	public List<Object> presentOnlineSizes(List<String> wifiIds){
		if(wifiIds == null || wifiIds.isEmpty()) return null;
		return super.pipelineZCount_diffKeyWithSameScore(generateKeys(wifiIds), OnlineBaseScore, Long.MAX_VALUE);
	}
	
	public Long presentOfflineSize(String wifiId){
		return super.zcount(generateKey(wifiId), 0, (OnlineBaseScore-1));
	}
	
	public Long presentSize(String wifiId){
		return super.zcard(generateKey(wifiId));
	}
	
	public long addOnlinePresent(String wifiId, String handsetId, double rx_rate){
		return super.zadd(generateKey(wifiId), OnlineBaseScore+rx_rate, handsetId);
	}
	
	public long  addOfflinePresent(String wifiId, String handsetId, double rx_rate){
		return super.zadd(generateKey(wifiId), rx_rate, handsetId);
	}
	
	public Set<Tuple> fetchOnlinePresentWithScores(String wifiId,int start,int size){
		if(StringUtils.isEmpty(wifiId)) return Collections.emptySet();
		return super.zrevrangeByScoreWithScores(generateKey(wifiId), OnlineBaseScore, Long.MAX_VALUE, start, size);
	}
	
//	public Set<String> fetchOnlinePresents(String wifiId,int start,int size){
//		if(StringUtils.isEmpty(wifiId)) return Collections.emptySet();
//		return super.zrevrangeByScore(generateKey(wifiId), OnlineBaseScore, Long.MAX_VALUE, start, size);
//	}
	
	public Set<Tuple> fetchOfflinePresentWithScores(String wifiId,int start,int size){
		if(StringUtils.isEmpty(wifiId)) return Collections.emptySet();
		return super.zrevrangeByScoreWithScores(generateKey(wifiId), 0, (OnlineBaseScore-1), start, size);
	}
	
//	public Set<String> fetchOfflinePresents(String wifiId,int start,int size){
//		if(StringUtils.isEmpty(wifiId)) return Collections.emptySet();
//		return super.zrevrangeByScore(generateKey(wifiId), 0, (OnlineBaseScore-1), start, size);
//	}
	
	public Set<Tuple> fetchPresents(String wifiId,int start,int size){
		if(StringUtils.isEmpty(wifiId)) return Collections.emptySet();
		return super.zrevrangeWithScores(generateKey(wifiId), start, (start+size-1));
	}
	
	public void removePresents(String wifiId, List<String> handsetIds){
		if(handsetIds == null || handsetIds.isEmpty()) return;
		super.pipelineZRem_sameKeyWithDiffMember(generateKey(wifiId), handsetIds.toArray(new String[]{}));
	}
	
	public boolean isOnline(double score){
		if(score >= OnlineBaseScore){
			return true;
		}
		return false;
	}
	
	public double get_rx_rate(double score){
		if(isOnline(score)){
			return score - OnlineBaseScore;
		}
		return 0;
	}
	
	/**
	 * 把设备的在线终端变成离线状态
	 * @param wifiId
	 */
	public void changeOnlinePresentsToOffline(String wifiId){
		int size = 50;
		long count = presentOnlineSize(wifiId);
		int page = PageHelper.getTotalPages((int)count, size);
		for(int i=0;i<page;i++){
			Set<Tuple> result = fetchOnlinePresentWithScores(wifiId, 0, size);
			for(Tuple tuple : result){
				addOfflinePresent(wifiId, tuple.getElement(), (tuple.getScore() - OnlineBaseScore));
			}
		}
	}
	
	/**
	 * 移除设备的离线终端记录
	 * @param wifiId
	 */
	public long clearOfflinePresents(String wifiId){
		return super.zremrangeByScore(generateKey(wifiId), 0, (OnlineBaseScore-1));
	}
	
	
	/**
	 * 
	 * @param wifiId
	 * @return
	 * modified by Edmond Lee for handset storage
	 */
	public List<String> fetchAllOnlinePresents(String wifiId){
		List<String> result = new ArrayList<String>();
		int size = 100;
		long count = presentOnlineSize(wifiId);
		int page = PageHelper.getTotalPages((int)count, size);
		for(int i=1;i<=page;i++){
			Set<Tuple> tuple_result = fetchOnlinePresentWithScores(wifiId, PageHelper.getStartIndexOfPage(i, size), size);
			for(Tuple tuple : tuple_result){
				result.add(tuple.getElement());
			}
		}
		return result;
	}
	

//	public Set<String> fetchPresents(String wifiId){
//		if(StringUtils.isEmpty(wifiId)) return Collections.emptySet();
//		//return super.zrevrangeWithScores(generateKey(wifiId), 0, 10);
//		return super.zrevrangeByScore(generateKey(wifiId), 0, 10000, 0, 10);
//	}
	
	/**
	 * 按移动设备接入时间，从大到小排序
	 * @param wifiId wifi mac
	 * @param start
	 * @param size
	 * @return 包含移动设备的接入时间的set集合
	 */
//	public Set<Tuple> fetchPresents(String wifiId,int start,int size){
//		if(StringUtils.isEmpty(wifiId)) return Collections.emptySet();
//		return super.zrevrangeWithScores(generateKey(wifiId), start, start+size-1);
//	}

	
	@Override
	public String getRedisKey() {
		return null;
	}
	
	@Override
	public String getName() {
		return WifiDeviceHandsetPresentSortedSetServiceOld.class.getName();
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
		Set<Tuple> result = WifiDeviceHandsetPresentSortedSetService.getInstance().fetchOnlinePresentWithScores(wifiId, 0, 10);
		for(Tuple tuple : result){
			System.out.println("online="+tuple.getElement() + "=" + tuple.getScore()+"="+(tuple.getScore() - OnlineBaseScore));
		}
		result = WifiDeviceHandsetPresentSortedSetService.getInstance().fetchOfflinePresentWithScores(wifiId, 0, 10);
		for(Tuple tuple : result){
			System.out.println("offline="+tuple.getElement() + "=" + tuple.getScore()+"="+(tuple.getScore()));
		}
		System.out.println("online:"+WifiDeviceHandsetPresentSortedSetService.getInstance().presentOnlineSize(wifiId));
		System.out.println("offline:"+WifiDeviceHandsetPresentSortedSetService.getInstance().presentOfflineSize(wifiId));
	}
}
