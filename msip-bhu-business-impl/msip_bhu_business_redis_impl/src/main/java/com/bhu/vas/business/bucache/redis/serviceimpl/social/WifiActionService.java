package com.bhu.vas.business.bucache.redis.serviceimpl.social;

import java.util.Map;

import com.bhu.vas.api.vto.WifiActionVTO;
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
	
	public boolean isNoExist(String bssid) {
	    if ((this.hgetall(generateKey(bssid)).isEmpty())) {
		return true;
	    }
	    return false;
	}
	
	public WifiActionVTO counts(String bssid){
	    Map<String,String> map = this.hgetall(generateKey(bssid));
	    WifiActionVTO entity = new WifiActionVTO();
	    entity.setUp(map.get("up"));
	    entity.setDown(map.get("down"));
	    entity.setReport(map.get("report"));
	    return entity;
	}
	
	public void hadd(String bssid, Map<String,String> map) {
	    this.hmset(generateKey(bssid), map);
	}
	
	public void hincrease(String bssid,String field) {
	    this.hincrby(generateKey(bssid), field, 1);
	}
	
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
	
}
