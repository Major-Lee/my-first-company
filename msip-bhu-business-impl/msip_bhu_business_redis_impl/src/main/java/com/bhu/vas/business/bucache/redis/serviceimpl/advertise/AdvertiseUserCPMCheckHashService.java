package com.bhu.vas.business.bucache.redis.serviceimpl.advertise;

import redis.clients.jedis.JedisPool;

import com.smartwork.msip.cores.cache.relationcache.impl.jedis.RedisKeyEnum;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.RedisPoolManager;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.impl.AbstractRelationHashCache;

public class AdvertiseUserCPMCheckHashService extends AbstractRelationHashCache{

	
	
	@Override
	public JedisPool getRedisPool() {
		return RedisPoolManager.getInstance().getPool(RedisKeyEnum.ADVERTISE);	
	}

	@Override
	public String getRedisKey() {
		return null;
	}

	@Override
	public String getName() {
		return AdvertisePortalHashService.class.getName();
	}
}
