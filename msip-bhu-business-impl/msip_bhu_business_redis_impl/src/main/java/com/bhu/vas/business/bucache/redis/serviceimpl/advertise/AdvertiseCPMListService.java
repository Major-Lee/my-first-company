package com.bhu.vas.business.bucache.redis.serviceimpl.advertise;

import redis.clients.jedis.JedisPool;

import com.bhu.vas.business.bucache.redis.serviceimpl.BusinessKeyDefine;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.RedisKeyEnum;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.RedisPoolManager;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.impl.AbstractRelationListCache;

public class AdvertiseCPMListService  extends AbstractRelationListCache{

    private static class ServiceHolder{
        private static AdvertiseCPMListService instance =new AdvertiseCPMListService();
    }
    
    /**
     * 获取工厂单例
     * @return
     */
    public static AdvertiseCPMListService getInstance() {
        return ServiceHolder.instance;
    }
    
    public void AdCPMPosh(String adid){
    	this.lpush(BusinessKeyDefine.Advertise.AdvertiseCPM, adid);
    }
    
    public String AdCPMNotify(){
    	return this.rpop(BusinessKeyDefine.Advertise.AdvertiseCPM);
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
		return AdvertiseCPMListService.class.getName();
	}

}
