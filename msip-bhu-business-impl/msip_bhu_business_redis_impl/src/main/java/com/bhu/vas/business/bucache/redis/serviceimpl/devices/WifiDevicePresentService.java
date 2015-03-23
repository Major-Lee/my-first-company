package com.bhu.vas.business.bucache.redis.serviceimpl.devices;

import java.util.List;

import redis.clients.jedis.JedisPool;

import com.bhu.vas.business.bucache.redis.serviceimpl.BusinessKeyDefine;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.RedisKeyEnum;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.RedisPoolManager;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.impl.AbstractRelationStringCache;
import com.smartwork.msip.cores.helper.StringHelper;
/**
 * wifi设备的实时在线状态
 * value 为wifi所在的CM标识
 * @author tangzichao
 *
 */
public class WifiDevicePresentService extends AbstractRelationStringCache{
	
	//public static final int Present_Exprie_Seconds = 60 * 60;//1小时过期
	
	private static class ServiceHolder{ 
		private static WifiDevicePresentService instance =new WifiDevicePresentService(); 
	}

	public static WifiDevicePresentService getInstance() { 
		return ServiceHolder.instance; 
	}
	
	public String generateKey(String wifiId){
		StringBuilder sb = new StringBuilder();
		sb.append(BusinessKeyDefine.Present.WifiDevicePresentPrefixKey).append(StringHelper.POINT_CHAR_GAP).append(wifiId);
		return sb.toString();
	}
	
	public String[] generateKeys(List<String> wifiIds){
		if(wifiIds == null || wifiIds.isEmpty()) return null;
		String[] keys = new String[wifiIds.size()];
		int cursor = 0;
		for(String wifiId : wifiIds){
			keys[cursor] = generateKey(wifiId);
			cursor++;
		}
		return keys;
	}
	
	public void addPresent(String wifiId, String ctx){
		super.set(generateKey(wifiId), ctx);
	}
	
	public void addPresents(List<String> wifiIds, String ctx){
		String[] keys = this.generateKeys(wifiIds);
		String[] values = new String[keys.length];
		for(int i = 0;i<values.length;i++){
			values[i] = ctx;
		}
		super.mset(keys, values);
	}
	
	public String getPresent(String wifiId){
		return super.get(generateKey(wifiId));
	}
	
	public List<String> getPresents(List<String> wifiIds){
		return super.mget(generateKeys(wifiIds));
	}
	
	public void removePresent(String wifiId){
		super.del(generateKey(wifiId));
	}
	
//	public void setExprieTime(String key){
//		super.expire(key, Present_Exprie_Seconds);
//	}
	
	@Override
	public String getRedisKey() {
		return null;
	}

	@Override
	public String getName() {
		return WifiDevicePresentService.class.getName();
	}

	@Override
	public JedisPool getRedisPool() {
		return RedisPoolManager.getInstance().getPool(RedisKeyEnum.PRESENT);
	}
}
