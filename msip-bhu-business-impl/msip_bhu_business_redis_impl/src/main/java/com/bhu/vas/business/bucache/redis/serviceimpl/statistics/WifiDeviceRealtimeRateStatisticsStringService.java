package com.bhu.vas.business.bucache.redis.serviceimpl.statistics;

import java.util.List;

import redis.clients.jedis.JedisPool;

import com.bhu.vas.business.bucache.redis.serviceimpl.BusinessKeyDefine;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.RedisKeyEnum;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.RedisPoolManager;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.impl.AbstractRelationStringCache;
import com.smartwork.msip.cores.helper.StringHelper;

/**
 * 	用于设备的实时上下行速率
 * @author lawliet
 *
 */
public class WifiDeviceRealtimeRateStatisticsStringService extends AbstractRelationStringCache{
	
	private static class ServiceHolder{ 
		private static WifiDeviceRealtimeRateStatisticsStringService instance =new WifiDeviceRealtimeRateStatisticsStringService(); 
	}
	/**
	 * 获取工厂单例
	 * @return
	 */
	public static WifiDeviceRealtimeRateStatisticsStringService getInstance() { 
		return ServiceHolder.instance; 
	}
	
	private WifiDeviceRealtimeRateStatisticsStringService(){
	}
	
	private static final int exprie_realtime_seconds = 10;//10秒
	private static final int exprie_waiting_seconds = 30;//30秒
	
	public static final int Type_Tx_Rate = 1;//上行速率
	public static final int Type_Rx_Rate = 2;//下行速率
	
	private static String generateRealtimeKey(String mac, int type){
		StringBuilder sb = new StringBuilder(BusinessKeyDefine.Statistics.WifiDeviceStatistics);
		sb.append(StringHelper.POINT_CHAR_GAP).append(BusinessKeyDefine.Statistics.WifiDeviceStatistics_RealtimeRate);
		sb.append(StringHelper.POINT_CHAR_GAP).append(type);
		sb.append(StringHelper.POINT_CHAR_GAP).append(mac);
		return sb.toString();
	}
	
	private static String generateLastKey(String mac, int type){
		StringBuilder sb = new StringBuilder(BusinessKeyDefine.Statistics.WifiDeviceStatistics);
		sb.append(StringHelper.POINT_CHAR_GAP).append(BusinessKeyDefine.Statistics.WifiDeviceStatistics_LastRate);
		sb.append(StringHelper.POINT_CHAR_GAP).append(type);
		sb.append(StringHelper.POINT_CHAR_GAP).append(mac);
		return sb.toString();
	}
	
	private static String generatePeakKey(String mac){
		StringBuilder sb = new StringBuilder(BusinessKeyDefine.Statistics.WifiDeviceStatistics);
		sb.append(StringHelper.POINT_CHAR_GAP).append(BusinessKeyDefine.Statistics.WifiDeviceStatistics_PeakRate);
		sb.append(StringHelper.POINT_CHAR_GAP).append(mac);
		return sb.toString();
	}
	
	private static String[] generateAllKeysWithoutWaiting(String mac){
		String[] keys = new String[4];
		String r_tx_key = generateRealtimeKey(mac, Type_Tx_Rate);
		keys[0] = r_tx_key;
		String r_rx_key = generateRealtimeKey(mac, Type_Rx_Rate);
		keys[1] = r_rx_key;
		String l_tx_key = generateLastKey(mac, Type_Tx_Rate);
		keys[2] = l_tx_key;
		String l_rx_key = generateLastKey(mac, Type_Rx_Rate);
		keys[3] = l_rx_key;
		return keys;
	}
	
	private static String[] generateAllKeys(String mac){
		String[] keys = new String[4];
		String r_tx_key = generateRealtimeKey(mac, Type_Tx_Rate);
		keys[0] = r_tx_key;
		String r_rx_key = generateRealtimeKey(mac, Type_Rx_Rate);
		keys[1] = r_rx_key;
		String l_tx_key = generateLastKey(mac, Type_Tx_Rate);
		keys[2] = l_tx_key;
		String l_rx_key = generateLastKey(mac, Type_Rx_Rate);
		keys[3] = l_rx_key;
		String waiting_key = generateWaitingKey(mac);
		keys[4] = waiting_key;
		String peak_key = generatePeakKey(mac);
		keys[5] = peak_key;
		return keys;
	}
	
	private static String[] generateAllValues(String tx_rate, String rx_rate){
		String[] values = new String[4];
		values[0] = tx_rate;
		values[1] = rx_rate;
		values[2] = tx_rate;
		values[3] = rx_rate;
		return values;
	}
	
	private static String generateWaitingKey(String mac){
		StringBuilder sb = new StringBuilder(BusinessKeyDefine.Statistics.WifiDeviceStatistics);
		sb.append(StringHelper.POINT_CHAR_GAP).append(BusinessKeyDefine.Statistics.WifiDeviceStatistics_Waiting);
		sb.append(StringHelper.POINT_CHAR_GAP).append(mac);
		return sb.toString();
	}
	
	public static final String WaitingMark = "waiting";
	
	public void addWaiting(String mac){
		String key = generateWaitingKey(mac);
		super.set(key, WaitingMark);
		super.expire(key, exprie_waiting_seconds);
	}
	
	/**
	 * 设置实时上下行速率和最后一次的实时上下行速率
	 * @param mac
	 * @param tx_rate 上行速率
	 * @param rx_rate 下行速率
	 */
	public void addRate(String mac, String tx_rate, String rx_rate){
		String[] keys = generateAllKeysWithoutWaiting(mac);
		String[] values = generateAllValues(tx_rate, rx_rate);
		super.mset(keys, values);
		super.expire(keys[0], exprie_realtime_seconds);
		super.expire(keys[1], exprie_realtime_seconds);
	}
	
	/**
	 * 设备设备网速峰值 (下行最大速率)
	 * @param mac
	 * @param rx_peak_rate
	 */
	public void addPeak(String mac, String rx_peak_rate){
		super.set(generatePeakKey(mac), rx_peak_rate);
	}

	/**
	 * 获取所有数据
	 * @param mac
	 * @return 
	 * 		0: tx_rate
	 * 		1: rx_rate
	 * 		2: last_tx_rate
	 * 		3: last_rx_rate
	 * 		4: waiting
	 */
	public List<String> getRate(String mac){
		String[] keys = generateAllKeys(mac);
		return super.mget(keys);
	}
	
	@Override
	public String getRedisKey() {
		return null;
	}
	
	@Override
	public String getName() {
		return WifiDeviceRealtimeRateStatisticsStringService.class.getName();
	}
	
	@Override
	public JedisPool getRedisPool() {
		return RedisPoolManager.getInstance().getPool(RedisKeyEnum.STATISTICS);
	}
}
