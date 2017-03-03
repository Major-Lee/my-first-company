package com.bhu.vas.business.bucache.redis.serviceimpl.advertise;

import redis.clients.jedis.JedisPool;

import com.bhu.vas.business.bucache.redis.serviceimpl.BusinessKeyDefine;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.RedisKeyEnum;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.RedisPoolManager;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.impl.AbstractRelationHashCache;
import com.smartwork.msip.cores.helper.DateTimeHelper;

public class AdvertiseUserCPMCheckHashService extends AbstractRelationHashCache{

    private static class ServiceHolder{
        private static AdvertiseUserCPMCheckHashService instance =new AdvertiseUserCPMCheckHashService();
    }
    
    /**
     * 获取工厂单例
     * @return
     */
    public static AdvertiseUserCPMCheckHashService getInstance() {
        return ServiceHolder.instance;
    }

    private static String generateKey(String adid,String day){
        StringBuilder sb = new StringBuilder(BusinessKeyDefine.Advertise.AdvertiseCPMCheck);
        sb.append(adid).append(day);
        return sb.toString();
    }
    
    public boolean checkUserCpm(String adid,String userid,String sourcetype){
    	 return this.hexists(generateKey(adid, DateTimeHelper.getDateTime("DD")), userid+sourcetype);
    }
	
    public void userCpmNotify(String adid,String userid,String sourcetype){
    	this.hset(generateKey(adid, DateTimeHelper.getDateTime("DD")), userid, DateTimeHelper.getDateTime(DateTimeHelper.FormatPattern0));
    	this.expire(generateKey(adid, DateTimeHelper.getDateTime("DD")), 24*3600);
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
