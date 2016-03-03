package com.bhu.vas.business.bucache.redis.serviceimpl.social;

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
}
