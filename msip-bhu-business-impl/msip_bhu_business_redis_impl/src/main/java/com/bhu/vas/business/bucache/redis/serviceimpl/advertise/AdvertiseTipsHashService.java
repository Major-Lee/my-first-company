   package com.bhu.vas.business.bucache.redis.serviceimpl.advertise;

import java.util.Map;

import redis.clients.jedis.JedisPool;

import com.bhu.vas.business.bucache.redis.serviceimpl.BusinessKeyDefine;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.RedisKeyEnum;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.RedisPoolManager;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.impl.AbstractRelationHashCache;

public class AdvertiseTipsHashService extends AbstractRelationHashCache{

    private static class ServiceHolder{
        private static AdvertiseTipsHashService instance =new AdvertiseTipsHashService();
    }
    
    /**
     * 获取工厂单例
     * @return
     */
    public static AdvertiseTipsHashService getInstance() {
        return ServiceHolder.instance;
    }

    private static String generateKey(int uid){
        StringBuilder sb = new StringBuilder(BusinessKeyDefine.Advertise.AdvertiseTips);
        sb.append(uid);
        return sb.toString();
    }
    
    public void adComment(int uid,String adid,String event){
    	this.hset(generateKey(uid), adid, event);
    }
    
    public Map<String, String> fetchAdTips(int uid){
    	return this.hgetall(generateKey(uid));
    }
    
    public void destoryTips(int uid,String adid){
    	this.hdel(generateKey(uid), adid);
    }
    
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
		return AdvertiseTipsHashService.class.getName();
	}
}
