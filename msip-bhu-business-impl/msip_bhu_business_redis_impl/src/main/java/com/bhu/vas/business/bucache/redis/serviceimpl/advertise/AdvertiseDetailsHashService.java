package com.bhu.vas.business.bucache.redis.serviceimpl.advertise;

import java.util.Map;

import redis.clients.jedis.JedisPool;

import com.bhu.vas.business.bucache.redis.serviceimpl.BusinessKeyDefine;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.RedisKeyEnum;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.RedisPoolManager;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.impl.AbstractRelationHashCache;

public class AdvertiseDetailsHashService extends AbstractRelationHashCache{

    private static class ServiceHolder{
        private static AdvertiseDetailsHashService instance =new AdvertiseDetailsHashService();
    }
    
    /**
     * 获取工厂单例
     * @return
     */
    public static AdvertiseDetailsHashService getInstance() {
        return ServiceHolder.instance;
    }

    private static String generateKey(String adid){
        StringBuilder sb = new StringBuilder(BusinessKeyDefine.Advertise.AdvertiseDetail);
        sb.append(adid);
        return sb.toString();
    }
    
    public void advertiseInfo(String adid,Map<String, String> map){
    	this.hmset(generateKey(adid), map);
    }
	
	@Override
	public String getName() {
		return AdvertiseDetailsHashService.class.getName();
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
