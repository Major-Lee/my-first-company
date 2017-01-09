package com.bhu.vas.business.bucache.redis.serviceimpl.commdity;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Pipeline;

import java.util.ArrayList;
import java.util.List;

import com.bhu.vas.business.bucache.redis.serviceimpl.BusinessKeyDefine;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.RedisKeyEnum;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.RedisPoolManager;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.impl.AbstractRelationStringCache;
import com.smartwork.msip.cores.helper.DateTimeHelper;
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

    private String generateCurrentHourKey(long hour) {
    	StringBuilder key = new StringBuilder();
    	key.append(BusinessKeyDefine.Commdity.OrdersRecent7DaysKey);
    	key.append(StringHelper.UNDERLINE_STRING_GAP);
    	key.append(hour);
    	return key.toString();
    }
    
    private String generateCurrentHourKey() {
    	return generateCurrentHourKey(System.currentTimeMillis()/3600000);
    }
    
    private List<String> generateOrdersRecent7DaysKey(){
    	List<String> list = new ArrayList<String>();
    	long currentHour = System.currentTimeMillis()/3600000;
    	long hour7DaysAgo = DateTimeHelper.getDateDaysAgo(7).getTime()/3600000;
    	for(long i = hour7DaysAgo; i <= currentHour; i++){
    		list.add(generateCurrentHourKey(i));
    	}
    	return list;
    }
    
    public long incrOrdersRecent7DaysKey(){
    	long count = 0;
    	if (!super.exists(generateCurrentHourKey())){
    		count =  super.incr(generateCurrentHourKey());
    		super.expire(generateCurrentHourKey(), 604800);
    	}else{
    		count =  super.incr(generateCurrentHourKey());
    	}
    	return count;
    }
    
    public long fetchOrdersFinishRecent7Days(){
    	List<String> keys = generateOrdersRecent7DaysKey();
    	Jedis jedis = this.getRedisPool().getResource();
        Pipeline pipeline = jedis.pipelined();  
        for(String key : keys){
        	pipeline.get(key);
        }    
        List<Object> all = pipeline.syncAndReturnAll();
        long ordersCount = 0;
        for(Object hour : all){
        	if (hour != null)
        		ordersCount = ordersCount + Long.parseLong((String)hour);
        }
        return ordersCount;
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
