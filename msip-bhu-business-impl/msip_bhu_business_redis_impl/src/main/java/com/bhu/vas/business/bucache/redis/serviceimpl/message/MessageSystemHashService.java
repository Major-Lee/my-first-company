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
    
    private static String generateKey(String key, String utype){
    	StringBuilder sb = new StringBuilder();
    	sb.append(BusinessKeyDefine.Message.Key);
    	sb.append(utype);
    	sb.append(key);
        return sb.toString();
    }
    
    public String fetchMessageUserSig(String key, String utype){
		return this.hget(generateKey(key, utype),BusinessKeyDefine.Message.FieldSig);
	}
    
    
    public Long setMessageUserSig(String key, String utype, String sig){
		return this.hset(generateKey(key, utype),BusinessKeyDefine.Message.FieldSig, sig);
	}
    
    public Long setMessageUserTag(String key, String utype, String tags){
		return this.hset(generateKey(key, utype),BusinessKeyDefine.Message.FieldTags, tags);
	}
    
    public String fetchMessageUserTag(String key, String utype){
		return this.hget(generateKey(key, utype),BusinessKeyDefine.Message.FieldSig);
	}
    
    public Long setMessageUserSigExpire(String key, String utype, String expire){
		return this.expire(generateKey(key, utype), Integer.parseInt(expire));
	}
    
    public Long getMessageUserSigExpire(String key, String utype, String expire){
		return this.ttl(generateKey(key, utype));
	}
    
	public Map<String, String> fetchAll(String key, String utype){
		return this.hgetall(generateKey(key, utype));
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
