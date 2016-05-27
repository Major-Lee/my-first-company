package com.bhu.statistics.util.cache;

import java.util.List;
import java.util.Set;

import org.apache.commons.lang.math.NumberUtils;

import com.bhu.statistics.util.redis.IJedisClient;
import com.bhu.statistics.util.redis.JedisClientImpl;


/**
 * JD缓存管理类
 * @author zhangsongpu
 * @date:2015-9-7 下午10:10:25
 */
public class BhuCache {
	
	private static IJedisClient jedisClient = new JedisClientImpl("redis_m");
	
	private static BhuCache instance = new BhuCache();
	
	public static BhuCache getInstance() {
		return instance;
	}

	private BhuCache() {
	}
	
	/**
	 * 获取用户验证码
	 * @param userid
	 * @return
	 */
	public String getCaptcha(String mobile,int type){
		return jedisClient.get(BhuCacheKeyGen.getCaptcha(mobile,type));
	}
	
	public void setCaptcha(String mobile,int type, String value){
		jedisClient.setex(BhuCacheKeyGen.getCaptcha(mobile,type), value, BhuCacheKeyGen.CACHE_TIME_FIVE_MINITS);
	}
	
	/**
	 * 缓存所有PV【连接总数】总数
	 * @param data
	 * @param type
	 * @return
	 */
	public String getTotalPV(String typeName){
		return jedisClient.get(BhuCacheKeyGen.getTotalPV(typeName));
	}
	
	public void setTotalPV(String typeName,String value){
		jedisClient.setex(BhuCacheKeyGen.getTotalPV(typeName),value, BhuCacheKeyGen.CACHE_TIME_ONE_MONTH);
	}
	
	/**
	 * 缓存所有UV【连接人数总数】总数
	 * @param data
	 * @param type
	 * @return
	 */
	public String getTotalUV(String typeName){
		return jedisClient.get(BhuCacheKeyGen.getTotalUV(typeName));
	}
	
	public void setTotalUV(String typeName,String value){
		jedisClient.setex(BhuCacheKeyGen.getTotalUV(typeName),value, BhuCacheKeyGen.CACHE_TIME_ONE_MONTH);
	}
	
	public String getDayPV(String data,String filed){
		return jedisClient.hget(BhuCacheKeyGen.getDayPV(data),filed);
	}
	
	public Long setDayPV(String data,String field,String value){
		return jedisClient.hset(BhuCacheKeyGen.getDayPV(data), field, value);
	}
	
	public String getDayUV(String data,String filed){
		return jedisClient.hget(BhuCacheKeyGen.getDayUV(data),filed);
	}
	
	public Long setDayUV(String data,String field,String value){
		return jedisClient.hset(BhuCacheKeyGen.getDayUV(data), field, value);
	}
	
	public String getSSID(String data,String filed){
		return jedisClient.hget(BhuCacheKeyGen.getSSID(data),filed);
	}
	public Long setSSID(String data,String field,String value){
		return jedisClient.hset(BhuCacheKeyGen.getSSID(data), field, value);
	}
	
	public String getSSIDUV(String data){
		return jedisClient.get(BhuCacheKeyGen.getSSIDUV(data));
	}
	public Long setSSIDUV(String data,String field,String value){
		return jedisClient.hset(BhuCacheKeyGen.getSSIDUV(data), field, value);
	}
	
	public Set<String> getSSIDFiled(String key){
		return jedisClient.hkeys(BhuCacheKeyGen.getSSID(key));
	}
	
	public Set<String> getdayPVFiled(String key){
		return jedisClient.hkeys(BhuCacheKeyGen.getDayPV(key));
	}
	
	public Set<String> getdayUVFiled(String key){
		return jedisClient.hkeys(BhuCacheKeyGen.getDayUV(key));
	}
	
	public long deleteSSIDHash(String key,String filed){
		return jedisClient.hdel(BhuCacheKeyGen.getSSID(key), filed);
	}
	
	public long deletedayPVHash(String key,String filed){
		return jedisClient.hdel(BhuCacheKeyGen.getDayPV(key), filed);
	}
	
	public long deletedayUVHash(String key,String filed){
		return jedisClient.hdel(BhuCacheKeyGen.getDayUV(key), filed);
	}
	
	public long delTotalPV(String key){
		return jedisClient.del(BhuCacheKeyGen.getTotalPV(key));
	}
	
	public long delTotalUV(String key){
		return jedisClient.del(BhuCacheKeyGen.getTotalUV(key));
	}
	
	public String getEquipment(String data,String filed){
		return jedisClient.hget(BhuCacheKeyGen.getEquipment(data),filed);
	}
	public Long setEquipment(String data,String field,String value){
		return jedisClient.hset(BhuCacheKeyGen.getEquipment(data), field, value);
	}
	
	public String getStOrder(String data,String filed){
		return jedisClient.hget(BhuCacheKeyGen.getStOrder(data),filed);
	}
	public Long setStOrder(String data,String field,String value){
		return jedisClient.hset(BhuCacheKeyGen.getStOrder(data), field, value);
	}
	
}
