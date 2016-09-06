package com.bhu.vas.business.bucache.redis.serviceimpl.handset;

import redis.clients.jedis.JedisPool;

import com.bhu.vas.business.bucache.redis.serviceimpl.social.SocialHandsetMeetHashService;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.RedisKeyEnum;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.RedisPoolManager;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.impl.AbstractRelationHashCache;

public class HandsetGroupPresentHashService extends AbstractRelationHashCache{

    private static class ServiceHolder{
        private static HandsetGroupPresentHashService instance =new HandsetGroupPresentHashService();
    }
    /**
     * 获取工厂单例
     * @return
     */
    public static HandsetGroupPresentHashService getInstance() {
        return ServiceHolder.instance;
    }

    private HandsetGroupPresentHashService() {
    	
    }

	
	@Override
	public JedisPool getRedisPool() {
		// TODO Auto-generated method stub
        return RedisPoolManager.getInstance().getPool(RedisKeyEnum.STATISTICS);
	}

	@Override
	public String getRedisKey() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getName() {
		return HandsetGroupPresentHashService.class.getName();
	}

}
