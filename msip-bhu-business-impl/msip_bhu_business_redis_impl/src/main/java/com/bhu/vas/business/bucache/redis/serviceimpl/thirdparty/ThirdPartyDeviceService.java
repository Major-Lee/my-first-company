package com.bhu.vas.business.bucache.redis.serviceimpl.thirdparty;

import redis.clients.jedis.JedisPool;

import java.util.Map;

import com.bhu.vas.business.bucache.redis.serviceimpl.BusinessKeyDefine;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.RedisKeyEnum;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.RedisPoolManager;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.impl.AbstractRelationHashCache;

/**
 * 第三方设备再redis中的存储
 * @author yetao
 *
 */
public class ThirdPartyDeviceService extends AbstractRelationHashCache{
    private static class ServiceHolder{
        private static ThirdPartyDeviceService instance = new ThirdPartyDeviceService();
    }
    /**
     * 获取工厂单例
     * @return
     */
    public static ThirdPartyDeviceService getInstance() {
        return ServiceHolder.instance;
    }
    private ThirdPartyDeviceService(){
    	
    }
    
    private static String generateKey(String mac){
    	StringBuilder sb = new StringBuilder();
    	sb.append(BusinessKeyDefine.ThirdParty.DeviceKey);
    	sb.append(mac);
        return sb.toString();
    }
    
    public static Integer getBindedCount(String mac){
    	String ret =  ThirdPartyDeviceService.getInstance().hget(generateKey(mac),BusinessKeyDefine.ThirdParty.FieldBinded);
    	if(ret == null)
    		return null;
    	return Integer.parseInt(ret);
	}
    public static void bindDevice(String mac){
    	ThirdPartyDeviceService.getInstance().hincrby(generateKey(mac),BusinessKeyDefine.ThirdParty.FieldBinded, 1);
	}
    
    public static void unBindDevice(String mac){
    	ThirdPartyDeviceService.getInstance().hincrby(generateKey(mac),BusinessKeyDefine.ThirdParty.FieldBinded, -1);
	}
    
	public static Map<String, String> fetchAll(String key){
		return ThirdPartyDeviceService.getInstance().hgetall(generateKey(key));
	}
	
    @Override
    public String getRedisKey() {
        return null;
    }
    @Override
    public String getName() {
        return ThirdPartyDeviceService.class.getName();
    }
    
    @Override
    public JedisPool getRedisPool() {
        return RedisPoolManager.getInstance().getPool(RedisKeyEnum.ThirdPartyAPI);
    }

}
