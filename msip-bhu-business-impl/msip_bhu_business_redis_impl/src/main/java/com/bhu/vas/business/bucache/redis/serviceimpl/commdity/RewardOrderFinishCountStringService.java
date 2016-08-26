package com.bhu.vas.business.bucache.redis.serviceimpl.commdity;

import redis.clients.jedis.JedisPool;

import com.bhu.vas.business.bucache.redis.serviceimpl.BusinessKeyDefine;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.RedisKeyEnum;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.RedisPoolManager;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.impl.AbstractRelationStringCache;

/**
 * 用户存放统计打赏订单的统计数量
 * @author tangzichao
 *
 */
public class RewardOrderFinishCountStringService extends AbstractRelationStringCache {
    private static class ServiceHolder{
        private static RewardOrderFinishCountStringService instance =new RewardOrderFinishCountStringService();
    }
    /**
     * 获取工厂单例
     * @return
     */
    public static RewardOrderFinishCountStringService getInstance() {
        return ServiceHolder.instance;
    }

    private RewardOrderFinishCountStringService(){
    	
    }

    private String generateRecent7DaysKey() {
    	return BusinessKeyDefine.Commdity.RewardOrderRecent7DaysKey;
    }
 
    public void refreshRecent7daysValue(Integer count){
    	super.set(generateRecent7DaysKey(), String.valueOf(count));
    	super.expire(generateRecent7DaysKey(), 24 * 3600);
    }
    
    @Override
    public String getRedisKey() {
        return null;
    }

    @Override
    public String getName() {
        return RewardOrderFinishCountStringService.class.getName();
    }
    @Override
    public JedisPool getRedisPool() {
        return RedisPoolManager.getInstance().getPool(RedisKeyEnum.COMMDITY);
    }
    
    public String getRecent7daysValue(){
    	return super.get(generateRecent7DaysKey());
    }

}
