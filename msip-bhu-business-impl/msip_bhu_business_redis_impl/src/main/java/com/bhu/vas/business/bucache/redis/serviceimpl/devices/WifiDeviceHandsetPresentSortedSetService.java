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
 *  前置条件：终端下行速率(百兆每秒速率是亿级别)，终端的速率不可能达到百兆或千兆
 *  ZSET 
 *  	key：wifiId 
 *  	score （old）在线状态基础数值(在线是100亿，100,0000,0000 离线是0) + 终端下行速率(百兆每秒速率是亿级别)
 *  			（new）在线状态分为 无线终端在线和有线终端在线
 *  				  有线终端在线状态基础数值(在线是200亿~Long.MAX_VALUE，100,0000,0000 离线是0) + 终端下行速率(百兆每秒速率是亿级别)
 *  				  无线终端在线状态基础数值(在线是100亿~200亿，100,0000,0000 离线是0) + 终端下行速率(百兆每秒速率是亿级别) 
 *  	value 移动设备的mac
 *  
 * 1：移动设备上线新增，下线删除，sync更新数据
 * @author lawliet
 *
 */
public class WifiDeviceHandsetPresentSortedSetService extends AbstractRelationSortedSetCache{
	//在线初始score数值 100亿 
	public static final double Online_WirelessMinScore = 10000000000d;
	public static final double Online_WirelessMaxScore = 20000000000d;
	public static final double Online_WiredMinScore = 20000000001d;
	//public static final double Online_WiredMaxScore = 20000000000d;
	
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
	
/*	*//**
	 * 获取该设备的在线终端数量
	 * @param wifiId
	 * @return
	 *//*
	public Long presentOnlineSize(String wifiId){
		return super.zcount(generateKey(wifiId), Online_WirelessMinScore, Long.MAX_VALUE);
	}*/
	
	/**
	 * 获取离线数量
	 * @param wifiId
	 * @return
	 */
	public Long presentOfflineSize(String wifiId){
		return super.zcount(generateKey(wifiId), 0, (Online_WirelessMinScore-1));
	}
	
	/*public Long presentSize(String wifiId){
		return super.zcard(generateKey(wifiId));
	}*/
	
	/**
	 * 获取有线或者无线终端的在线数量
	 * @param wifiId
	 * @param wireless null 不区分有线无线  true 无线  false 有线
	 * @return
	 */
	public Long presentOnlineSize(String wifiId,Boolean wireless){
		Long ret = 0l;
		if(wireless == null){
			ret = super.zcount(generateKey(wifiId), Online_WirelessMinScore, Long.MAX_VALUE);
		}else{
			if(wireless.booleanValue())
				ret = super.zcount(generateKey(wifiId), Online_WirelessMinScore, Online_WirelessMaxScore);
			else
				ret = super.zcount(generateKey(wifiId), Online_WiredMinScore, Long.MAX_VALUE);
		}
		return ret;
	}
	
	/**
	 * 增加终端的在线状态
	 * 区分有线和无线的终端
	 * @param wifiId
	 * @param handsetId
	 * @param rx_rate
	 * @param wireless null的情况下 会赋值Boolean.TRUE
	 * @return
	 */
	public Long addOnlinePresent(String wifiId, String handsetId, double rx_rate,Boolean wireless){
		Long ret = 0l;
		if(wireless == null) wireless = Boolean.TRUE;
		if(wireless)
			ret = super.zadd(generateKey(wifiId), Online_WirelessMinScore+rx_rate, handsetId);
		else
			ret = super.zadd(generateKey(wifiId), Online_WiredMinScore+rx_rate, handsetId);
		return ret;
	}
	
	
	public long addOfflinePresent(String wifiId, String handsetId, double rx_rate){
		return super.zadd(generateKey(wifiId), rx_rate, handsetId);
	}
	
	/**
	 * 获取所有在线的终端列表
	 * 包括有线和无线
	 * @param wifiId
	 * @param start
	 * @param size
	 * @param wireless null 不区分有线无线  true 无线  false 有线
	 * @return
	 */
	public Set<Tuple> fetchOnlinePresentWithScores(String wifiId,int start,int size,Boolean wireless){
		if(StringUtils.isEmpty(wifiId)) return Collections.emptySet();
		Set<Tuple> ret = null;
		if(wireless == null)
			ret = super.zrevrangeByScoreWithScores(generateKey(wifiId), Online_WirelessMinScore, Long.MAX_VALUE, start, size);
		else{
			if(wireless)
				ret = super.zrevrangeByScoreWithScores(generateKey(wifiId), Online_WirelessMinScore, Online_WirelessMaxScore, start, size);
			else
				ret = super.zrevrangeByScoreWithScores(generateKey(wifiId), Online_WiredMinScore, Long.MAX_VALUE, start, size);
		}
		return ret;
	}
	
	/**
	 * 获取所有离线的终端列表
	 * 可以不区分有线和无线终端
	 * @param wifiId
	 * @param start
	 * @param size
	 * @return
	 */
	public Set<Tuple> fetchOfflinePresentWithScores(String wifiId,int start,int size){
		if(StringUtils.isEmpty(wifiId)) return Collections.emptySet();
		return super.zrevrangeByScoreWithScores(generateKey(wifiId), 0, (Online_WirelessMinScore-1), start, size);
	}
	
