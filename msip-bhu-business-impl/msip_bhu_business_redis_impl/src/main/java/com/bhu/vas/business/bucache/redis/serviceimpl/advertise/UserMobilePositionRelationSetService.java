package com.bhu.vas.business.bucache.redis.serviceimpl.advertise;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import redis.clients.jedis.JedisPool;

import com.bhu.vas.business.bucache.redis.serviceimpl.BusinessKeyDefine;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.RedisKeyEnum;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.RedisPoolManager;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.impl.AbstractRelationSortedSetCache;

public class UserMobilePositionRelationSetService extends AbstractRelationSortedSetCache{
	
	private static final int DefaultScore = 0; 
	
    private static class ServiceHolder{
        private static UserMobilePositionRelationSetService instance =new UserMobilePositionRelationSetService();
    }

    /**
     * 获取工厂单例
     * @return
     */
    public static UserMobilePositionRelationSetService getInstance() {
        return ServiceHolder.instance;
    }
    
    private static String generateKey(String postion){
        StringBuilder sb = new StringBuilder(BusinessKeyDefine.Advertise.AdvertiseMobilePostion);
        sb.append(postion);
        return sb.toString();
    }
    
    public void mobilenoRecord(String postion,String mobileno){
    	this.zadd(generateKey(postion), DefaultScore, mobileno);
    }
    
    public  Set<String> fetchKeys(String postion){
    	return this.keys(generateKey(postion)+"*");
    }
    
    public List<String> fetchPostionMobileno(String postion){
    	Set<String> keys = fetchKeys(postion);
    	List<String> mobileList = new ArrayList<String>();
    	for(String key : keys){
    		Set<String> mobileSet =   this.zrange(key, 0, -1);
    		mobileList.addAll(mobileSet);
    	}
    	return mobileList;
    }
    
    public int scardPostionMobileno(String postion){
    	Set<String> keys = fetchKeys(postion);
    	int count = 0;
    	for(String key : keys){
    		count += this.zcard(key);
    	}
    	return count;
    }
    
	@Override
	public String getName() {
		return UserMobilePositionRelationSetService.class.getName();
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
