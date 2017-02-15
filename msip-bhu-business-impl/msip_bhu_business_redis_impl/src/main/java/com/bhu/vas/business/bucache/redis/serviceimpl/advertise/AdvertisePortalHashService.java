package com.bhu.vas.business.bucache.redis.serviceimpl.advertise;

import java.util.ArrayList;
import java.util.List;

import redis.clients.jedis.JedisPool;

import com.bhu.vas.business.bucache.redis.serviceimpl.BusinessKeyDefine;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.RedisKeyEnum;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.RedisPoolManager;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.impl.AbstractRelationHashCache;

public class AdvertisePortalHashService extends AbstractRelationHashCache{
	
	public final static String adPortalPv = "pv";
	public final static String adPortalAct = "act";
	public final static String adPortalReason = "stop_reason";
	
    private static class ServiceHolder{
        private static AdvertisePortalHashService instance =new AdvertisePortalHashService();
    }
    
    /**
     * 获取工厂单例
     * @return
     */
    public static AdvertisePortalHashService getInstance() {
        return ServiceHolder.instance;
    }
    
    private static String generateKey(String adid){
        StringBuilder sb = new StringBuilder(BusinessKeyDefine.Advertise.AdvertiseData);
        sb.append(adid);
        return sb.toString();
    }
	
    private static List<String> generateKeys(List<String> adids){
    	List<String> keys = new ArrayList<String>();
    	for(String adid : adids){
            StringBuilder sb = new StringBuilder(BusinessKeyDefine.Advertise.AdvertiseData);
            sb.append(adid);
            keys.add(sb.toString());
    	}
    	return keys;
    }
    
	public  List<String> queryAdvertisePV(List<String> adids){
		List<String> adpvs = new ArrayList<String>();
		for(String adid : generateKeys(adids)){
			adpvs.add(this.hget(adid, adPortalPv));
		}
		return adpvs;
	}
	
	public  List<String> queryAdvertiseAct(List<String> adids){
		List<String> adacts = new ArrayList<String>();
		for(String adid : generateKeys(adids)){
			adacts.add(this.hget(adid, adPortalAct));
		}
		return adacts;
	}
	
	public void advertiseVerifyFailure(String adid,String msg){
		this.hset(generateKey(adid), adPortalReason, msg);
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
		return AdvertisePortalHashService.class.getName();
	}
}