	/*public Set<Tuple> fetchPresents(String wifiId,int start,int size){
		if(StringUtils.isEmpty(wifiId)) return Collections.emptySet();
		return super.zrevrangeWithScores(generateKey(wifiId), start, (start+size-1));
	}*/
	
	public void removePresents(String wifiId, List<String> handsetIds){
		if(handsetIds == null || handsetIds.isEmpty()) return;
		super.pipelineZRem_sameKeyWithDiffMember(generateKey(wifiId), handsetIds.toArray(new String[]{}));
	}
	
	public boolean isOnline(double score){
		if(score >= Online_WirelessMinScore){
			return true;
		}
		return false;
	}
	
	/**
	 * 是否无线在线
	 * @param score
	 * @return
	 */
	public boolean isWirelessOnline(double score){
		if(score >= Online_WirelessMinScore && score<=Online_WirelessMaxScore){
			return true;
		}
		return false;
	}
	
	/**
	 * 把设备的在线终端变成离线状态
	 * @param wifiId
	 */
	public void changeOnlinePresentsToOffline(String wifiId){
		int size = 50;
		long count = presentOnlineSize(wifiId,null);
		int page = PageHelper.getTotalPages((int)count, size);
		for(int i=0;i<page;i++){
			Set<Tuple> result = fetchOnlinePresentWithScores(wifiId, 0, size,null);
			for(Tuple tuple : result){
				if(isWirelessOnline(tuple.getScore()))//无线终端
					addOfflinePresent(wifiId, tuple.getElement(), (tuple.getScore() - Online_WirelessMinScore));
				else
					addOfflinePresent(wifiId, tuple.getElement(), (tuple.getScore() - Online_WiredMinScore));
			}
		}
	}
	
	/**
	 * 移除设备的离线终端记录
	 * @param wifiId
	 */
	public long clearOfflinePresents(String wifiId){
		return super.zremrangeByScore(generateKey(wifiId), 0, (Online_WirelessMinScore-1));
	}
	
	
	/**
	 * 获取所有的在线终端（不区分有线和无线）
	 * @param wifiId
	 * @return
	 * modified by Edmond Lee for handset storage
	 */
	public List<String> fetchAllOnlinePresents(String wifiId){
		List<String> result = new ArrayList<String>();
		int size = 100;
		long count = presentOnlineSize(wifiId,null);
		int page = PageHelper.getTotalPages((int)count, size);
		for(int i=1;i<=page;i++){
			Set<Tuple> tuple_result = fetchOnlinePresentWithScores(wifiId, PageHelper.getStartIndexOfPage(i, size), size,null);
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
		return WifiDeviceHandsetPresentSortedSetService.class.getName();
	}
	
	@Override
	public JedisPool getRedisPool() {
		return RedisPoolManager.getInstance().getPool(RedisKeyEnum.PRESENT);
	}
	
	public static void main(String[] args){
		String wifiId = "tt:tt:tt:tt:tt:t";
		String wirelessHandsetMacPrefix = "hh:hh:hh:hh:hh:h";
		String wireledHandsetMacPrefix  = "oo:oo:oo:oo:oo:o";
		for(int i = 0;i<10;i++){
			WifiDeviceHandsetPresentSortedSetService.getInstance().addOnlinePresent(wifiId, 
					wirelessHandsetMacPrefix.concat(String.valueOf(i)), 1024+i,Boolean.TRUE);
		}
		
		/*for(int i = 10;i<20;i++){
			WifiDeviceHandsetPresentSortedSetService.getInstance().addOfflinePresent(wifiId, 
					wireledHandsetMacPrefix.concat(String.valueOf(i)), 1024+i,Boolean.FALSE);
		}*/
		
		for(int i = 0;i<10;i++){
			WifiDeviceHandsetPresentSortedSetService.getInstance().addOnlinePresent(wifiId, 
					wireledHandsetMacPrefix.concat(String.valueOf(i)), 1024+i,Boolean.FALSE);
		}
		
		/*for(int i = 0;i<10;i++){
			WifiDeviceHandsetPresentSortedSetService.getInstance().addOfflinePresent(wifiId, 
					wireledHandsetMacPrefix.concat(String.valueOf(i)), 1024+i,Boolean.FALSE);
		}*/
		
		Set<Tuple> result = WifiDeviceHandsetPresentSortedSetService.getInstance().fetchOnlinePresentWithScores(wifiId, 0, 10,Boolean.TRUE);
		for(Tuple tuple : result){
			System.out.println("wireless online="+tuple.getElement() + "=" + tuple.getScore()+"="+(tuple.getScore() - Online_WirelessMinScore));
		}
		
		result = WifiDeviceHandsetPresentSortedSetService.getInstance().fetchOnlinePresentWithScores(wifiId, 0, 10,Boolean.FALSE);
		for(Tuple tuple : result){
			System.out.println("wired online="+tuple.getElement() + "=" + tuple.getScore()+"="+(tuple.getScore() - Online_WiredMinScore));
		}
		
		result = WifiDeviceHandsetPresentSortedSetService.getInstance().fetchOfflinePresentWithScores(wifiId, 0, 10);
		for(Tuple tuple : result){
			System.out.println("offline="+tuple.getElement() + "=" + tuple.getScore()+"="+(tuple.getScore()));
		}
		System.out.println("online:"+WifiDeviceHandsetPresentSortedSetService.getInstance().presentOnlineSize(wifiId));
		System.out.println("offline:"+WifiDeviceHandsetPresentSortedSetService.getInstance().presentOfflineSize(wifiId));
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
}
