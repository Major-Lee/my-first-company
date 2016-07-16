package com.bhu.vas.business.bucache.redis.serviceimpl.commdity;

import com.bhu.vas.api.dto.commdity.internal.useragent.OrderUserAgentDTO;
import com.bhu.vas.business.bucache.redis.serviceimpl.BusinessKeyDefine;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.RedisKeyEnum;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.RedisPoolManager;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.impl.AbstractRelationHashCache;
import com.smartwork.msip.cores.helper.JsonHelper;
import com.smartwork.msip.cores.helper.StringHelper;

import redis.clients.jedis.JedisPool;

public class UserOrderDetailsHashService extends AbstractRelationHashCache{

	private static class ServiceHolder{ 
		private static UserOrderDetailsHashService instance =new UserOrderDetailsHashService(); 
	}

	public static UserOrderDetailsHashService getInstance() { 
		return ServiceHolder.instance; 
	}
	
	private static String generateKey(String wifiId){
		StringBuilder sb = new StringBuilder(BusinessKeyDefine.Commdity.UserAgentPrefixKey);
		sb.append(StringHelper.POINT_CHAR_GAP).append(wifiId);
		return sb.toString();
	}
	
	public long addUserOrderDetail(String wifiId, String hdMac, OrderUserAgentDTO dto){
		return super.hset(generateKey(wifiId), hdMac, JsonHelper.getJSONString(dto));
	}
	
	public String fetchUserOrderDetail(String wifiId, String hdMac){
		return super.hget(generateKey(wifiId), hdMac);
	}
	
	@Override
	public String getRedisKey() {
		return null;
	}

	@Override
	public String getName() {
		return RewardOrderAmountHashService.class.getName();
	}

	@Override
	public JedisPool getRedisPool() {
		return RedisPoolManager.getInstance().getPool(RedisKeyEnum.USERAGENT);
	}
	
}
