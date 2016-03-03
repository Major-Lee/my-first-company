package com.bhu.vas.business.bucache.redis.serviceimpl.social;

import java.util.Map;

import com.bhu.vas.business.bucache.redis.serviceimpl.BusinessKeyDefine;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.RedisKeyEnum;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.RedisPoolManager;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.impl.AbstractRelationHashCache;

import redis.clients.jedis.JedisPool;

public class WifiActionService extends AbstractRelationHashCache{
    	
	private static class ServiceHolder{ 
		private static WifiActionService instance =new WifiActionService(); 
	}
	/**
	 * 获得工厂单例
	 * @return
	 */
	public static WifiActionService getInstance() {
	    return ServiceHolder.instance;
	}
    	
	private WifiActionService() {
	}
	
	private String generateKey(String bssid){
		StringBuilder sb = new StringBuilder();
		sb.append(BusinessKeyDefine.Social.ACTION).append(bssid);
		return sb.toString();
	}
	
	public Map<String, String> Counts(String bssid){
	    	return this.hgetall(generateKey(bssid));
	}
	
//	public void hadd(String bssid, Map<String,String> map) {
//	    this.hhmset(bssid, map);
//	}
	
	
	@Override
	public String getRedisKey() {
	    return null;
	}
	
	@Override
	public String getName() {
	    return WifiActionService.class.getName();
	}
	
	@Override
	public JedisPool getRedisPool() {
	    return RedisPoolManager.getInstance().getPool(RedisKeyEnum.DEFAULT);
	}
	
	
	public static void main(String[] args) {
//		Map<String,String> map = WifiActionService.getInstance().actions("84:82:f4:28:7a:ec");
		
//		if (map.isEmpty()) {
//		    System.out.println("true");
//		}
	}

}
