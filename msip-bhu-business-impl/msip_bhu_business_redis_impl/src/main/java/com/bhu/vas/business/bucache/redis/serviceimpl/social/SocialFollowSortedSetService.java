package com.bhu.vas.business.bucache.redis.serviceimpl.social;

import com.bhu.vas.business.bucache.redis.serviceimpl.BusinessKeyDefine;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.RedisKeyEnum;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.RedisPoolManager;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.impl.AbstractRelationSortedSetCache;

import redis.clients.jedis.JedisPool;

public class SocialFollowSortedSetService
	extends AbstractRelationSortedSetCache {

    private static class ServiceHolder {
	private static SocialFollowSortedSetService instance = new SocialFollowSortedSetService();
    }
    
    /**
     * 获得工厂单例
     * 
     * @return
     */
    public static SocialFollowSortedSetService getInstance() {
	return ServiceHolder.instance;
    }

    public SocialFollowSortedSetService() {
    }

    private String generateKey(long uid){
	StringBuilder sb = new StringBuilder();
	sb.append(BusinessKeyDefine.Social.RELATION).append(uid);
	return sb.toString();
    }
    
    @Override
    public String getName() {
	return SocialFollowSortedSetService.class.getName();
    }

    @Override
    public String getRedisKey() {
	return null;
    }

    @Override
    public JedisPool getRedisPool() {
	return RedisPoolManager.getInstance().getPool(RedisKeyEnum.DEFAULT);
    }
    
    public void follow(long uid,String hd_mac) {
	this.zadd(generateKey(uid), System.currentTimeMillis(), hd_mac);
    }
    
    public void unFollow(long uid,String hd_mac) {
	this.zrem(generateKey(uid), hd_mac);
    }
}
