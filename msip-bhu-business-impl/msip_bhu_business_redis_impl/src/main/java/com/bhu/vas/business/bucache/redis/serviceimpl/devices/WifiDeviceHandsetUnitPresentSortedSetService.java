package com.bhu.vas.business.bucache.redis.serviceimpl.devices;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.springframework.util.StringUtils;

import com.bhu.vas.business.bucache.redis.serviceimpl.BusinessKeyDefine;
import com.ibm.icu.text.SimpleDateFormat;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.RedisKeyEnum;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.RedisPoolManager;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.impl.AbstractRelationSortedSetCache;
import com.smartwork.msip.cores.helper.StringHelper;
import com.smartwork.msip.cores.orm.support.page.PageHelper;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Tuple;
/**
 * 统一主网络和访客网络上下线设备
 *  *  	key：wifiId 
 *  	score 在线状态基础数值(在线是100亿，100,0000,0000 离线是0) + 时间(yyMMddhhmm)
 *  	value 移动设备的mac
 * 
 * 上线新增、线下修改socre值、sync更新
 * @author xiaowei
 *
 */
public class WifiDeviceHandsetUnitPresentSortedSetService extends AbstractRelationSortedSetCache{

	private static class ServiceHolder{ 
		private static WifiDeviceHandsetUnitPresentSortedSetService instance =new WifiDeviceHandsetUnitPresentSortedSetService(); 
	}
	/**
	 * 获取工厂单例
	 * @return
	 */
	public static WifiDeviceHandsetUnitPresentSortedSetService getInstance() { 
		return ServiceHolder.instance; 
	}
	
	//在线初始score数值 100亿 
	public static final double OnlineBaseScore = 10000000000d;
	public static final double VisitorOnlineBaseScore = 0d;
	
	public static final String OnlineDatePattern = "MMddHHmm";
	private WifiDeviceHandsetUnitPresentSortedSetService(){
	}
	
	private static String generateKey(String wifiId){
		StringBuilder sb = new StringBuilder(BusinessKeyDefine.Present.WifiDeviceHandsetUnitPresentPrefixKey);
		sb.append(StringHelper.POINT_CHAR_GAP).append(wifiId);
		return sb.toString();
	}
	
	private static String[] generateKey(String[] wifiIds){
		
		String[] macs = new String [wifiIds.length];
		int index = 0;
		for (String wifiId : wifiIds) {
			StringBuilder sb = new StringBuilder(BusinessKeyDefine.Present.WifiDeviceHandsetUnitPresentPrefixKey);
			sb.append(StringHelper.POINT_CHAR_GAP).append(wifiId);
			macs[index] = sb.toString();
			index++;
		}
		return macs;
	}
	
	//生成score值，为当前终端上线时间  年月日时分
	private static double generateScore(long login_at){
		SimpleDateFormat sdf = new SimpleDateFormat(OnlineDatePattern,new Locale("zh","CN"));
		return Double.parseDouble(sdf.format(new Date(login_at)));
	}
	
	public long addOnlinePresent(String wifiId, String handsetId, long this_login_at){
		System.out.println("----------------------");
		System.out.println(this_login_at);
		System.out.println(this_login_at);
		System.out.println(generateScore(this_login_at));
		System.out.println(generateScore(this_login_at));
		System.out.println("=========================");
		return super.zadd(generateKey(wifiId), OnlineBaseScore+generateScore(this_login_at), handsetId);
	}
	
	public List<Object> presentOnlineSizeWithScore(String[] wifiIds,long timestamp){
		System.out.println("********************");
		System.out.println(timestamp);
		System.out.println(timestamp);
		System.out.println(generateScore(timestamp));
		System.out.println(generateScore(timestamp));
		System.out.println("********************");
		return super.pipelineZCount_diffKeyWithSameScore(generateKey(wifiIds), OnlineBaseScore+generateScore(timestamp), Long.MAX_VALUE);
	}
	
	public long removeUnauthHandset(String wifiId, String handsetId, long last_login_at){
		return super.zremrangeByScore(generateKey(wifiId), VisitorOnlineBaseScore, VisitorOnlineBaseScore);
	}
	
	/**
	 * 获取该设备的在线终端数量
	 * @param wifiId
	 * @return
	 */
	public Long presentOnlineSize(String wifiId){
		return super.zcount(generateKey(wifiId), OnlineBaseScore, Long.MAX_VALUE);
	}
	/**
	 * 获取该设备的离线终端数量
	 * @param wifiId
	 * @return
	 */
	public Long presentOfflineSize(String wifiId){
		return super.zcount(generateKey(wifiId), 1L, (OnlineBaseScore-1));
	}
	
