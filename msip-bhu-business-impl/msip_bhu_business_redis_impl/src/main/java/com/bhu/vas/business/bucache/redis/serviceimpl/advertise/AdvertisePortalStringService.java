package com.bhu.vas.business.bucache.redis.serviceimpl.advertise;

import java.util.ArrayList;
import java.util.List;

import redis.clients.jedis.JedisPool;

import com.smartwork.msip.cores.cache.relationcache.impl.jedis.RedisKeyEnum;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.RedisPoolManager;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.impl.AbstractRelationStringCache;

public class AdvertisePortalStringService extends AbstractRelationStringCache{
	
	public final static String adPortalPv = "pv:";
	public final static String adPortalAct = "act:";
	
    private static class ServiceHolder{
        private static AdvertisePortalStringService instance =new AdvertisePortalStringService();
    }
    
    /**
     * 获取工厂单例
     * @return
     */
    public static AdvertisePortalStringService getInstance() {
        return ServiceHolder.instance;
    }
	
	public  List<String> queryAdvertisePV(List<String> extparams){
		List<String> keys = new ArrayList<String>();
		List<Object> values = null;
		List<String> result = new ArrayList<String>();
		for(String ext : extparams){
			int index = ext.indexOf(".html");
			if(index != -1){
				keys.add(adPortalPv+ext.substring(index - 13, index));
			}else{
				keys.add(adPortalPv);
			}
		}
		values = this.pipelineGet_diffKey(keys);
		for(Object obj : values){
			if(obj == null){
				result.add("0");
			}else{
				result.add(obj.toString());
			}
		}
		
		return result;
	}
	
	public  List<String> queryAdvertiseAct(List<String> extparams){
		List<String> keys = new ArrayList<String>();
		List<Object> values = null;
		List<String> result = new ArrayList<String>();
		for(String ext : extparams){
			int index = ext.indexOf(".html");
			if(index != -1){
				keys.add(adPortalAct+ext.substring(index - 13, index));
			}else{
				keys.add(adPortalAct);
			}
		}
		values = this.pipelineGet_diffKey(keys);
		for(Object obj : values){
			if(obj == null){
				result.add("0");
			}else{
				result.add(obj.toString());
			}
		}
		return result;
	}

	@Override
	public JedisPool getRedisPool() {
		return RedisPoolManager.getInstance().getPool(RedisKeyEnum.ADVERTISE);	
	}

	@Override
	public String getRedisKey() {
		return null;
	}

	@Override
	public String getName() {
		return AdvertisePortalStringService.class.getName();
	}
}
