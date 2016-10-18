package com.bhu.vas.business.bucache.redis.serviceimpl.advertise;

import redis.clients.jedis.JedisPool;

import com.smartwork.msip.cores.cache.relationcache.impl.jedis.RedisKeyEnum;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.RedisPoolManager;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.impl.AbstractRelationListCache;

public class WifiDeviceAdvertiseListService extends AbstractRelationListCache{

    private static class ServiceHolder{
        private static WifiDeviceAdvertiseListService instance =new WifiDeviceAdvertiseListService();
    }
	
    /**
     * 获取工厂单例
     * @return
     */
    public static WifiDeviceAdvertiseListService getInstance() {
        return ServiceHolder.instance;
    }
    
	@Override
	public String getName() {
		return WifiDeviceAdvertiseListService.class.getName();
	}

	@Override
	public String getRedisKey() {
		return null;
	}

	@Override
	public JedisPool getRedisPool() {
		return RedisPoolManager.getInstance().getPool(RedisKeyEnum.ADVERTISE);
	}
}
