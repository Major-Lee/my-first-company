package com.bhu.vas.business.bucache.redis.serviceimpl.commdity;

import redis.clients.jedis.JedisPool;

import com.bhu.vas.business.bucache.redis.serviceimpl.BusinessKeyDefine;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.RedisKeyEnum;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.RedisPoolManager;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.impl.AbstractRelationStringCache;
import com.smartwork.msip.cores.helper.StringHelper;

/**
 * 用户存放完成订单的统计数量
 * @author fengshibo
 *
 */
public class OrdersFinishCountStringService extends AbstractRelationStringCache {
    private static class ServiceHolder{
        private static OrdersFinishCountStringService instance = new OrdersFinishCountStringService();
    }
    /**
     * 获取工厂单例
     * @return
     */
    public static OrdersFinishCountStringService getInstance() {
        return ServiceHolder.instance;
    }

    private OrdersFinishCountStringService(){
    	
    }

    private String generateRecent7DaysKey() {
    	long currentHour = System.currentTimeMillis()/3600000;
    	StringBuilder key = new StringBuilder();
    	key.append(BusinessKeyDefine.Commdity.OrdersRecent7DaysKey);
    	key.append(StringHelper.UNDERLINE_STRING_GAP);
    	key.append(currentHour);
    	return key.toString();
    }
    
    public long incrOrdersRecent7DaysKey(){
    	long count = 0;
    	if (!super.exists(generateRecent7DaysKey())){
    		count =  super.incr(generateRecent7DaysKey());
    		super.expire(generateRecent7DaysKey(), 604800);
    	}else{
    		count =  super.incr(generateRecent7DaysKey());
    	}
    	
    	return count;
    }
    
    @Override
    public String getRedisKey() {
        return null;
    }

    @Override
    public String getName() {
        return OrdersFinishCountStringService.class.getName();
    }
    @Override
    public JedisPool getRedisPool() {
        return RedisPoolManager.getInstance().getPool(RedisKeyEnum.COMMDITY);
    }
}
