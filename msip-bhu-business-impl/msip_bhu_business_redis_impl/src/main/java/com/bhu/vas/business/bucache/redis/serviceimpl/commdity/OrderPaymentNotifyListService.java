package com.bhu.vas.business.bucache.redis.serviceimpl.commdity;

import redis.clients.jedis.JedisPool;

import com.bhu.vas.business.bucache.redis.serviceimpl.BusinessKeyDefine;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.RedisKeyEnum;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.RedisPoolManager;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.impl.AbstractRelationListCache;

/**
 * 用于存放支付系统支付成功的通知消息
 * @author tangzichao
 *
 */
public class OrderPaymentNotifyListService extends AbstractRelationListCache {
    private static class ServiceHolder{
        private static OrderPaymentNotifyListService instance =new OrderPaymentNotifyListService();
    }
    /**
     * 获取工厂单例
     * @return
     */
    public static OrderPaymentNotifyListService getInstance() {
        return ServiceHolder.instance;
    }

    private OrderPaymentNotifyListService(){
    	
    }

    private String generateKey() {
    	return BusinessKeyDefine.Commdity.OrderPaymentNotifyKey;
    }

    public String lpopNotify(){
    	return super.lpop(generateKey());
    }
    
    public void rpushNofity(String notify_message){
    	super.rpush(generateKey(), notify_message);
    }
    
    @Override
    public String getRedisKey() {
        return null;
    }

    @Override
    public String getName() {
        return OrderPaymentNotifyListService.class.getName();
    }
    @Override
    public JedisPool getRedisPool() {
        return RedisPoolManager.getInstance().getPool(RedisKeyEnum.COMMDITY);
    }


    public static void main(String[] args) {
        //System.out.println(OrderPaymentNotifyListService.getInstance().pipelineHSet_sameKeyWithDiffFieldValue("TTTT", new String[]{"TT1,TT2"}, new String[]{"1", "2"}));

        //WifiDeviceModuleStatService.getInstance().hset("TTTT", "T", "1234");
    	OrderPaymentNotifyListService.getInstance().rpushNofity("01002016030300000000000000000048");
    }


}
