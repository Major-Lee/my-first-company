package com.bhu.vas.business.bucache.redis.serviceimpl.handset;

import redis.clients.jedis.JedisPool;

import com.bhu.vas.business.bucache.redis.serviceimpl.BusinessKeyDefine;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.RedisKeyEnum;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.RedisPoolManager;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.impl.AbstractRelationHashCache;
import com.smartwork.msip.cores.helper.StringHelper;
/**
 * 设备终端属性的一些扩展 变动的字段
 * key：dmac-hmac
 * Field:
 * 		累计流量 trb total_rx_bytes
 * 		TBD(例如终端接入次数，最后接入时间等)
 * 所有终端数量
 * @author edmond
 *
 */
public class DeviceHandsetExtFieldService extends AbstractRelationHashCache {
	
	private static class ServiceHolder{ 
		private static DeviceHandsetExtFieldService instance =new DeviceHandsetExtFieldService(); 
	}
	/**
	 * 获取工厂单例
	 * @return
	 */
	public static DeviceHandsetExtFieldService getInstance() { 
		return ServiceHolder.instance; 
	}
	
	private DeviceHandsetExtFieldService(){
	}
	
	private String generateKey(String dmac,String hmac){
		StringBuilder sb = new StringBuilder();
		sb.append(BusinessKeyDefine.HandsetPresent.StatisticsTRBKey)
			.append(dmac).append(StringHelper.MINUS_CHAR_GAP).append(hmac);
		return sb.toString();
	}
	private static final String Field_TRB = "trb";
	public long increaseTrb(String dmac,String hmac,long trb){
		return this.hincrby(generateKey(dmac,hmac), Field_TRB, trb);
	}
	
	public long trb(String dmac,String hmac){
		String result = this.hget(generateKey(dmac,hmac), Field_TRB);
		if(result == null) return 0l;
		else{
			return Long.parseLong(result);
		}
	}
	
	public long trbclear(String dmac,String hmac){
		this.hset(generateKey(dmac,hmac), Field_TRB, "0");
		return 0l;
	}
	
	
	
/*	private static final String Field_Online = "O";
	private static final String Field_Total = "T";
	
	private static String generateKey(){
		return BusinessKeyDefine.HandsetPresent.StatisticsPrefixKey;
	}
	
	public long online(boolean isOnline){
		return online(isOnline,1);
	}
	public long online(boolean isOnline,int incr){
		return this.hincrby(generateKey(), Field_Online, isOnline?incr:-incr);
	}
	
	public void statisticsSet(long online,long total){
		Map<String,String> map = new HashMap<String,String>();
		map.put(Field_Online, String.valueOf(online));
		map.put(Field_Total, String.valueOf(total));
		this.hmset(generateKey(), map);
	}
	
	public int[] statistics(){
		Map<String, String> all = this.hgetall(generateKey());
		int[] result = new int[2];
		String online = all.get(Field_Online);
		if(StringUtils.isNotEmpty(online)) 
			result[0] = Integer.parseInt(online);
		else
			result[0] = 0;
		String total = all.get(Field_Total);
		if(StringUtils.isNotEmpty(total)) 
			result[1] = Integer.parseInt(total);
		else
			result[1] = 0;
		return result;
	}*/
	
	@Override
	public String getRedisKey() {
		return null;
	}
	
	@Override
	public String getName() {
		return DeviceHandsetExtFieldService.class.getName();
	}
	@Override
	public JedisPool getRedisPool() {
		return RedisPoolManager.getInstance().getPool(RedisKeyEnum.CLUSTEREXT);
	}
}
