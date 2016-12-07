package com.bhu.vas.business.bucache.redis.serviceimpl.advertise;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import redis.clients.jedis.JedisPool;

import com.bhu.vas.business.bucache.redis.serviceimpl.BusinessKeyDefine;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.RedisKeyEnum;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.RedisPoolManager;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.impl.AbstractRelationSortedSetCache;

public class UserMobilePositionRelationSortedSetService extends AbstractRelationSortedSetCache{
	
	private static final long DefaultScore = 0L; 
	
    private static class ServiceHolder{
        private static UserMobilePositionRelationSortedSetService instance =new UserMobilePositionRelationSortedSetService();
    }

    /**
     * 获取工厂单例
     * @return
     */
    public static UserMobilePositionRelationSortedSetService getInstance() {
        return ServiceHolder.instance;
    }
    
    private static String generateKey(String province,String city ,String district){
        StringBuilder sb = new StringBuilder(BusinessKeyDefine.Advertise.AdvertiseMobilePostion);
        sb.append(province).append(city).append(district);
        return sb.toString();
    }
    
    public void mobilenoRecord(String province,String city ,String district,String mobileno){
    	this.zadd(generateKey(province,city,district), DefaultScore, mobileno);
    }
    
    public  Set<String> fetchKeys(String province,String city ,String district){
    	return this.keys(generateKey(province,city,district)+"*");
    }
    
    public List<String> fetchPostionMobileno(String province,String city ,String district){
    	Set<String> keys = fetchKeys(province,city,district);
    	List<String> mobileList = new ArrayList<String>();
    	for(String key : keys){
    		Set<String> mobileSet =   this.zrange(key, 0, -1);
    		mobileList.addAll(mobileSet);
    	}
    	return mobileList;
    }
    
    public int zcardPostionMobileno(String province,String city ,String district){
    	Set<String> keys = fetchKeys(province,city,district);
    	int count = 0;
    	for(String key : keys){
    		count += this.zcard(key);
    	}
    	return count;
    }
    
	@Override
	public String getName() {
		return UserMobilePositionRelationSortedSetService.class.getName();
	}

	@Override
	public String getRedisKey() {
		return null;
	}

	@Override
	public JedisPool getRedisPool() {
		return RedisPoolManager.getInstance().getPool(RedisKeyEnum.ADVERTISE);
	}
//	public static void main(String[] args) {
//		UserMobilePositionRelationSortedSetService.getInstance().mobilenoRecord("河北省", "石家庄市", "裕华区", "666666");
//		List<String> list = UserMobilePositionRelationSortedSetService.getInstance().fetchPostionMobileno("", "", "");
//		for(String str : list){
//			System.out.println(str);
//		}
//	}
}
