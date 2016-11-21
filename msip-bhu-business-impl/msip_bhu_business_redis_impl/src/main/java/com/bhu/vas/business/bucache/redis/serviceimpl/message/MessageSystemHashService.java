package com.bhu.vas.business.bucache.redis.serviceimpl.message;

import redis.clients.jedis.JedisPool;

import java.util.Map;

import com.bhu.vas.business.bucache.redis.serviceimpl.BusinessKeyDefine;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.RedisKeyEnum;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.RedisPoolManager;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.impl.AbstractRelationHashCache;

/**
 * 用于存放消息系统Usersig
 * @author fengshibo
 *
 */
public class MessageSystemHashService extends AbstractRelationHashCache{
    private static class ServiceHolder{
        private static MessageSystemHashService instance = new MessageSystemHashService();
    }
    /**
     * 获取工厂单例
     * @return
     */
    public static MessageSystemHashService getInstance() {
        return ServiceHolder.instance;
    }
    private MessageSystemHashService(){
    	
    }
    
    private static String generateKey(String key){
        return new StringBuilder(BusinessKeyDefine.Message.MessageUserSig).append(key).toString();
    }
    
    public String fetchMessageUserSig(String key){
		return this.hget(generateKey(key),BusinessKeyDefine.Message.MessageFieldSig);
	}
    
    public Long setMessageUserSig(String key, String sig){
		return this.hset(generateKey(key),BusinessKeyDefine.Message.MessageFieldSig,sig);
	}
    
	public Map<String, String> fetchAll(String key){
		return this.hgetall(generateKey(key));
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
        return RedisPoolManager.getInstance().getPool(RedisKeyEnum.COMMDITY);
    }

}