	/**
	 * 获取该设备的所有在线设备
	 * @param wifiId
	 * @return
	 */
	public Set<String> fetchAllOnlinePresent(String wifiId){
		return super.zrangeByScore(generateKey(wifiId), OnlineBaseScore, Long.MAX_VALUE);
	}
	
	public Set<Tuple> fetchPresents(String wifiId,int start,int size){
		if(StringUtils.isEmpty(wifiId)) return Collections.emptySet();
		return super.zrevrangeWithScores(generateKey(wifiId), start, (start+size-1));
	}
	
	public Long presentSize(String wifiId){
		return super.zcard(generateKey(wifiId));
	}
	
    /**
     * 访客网络终端认证取消下线
     * @param wifiId
     * @param handsetId
     * @return
     */
    public long removePresent(String wifiId, String handsetId) {
        return super.zrem(generateKey(wifiId), handsetId);
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
				addOfflinePresent(wifiId, tuple.getElement(), (long)(tuple.getScore() - OnlineBaseScore));
			}
			//移除访客网络未认证终端
			Set<Tuple> VistorOnlineResult = fetchVisitorOnlinePresent(wifiId, 0 ,size);
			for(Tuple tuple : VistorOnlineResult){
				removePresent(wifiId, tuple.getElement());
			}
		}
	}
	
	/**
	 * 
	 * @param wifiId
	 * @return
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
	
	public boolean isOnline(double score){
		if(score >= OnlineBaseScore){
			return true;
		}
		return false;
	}
	
	public Set<Tuple> fetchOnlinePresentWithScores(String wifiId,int start,int size){
		if(StringUtils.isEmpty(wifiId)) return Collections.emptySet();
		return super.zrevrangeByScoreWithScores(generateKey(wifiId), OnlineBaseScore, Long.MAX_VALUE, start, size);
	}
	
	public Set<Tuple> fetchAllPresentWithScores(String wifiId,int start,int size){
		if(StringUtils.isEmpty(wifiId)) return Collections.emptySet();
		return super.zrevrangeByScoreWithScores(generateKey(wifiId), VisitorOnlineBaseScore, Long.MAX_VALUE, start, size);
	}
	
	public Set<Tuple> fetchPresentWithScores(String wifiId,int start,int size){
		if(StringUtils.isEmpty(wifiId)) return Collections.emptySet();
		return super.zrevrangeByScoreWithScores(generateKey(wifiId), 1L, Long.MAX_VALUE, start, size);
	}
	
	
	public Set<Tuple> fetchVisitorOnlinePresent(String wifiId,int start,int size){
		return super.zrevrangeByScoreWithScores(generateKey(wifiId), VisitorOnlineBaseScore, VisitorOnlineBaseScore, start, size);
	}
	
	public Set<Tuple> fetchOfflinePresentWithScores(String wifiId,int start,int size){
		if(StringUtils.isEmpty(wifiId)) return Collections.emptySet();
		return super.zrevrangeByScoreWithScores(generateKey(wifiId), 1L, (OnlineBaseScore-1), start, size);
	}
	
	public long  addOfflinePresent(String wifiId, String handsetId, long last_login_at){
		return super.zadd(generateKey(wifiId), generateScore(last_login_at), handsetId);
	}
	
	@Override
	public String getName() {
		return WifiDeviceHandsetUnitPresentSortedSetService.class.getName();

	}

	@Override
	public String getRedisKey() {
		return null;
	}

	@Override
	public JedisPool getRedisPool() {
		return RedisPoolManager.getInstance().getPool(RedisKeyEnum.PRESENT);
	}

	public void clearPresent(String wifiId_lowerCase) {
		super.zremrangeByScore(generateKey(wifiId_lowerCase), 0, -1);
	}
	public static void main(String[] args) {
//		WifiDeviceHandsetUnitPresentSortedSetService.getInstance().addOnlinePresent("84:82:f4:19:01:0c", "11:11:11:11:11:11", System.currentTimeMillis());
//		WifiDeviceHandsetUnitPresentSortedSetService.getInstance().addOfflinePresent("84:82:f4:2f:3a:50", "68:3e:34:48:b7:35", 1607121523);
//		SimpleDateFormat sdf = new SimpleDateFormat(OnlineDatePattern);
//		String str  = sdf.format(new Date(1468893600000L));
//		int a = Integer.parseInt(str);
//		System.out.println();
//		System.out.println(sdf.format(new Date(1468893600000L)));
		System.out.println(generateScore(1468925162702L));
	}
}
