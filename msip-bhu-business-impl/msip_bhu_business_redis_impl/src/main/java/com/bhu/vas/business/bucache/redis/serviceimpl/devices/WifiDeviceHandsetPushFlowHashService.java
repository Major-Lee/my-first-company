package com.bhu.vas.business.bucache.redis.serviceimpl.devices;

import redis.clients.jedis.JedisPool;

import com.bhu.vas.business.bucache.redis.serviceimpl.BusinessKeyDefine;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.RedisKeyEnum;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.RedisPoolManager;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.impl.AbstractRelationStringCache;
import com.smartwork.msip.cores.helper.StringHelper;

public class WifiDeviceHandsetPushFlowHashService extends AbstractRelationStringCache{
	
	private static class ServiceHolder{ 
		private static WifiDeviceHandsetPushFlowHashService instance =new WifiDeviceHandsetPushFlowHashService(); 
	}
	/**
	 * 获取工厂单例
	 * @return
	 */
	public static WifiDeviceHandsetPushFlowHashService getInstance() { 
		return ServiceHolder.instance; 
	}
	private static String generateKey(int uid){
		StringBuilder sb = new StringBuilder(BusinessKeyDefine.Present.WifiDeviceHandsetPushFlowPrefixKey);
		sb.append(StringHelper.POINT_CHAR_GAP).append(uid);
		return sb.toString();
	}
	
	public Long pushComming(int uid){
		return super.incrby(generateKey(uid), 1);
	}
	
	public void pushFlowClear(int uid){
		super.del(generateKey(uid));
	}
	
	public String fetchPushFlow(int uid){
		return super.get(generateKey(uid));
	}
	
	@Override
	public String getName() {
		return WifiDeviceHandsetPushFlowHashService.class.getName();
	}

	@Override
	public String getRedisKey() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JedisPool getRedisPool() {
		return RedisPoolManager.getInstance().getPool(RedisKeyEnum.PRESENT);
	}
}
