package com.bhu.vas.business.bucache.redis.serviceimpl.devices;

import redis.clients.jedis.JedisPool;

import com.bhu.vas.business.bucache.redis.serviceimpl.BusinessKeyDefine;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.RedisKeyEnum;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.RedisPoolManager;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.impl.AbstractRelationStringCache;
import com.smartwork.msip.cores.helper.StringHelper;
/**
 * 设备的mode状态存储
 * model，ip，网关，子网掩码，dns
 * @author tangzichao
 *
 */
public class WifiDeviceModeStatusService extends AbstractRelationStringCache{
	
	//public static final int Present_Exprie_Seconds = 60 * 60;//1小时过期
	
	private static class ServiceHolder{ 
		private static WifiDeviceModeStatusService instance =new WifiDeviceModeStatusService(); 
	}

	public static WifiDeviceModeStatusService getInstance() { 
		return ServiceHolder.instance; 
	}
	
	public String generateKey(String wifiId){
		StringBuilder sb = new StringBuilder();
		sb.append(BusinessKeyDefine.Present.WifiDeviceModeStatusPrefixKey).append(StringHelper.POINT_CHAR_GAP).append(wifiId);
		return sb.toString();
	}
	
	public void addPresent(String wifiId, String json){
		super.set(generateKey(wifiId), json);
	}
	
	public String getPresent(String wifiId){
		return super.get(generateKey(wifiId));
	}

	public void removePresent(String wifiId){
		super.del(generateKey(wifiId));
	}

	@Override
	public String getRedisKey() {
		return null;
	}

	@Override
	public String getName() {
		return WifiDeviceModeStatusService.class.getName();
	}

	@Override
	public JedisPool getRedisPool() {
		return RedisPoolManager.getInstance().getPool(RedisKeyEnum.PRESENT);
	}
}
