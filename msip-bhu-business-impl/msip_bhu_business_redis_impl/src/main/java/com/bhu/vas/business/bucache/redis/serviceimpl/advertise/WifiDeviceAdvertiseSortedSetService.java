package com.bhu.vas.business.bucache.redis.serviceimpl.advertise;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.bhu.vas.business.bucache.redis.serviceimpl.BusinessKeyDefine;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.RedisKeyEnum;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.RedisPoolManager;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.impl.AbstractRelationSortedSetCache;

import redis.clients.jedis.JedisPool;

/**
 * 
 * @author xiaowei
 *
 */
public class WifiDeviceAdvertiseSortedSetService extends AbstractRelationSortedSetCache{

    private static class ServiceHolder{
        private static WifiDeviceAdvertiseSortedSetService instance =new WifiDeviceAdvertiseSortedSetService();
    }
	
    private static String generateKey(String mac){
        StringBuilder sb = new StringBuilder(BusinessKeyDefine.Advertise.ADVERTISE);
        sb.append(mac);
        return sb.toString();
    }
    
    private static List<String> generateKeys(List<String> macs){
        List<String> keys = new ArrayList<String>();
        for(String mac : macs){
        	keys.add(new StringBuilder(BusinessKeyDefine.Advertise.ADVERTISE).append(mac).toString());
        }
        return keys;
    }
    
    public void wifiDevicesAdInvalid(List<String> macs,double score){
    	List<String> keys = generateKeys(macs);
    	for(String key : keys){
    		this.zremrangeByScore(key, score, score);
    	}
    }
    
    public void wifiDevicesAllAdInvalid(){
    	Set<String> keySet = this.keys("ad*");
    	if(!keySet.isEmpty()){
        	String[] keys = new String[keySet.size()];
        	int index = 0;
        	for(String key : keySet){
        		keys[index] = key;
        		index++;
        	}
        	this.del(keys);
    	}
    }
    
    public void wifiDevicesAdApply(List<String> macs,String message,double score){
    	List<String> keys = generateKeys(macs);
    	for(String key : keys){
    		this.zadd(key, score, message);
    	}
    }
    
    /**
     * 获取工厂单例
     * @return
     */
    public static WifiDeviceAdvertiseSortedSetService getInstance() {
        return ServiceHolder.instance;
    }
    
	@Override
	public String getName() {
		return WifiDeviceAdvertiseSortedSetService.class.getName();
	}

	@Override
	public String getRedisKey() {
		return null;
	}

	@Override
	public JedisPool getRedisPool() {
		return RedisPoolManager.getInstance().getPool(RedisKeyEnum.ADVERTISE);
	}
}
