package com.bhu.vas.business.bucache.redis.serviceimpl.advertise;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.bhu.vas.business.bucache.redis.serviceimpl.BusinessKeyDefine;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.RedisKeyEnum;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.RedisPoolManager;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.impl.AbstractRelationListCache;

import redis.clients.jedis.JedisPool;

/**
 * 
 * @author xiaowei
 *
 */
public class WifiDeviceAdvertiseListService extends AbstractRelationListCache{

    private static class ServiceHolder{
        private static WifiDeviceAdvertiseListService instance =new WifiDeviceAdvertiseListService();
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
    
    public void wifiDevicesAdInvalid(List<String> macs){
    	List<String> keys = generateKeys(macs);
    	this.lpop_pipeline(keys,1);
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
    
    public void wifiDevicesAdApply(List<String> macs,String message){
    	List<String> keys = generateKeys(macs);
    	this.lpop_pipeline(keys, 1);
    	this.lpush_pipeline_samevalue(keys, message);
    }
    
    /**
     * 获取工厂单例
     * @return
     */
    public static WifiDeviceAdvertiseListService getInstance() {
        return ServiceHolder.instance;
    }
    
	@Override
	public String getName() {
		return WifiDeviceAdvertiseListService.class.getName();
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
