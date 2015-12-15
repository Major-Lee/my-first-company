package com.bhu.vas.business.bucache.redis.serviceimpl.handset;

import java.util.Map;

import redis.clients.jedis.JedisPool;

import com.bhu.vas.api.dto.HandsetLogDTO;
import com.bhu.vas.business.bucache.redis.serviceimpl.BusinessKeyDefine;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.RedisKeyEnum;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.RedisPoolManager;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.impl.AbstractRelationHashCache;
import com.smartwork.msip.cores.helper.StringHelper;

/**
 * 设备所有接入终端存储
 * key dmac field hmac value 最后一次登录或登出时间ts
 * @author Edmond Lee
 *
 */
public class DeviceHandsetsService extends AbstractRelationHashCache{
	private static class ServiceHolder{ 
		private static DeviceHandsetsService instance =new DeviceHandsetsService(); 
	}
	/**
	 * 获取工厂单例
	 * @return
	 */
	public static DeviceHandsetsService getInstance() { 
		return ServiceHolder.instance; 
	}
	private DeviceHandsetsService(){
	}
	
	private String generateKey(String dmac){
		StringBuilder sb = new StringBuilder();
		sb.append(BusinessKeyDefine.HandsetPresent.PresentRelationKey)
			.append(dmac);
		return sb.toString();
	}
	
	public Map<String,String> handsets(String dmac){
		return this.hgetall(generateKey(dmac));
	}
	
	public int handsetComming(boolean action,String dmac,String hmac,long ts){
		String valuePrefix = action?StringHelper.PLUS_STRING_GAP:StringHelper.MINUS_STRING_GAP;
		Long ret = this.hset(generateKey(dmac), hmac, 
				valuePrefix.concat(String.valueOf(ts)));
		if(ret.longValue() == 1){//新的hmac
			return HandsetLogDTO.Element_NewHandset;//.Element_ExistHandset;
		}else{
			return HandsetLogDTO.Element_ExistHandset;
		}
	}

	@Override
	public String getRedisKey() {
		return null;
	}

	@Override
	public String getName() {
		return DeviceHandsetsService.class.getName();
	}

	@Override
	public JedisPool getRedisPool() {
		return RedisPoolManager.getInstance().getPool(RedisKeyEnum.CLUSTEREXT);
	}
}
