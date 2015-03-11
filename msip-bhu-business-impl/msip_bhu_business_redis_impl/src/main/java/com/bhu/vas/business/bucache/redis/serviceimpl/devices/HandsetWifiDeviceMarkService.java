package com.bhu.vas.business.bucache.redis.serviceimpl.devices;

import redis.clients.jedis.JedisPool;

import com.bhu.vas.business.bucache.redis.serviceimpl.BusinessKeyDefine;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.RedisKeyEnum;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.RedisPoolManager;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.impl.AbstractRelationStringCache;
import com.smartwork.msip.cores.helper.StringHelper;
/**
 * 移动设备与wifi设备的实时在线mark
 * @author tangzichao
 *
 */
public class HandsetWifiDeviceMarkService extends AbstractRelationStringCache{
	
	public static final int Mark_Exprie_Seconds = 60 * 60;//1小时过期
	
	private static class ServiceHolder{ 
		private static HandsetWifiDeviceMarkService instance =new HandsetWifiDeviceMarkService(); 
	}

	public static HandsetWifiDeviceMarkService getInstance() { 
		return ServiceHolder.instance; 
	}
	
	public String generateKey(String handsetId){
		StringBuilder sb = new StringBuilder();
		sb.append(BusinessKeyDefine.Present.HandsetWifiDeviceMarkPrefixKey).append(StringHelper.POINT_CHAR_GAP).append(handsetId);
		return sb.toString();
	}
	
	public void mark(String handsetId, String wifiId){
		String key = generateKey(handsetId);
		super.set(key, wifiId);
		this.setExprieTime(key);
	}
	
	public String getMark(String handsetId){
		return super.get(generateKey(handsetId));
	}
	
	public void removeMark(String handsetId){
		super.del(generateKey(handsetId));
	}
	
	public void setExprieTime(String key){
		super.expire(key, Mark_Exprie_Seconds);
	}
	
	@Override
	public String getRedisKey() {
		return null;
	}

	@Override
	public String getName() {
		return HandsetWifiDeviceMarkService.class.getName();
	}

	@Override
	public JedisPool getRedisPool() {
		return RedisPoolManager.getInstance().getPool(RedisKeyEnum.PRESENT);
	}
}
