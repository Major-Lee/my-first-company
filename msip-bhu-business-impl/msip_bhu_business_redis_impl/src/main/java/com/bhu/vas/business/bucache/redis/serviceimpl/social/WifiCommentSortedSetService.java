package com.bhu.vas.business.bucache.redis.serviceimpl.social;

import java.util.Collections;
import java.util.Set;

import org.springframework.util.StringUtils;

import com.bhu.vas.business.bucache.redis.serviceimpl.BusinessKeyDefine;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.RedisKeyEnum;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.RedisPoolManager;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.impl.AbstractRelationSortedSetCache;

import redis.clients.jedis.JedisPool;

public class WifiCommentSortedSetService  extends AbstractRelationSortedSetCache{
	
	private static class ServiceHolder{ 
		private static WifiCommentSortedSetService instance =new WifiCommentSortedSetService(); 
	}
	
	
	//初始用户评论wifi个数 100亿 
		public static final double WifiCout = 10000000000d;
	/**
	 * 获得工厂单例
	 * @return
	 */
	public static WifiCommentSortedSetService getInstance() {
	    return ServiceHolder.instance;
	}
    	
	private WifiCommentSortedSetService() {
	}
	
	private String generateKey(String bssid){
		StringBuilder sb = new StringBuilder();
		sb.append(BusinessKeyDefine.Social.COMMENT).append(bssid);
		return sb.toString();
	}
    
	public long addUserWifi(String uid, double createTime, String  bssid){
		return super.zadd(generateKey(uid),createTime , bssid);
	}
	
	public Set<String> fetchUserWifiList(String uid){
		if(StringUtils.isEmpty(uid)) return Collections.emptySet();
		  return super.zrevrange(generateKey(uid), 0, -1);		
	}
	@Override
	public String getName() {
		 return WifiCommentSortedSetService.class.getName();
	}

	@Override
	public String getRedisKey() {
		
		return null;
	}

	@Override
	public JedisPool getRedisPool() {
		return RedisPoolManager.getInstance().getPool(RedisKeyEnum.SOCIAL);
	}
	
	
	

}
