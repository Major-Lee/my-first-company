package com.bhu.vas.business.bucache.redis.serviceimpl.handset;

import java.util.Map;

import redis.clients.jedis.JedisPool;

import com.bhu.vas.business.bucache.redis.serviceimpl.BusinessKeyDefine;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.RedisKeyEnum;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.RedisPoolManager;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.impl.AbstractRelationHashCache;
import com.smartwork.msip.cores.helper.DateTimeHelper;
import com.smartwork.msip.cores.helper.StringHelper;

public class HandsetGroupPresentHashService extends AbstractRelationHashCache{
	
	private static final String HANDSET_GROUP_PRESENT_TOTAL = "total";
	private static final String HANDSET_GROUP_PRESENT_NEWLY = "newly";
	private static final int DEFAULT_INCRBY = 1;

    private static class ServiceHolder{
        private static HandsetGroupPresentHashService instance =new HandsetGroupPresentHashService();
    }
    
    /**
     * 获取工厂单例
     * @return
     */
    public static HandsetGroupPresentHashService getInstance() {
        return ServiceHolder.instance;
    }

    private HandsetGroupPresentHashService() {
    	
    }
    
	private static String generateKey(int gid,String timestr){
		StringBuilder sb = new StringBuilder(BusinessKeyDefine.HandsetPresent.GroupStatisticsPrefixKey);
		sb.append(gid).append(StringHelper.MINUS_CHAR_GAP).append(timestr);
		return sb.toString();
	}
	
	private static String generateKey(int gid){
		StringBuilder sb = new StringBuilder(BusinessKeyDefine.HandsetPresent.GroupStatisticsPrefixKey);
		sb.append(gid);
		return sb.toString();
	}
	
	public void groupHandsetComming(int gid){
		this.hincrby(generateKey(gid,DateTimeHelper.getDateTime(DateTimeHelper.FormatPattern7)), HANDSET_GROUP_PRESENT_TOTAL, DEFAULT_INCRBY);
		this.hincrby(generateKey(gid), HANDSET_GROUP_PRESENT_TOTAL, DEFAULT_INCRBY);
	}
	
	public void groupNewlyHandsetComming(int gid){
		this.hincrby(generateKey(gid,DateTimeHelper.getDateTime(DateTimeHelper.FormatPattern7)), HANDSET_GROUP_PRESENT_NEWLY, DEFAULT_INCRBY);
	}
	
	public Map<String, String> fetchGroupConnDetail(int gid,String timestr){
		return this.hgetall(generateKey(gid,timestr));
	}
	
	public String fetchGroupConnTotal(int gid){
		return this.hget(generateKey(gid),HANDSET_GROUP_PRESENT_TOTAL);
	}
	
	@Override
	public JedisPool getRedisPool() {
        return RedisPoolManager.getInstance().getPool(RedisKeyEnum.STATISTICS);
	}

	@Override
	public String getRedisKey() {
		return null;
	}

	@Override
	public String getName() {
		return HandsetGroupPresentHashService.class.getName();
	}
	
	public static void main(String[] args) {
		HandsetGroupPresentHashService.getInstance().groupHandsetComming(10000);
	}
}
