package com.bhu.statistics.util.cache;

import java.util.List;

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
	
	public String getDayPV(String data){
		return jedisClient.get(BhuCacheKeyGen.getDayPV(data));
	}
	
	public Long setDayPV(String data,String field,String value){
		return jedisClient.hset(BhuCacheKeyGen.getDayPV(data), field, value);
	}
	
	public String getDayUV(String data){
		return jedisClient.get(BhuCacheKeyGen.getDayUV(data));
	}
	
	public Long setDayUV(String data,String field,String value){
		return jedisClient.hset(BhuCacheKeyGen.getDayPV(data), field, value);
	}
	
	
}
