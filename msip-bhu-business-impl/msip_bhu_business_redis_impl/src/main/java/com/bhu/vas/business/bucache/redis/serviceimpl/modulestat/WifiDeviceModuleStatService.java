package com.bhu.vas.business.bucache.redis.serviceimpl.modulestat;

import com.smartwork.msip.cores.cache.relationcache.impl.jedis.RedisKeyEnum;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.RedisPoolManager;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.impl.AbstractRelationHashCache;
import redis.clients.jedis.JedisPool;

/**
 * Created by bluesand on 12/1/15.
 */
public class WifiDeviceModuleStatService extends AbstractRelationHashCache {
    private static class ServiceHolder{
        private static WifiDeviceModuleStatService instance =new WifiDeviceModuleStatService();
    }
    /**
     * 获取工厂单例
     * @return
     */
    public static WifiDeviceModuleStatService getInstance() {
        return ServiceHolder.instance;
    }

    private WifiDeviceModuleStatService(){}


    @Override
    public String getRedisKey() {
        return null;
    }

    @Override
    public String getName() {
        return WifiDeviceModuleStatService.class.getName();
    }
    @Override
    public JedisPool getRedisPool() {
        return RedisPoolManager.getInstance().getPool(RedisKeyEnum.HANDSETPRESENT);
    }
}
