package com.bhu.vas.business.bucache.redis.serviceimpl.statistics;

import com.bhu.vas.business.bucache.redis.serviceimpl.BusinessKeyDefine;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.RedisKeyEnum;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.RedisPoolManager;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.impl.AbstractRelationHashCache;
import redis.clients.jedis.JedisPool;

/**
 * Created by bluesand on 5/26/15.
 */
public class VapModeHashService extends AbstractRelationHashCache {

    private static class ServiceHolder{
        private static VapModeHashService instance =new VapModeHashService();
    }
    /**
     * 获取工厂单例
     * @return
     */
    public static VapModeHashService getInstance() {
        return ServiceHolder.instance;
    }

    private VapModeHashService(){
    }

    private static String generatePrefixKey(String key){
        return new StringBuilder(BusinessKeyDefine.VapMode.VapModeCount).append(key).toString();
    }

    public Long incrStatistics(String key,String field,long increment){
        return this.hincrby(generatePrefixKey(key), field, increment);
    }

    @Override
    public String getRedisKey() {
        return null;
    }
    @Override
    public String getName() {
        return VapModeHashService.class.getName();
    }
    @Override
    public JedisPool getRedisPool() {
        return RedisPoolManager.getInstance().getPool(RedisKeyEnum.PRESENT);
    }

    public static void main(String[] args) {

        VapModeHashService.getInstance().incrStatistics("htmlad","style001",1);
        System.out.println(VapModeHashService.getInstance().hget(generatePrefixKey("htmlad"), "style001"));
    }
}
