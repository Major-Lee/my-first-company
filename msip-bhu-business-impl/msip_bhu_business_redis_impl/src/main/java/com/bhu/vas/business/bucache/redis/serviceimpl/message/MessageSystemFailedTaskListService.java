package com.bhu.vas.business.bucache.redis.serviceimpl.message;

import redis.clients.jedis.JedisPool;

import com.smartwork.msip.cores.cache.relationcache.impl.jedis.RedisKeyEnum;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.RedisPoolManager;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.impl.AbstractRelationListCache;

/**
 * 用于存放消息系统失败的推送记录
 * @author fengshibo
 *
 */
public class MessageSystemFailedTaskListService extends AbstractRelationListCache{
    private static class ServiceHolder{
        private static MessageSystemFailedTaskListService instance = new MessageSystemFailedTaskListService();
    }
    public static final String failedTaskKey = "FAIL_TASK";
    /**
     * 获取工厂单例
     * @return
     */
    public static MessageSystemFailedTaskListService getInstance() {
        return ServiceHolder.instance;
    }
    private MessageSystemFailedTaskListService(){
    	
    }
    
    private String generateFailedTaskKey(){
    	return failedTaskKey;
    }
    
    public String lpopFailedTaskKey(){
    	return super.lpop(generateFailedTaskKey());
    }
    
    public Long rpushFailedTaskKey(String notify_message){
    	return super.rpush(generateFailedTaskKey(), notify_message);
    }
    
    @Override
    public String getRedisKey() {
        return null;
    }
    @Override
    public String getName() {
        return MessageSystemHashService.class.getName();
    }
    
    @Override
    public JedisPool getRedisPool() {
        return RedisPoolManager.getInstance().getPool(RedisKeyEnum.MESSAGESYS);
    }
    

}
