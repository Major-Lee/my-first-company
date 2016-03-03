package com.bhu.vas.business.bucache.redis.serviceimpl.social;

import java.util.HashMap;
import java.util.Map;

import com.bhu.vas.api.vto.WifiActionVTO;
import com.bhu.vas.business.bucache.redis.serviceimpl.BusinessKeyDefine;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.RedisKeyEnum;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.RedisPoolManager;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.impl.AbstractRelationHashCache;

import redis.clients.jedis.JedisPool;

public class WifiActionHashService extends AbstractRelationHashCache{
    
    private final static String ACTION_TYPE_UP = "up";
    private final static String ACTION_TYPE_DOWN = "down";
    private final static String ACTION_TYPE_REPORT = "report";
    private final static String COUNT_DEFAULT = "0";
    	
	private static class ServiceHolder{ 
		private static WifiActionHashService instance =new WifiActionHashService(); 
	}
	/**
	 * 获得工厂单例
	 * @return
	 */
	public static WifiActionHashService getInstance() {
	    return ServiceHolder.instance;
	}
    	
	private WifiActionHashService() {
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
	    entity.setUp(map.get(ACTION_TYPE_UP));
	    entity.setDown(map.get(ACTION_TYPE_DOWN));
	    entity.setReport(map.get(ACTION_TYPE_REPORT));
	    return entity;
	}
	
	public void hadd(String bssid) {
	    Map<String,String> map = new HashMap<String,String>();
	    map.put(ACTION_TYPE_UP, COUNT_DEFAULT);
	    map.put(ACTION_TYPE_DOWN, COUNT_DEFAULT);
	    map.put(ACTION_TYPE_REPORT, COUNT_DEFAULT);
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
	    return WifiActionHashService.class.getName();
	}
	
	@Override
	public JedisPool getRedisPool() {
	    return RedisPoolManager.getInstance().getPool(RedisKeyEnum.DEFAULT);
	}
	
}