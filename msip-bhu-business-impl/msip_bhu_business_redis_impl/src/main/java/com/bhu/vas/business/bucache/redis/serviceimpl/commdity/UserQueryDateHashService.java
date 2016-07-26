package com.bhu.vas.business.bucache.redis.serviceimpl.commdity;

import com.bhu.vas.business.bucache.redis.serviceimpl.BusinessKeyDefine;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.RedisKeyEnum;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.RedisPoolManager;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.impl.AbstractRelationHashCache;
import com.smartwork.msip.cores.helper.StringHelper;

import redis.clients.jedis.JedisPool;

public class UserQueryDateHashService extends AbstractRelationHashCache{
	
	private static class ServiceHolder{ 
		private static UserQueryDateHashService instance =new UserQueryDateHashService(); 
	}

	public static UserQueryDateHashService getInstance() { 
		return ServiceHolder.instance; 
	}
	
	private static String generateKey(){
		StringBuilder sb = new StringBuilder(BusinessKeyDefine.Commdity.UserQueryDataPrefixKey);
		sb.append(StringHelper.POINT_CHAR_GAP);
		return sb.toString();
	}

	public Long addQueryData(int uid ,long timestamp){
		return super.hset(generateKey(), uid+"", timestamp+"");
	}
	
	public String fetchLastQueryData(int uid){
		return super.hget(generateKey(), uid+"");
	}
	
	@Override
	public String getRedisKey() {
		return null;
	}

	@Override
	public String getName() {
		return UserQueryDateHashService.class.getName();
	}

	@Override
	public JedisPool getRedisPool() {
		return RedisPoolManager.getInstance().getPool(RedisKeyEnum.COMMDITY);
	}
}
