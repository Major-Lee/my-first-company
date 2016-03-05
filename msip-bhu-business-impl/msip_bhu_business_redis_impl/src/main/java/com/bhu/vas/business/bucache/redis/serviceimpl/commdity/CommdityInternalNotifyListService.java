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
public class CommdityInternalNotifyListService extends AbstractRelationListCache {
    private static class ServiceHolder{
        private static CommdityInternalNotifyListService instance =new CommdityInternalNotifyListService();
    }
    /**
     * 获取工厂单例
     * @return
     */
    public static CommdityInternalNotifyListService getInstance() {
        return ServiceHolder.instance;
    }

    private CommdityInternalNotifyListService(){
    	
    }

    private String generateOrderPaymentNotifyKey() {
    	return BusinessKeyDefine.Commdity.OrderPaymentNotifyKey;
    }
    
    private String generateOrderDeliverNotifyKey() {
    	return BusinessKeyDefine.Commdity.OrderDeliverNotifyKey;
    }

    private String generateWithdrawAppliesRequestNotifyKey() {
    	return BusinessKeyDefine.Commdity.WithdrawAppliesRequestNotifyKey;
    }
    
    public String lpopOrderPaymentNotify(){
    	return super.lpop(generateOrderPaymentNotifyKey());
    }
    
    public void rpushOrderPaymentNofity(String notify_message){
    	super.rpush(generateOrderPaymentNotifyKey(), notify_message);
    }
    
    public Long rpushOrderDeliverNofity(String notify_message){
    	return super.rpush(generateOrderDeliverNotifyKey(), notify_message);
    }
    
    public void rpushWithdrawAppliesRequestNotify(String notify_message){
    	super.rpush(generateWithdrawAppliesRequestNotifyKey(), notify_message);
    }
    
    @Override
    public String getRedisKey() {
        return null;
    }

    @Override
    public String getName() {
        return CommdityInternalNotifyListService.class.getName();
    }
    @Override
    public JedisPool getRedisPool() {
        return RedisPoolManager.getInstance().getPool(RedisKeyEnum.COMMDITY);
    }


    public static void main(String[] args) {
        //System.out.println(OrderPaymentNotifyListService.getInstance().pipelineHSet_sameKeyWithDiffFieldValue("TTTT", new String[]{"TT1,TT2"}, new String[]{"1", "2"}));

        //WifiDeviceModuleStatService.getInstance().hset("TTTT", "T", "1234");
    	//CommdityInternalNotifyListService.getInstance().rpushOrderPaymentNofity(OrderPaymentNotifyDTO);
    }


}
