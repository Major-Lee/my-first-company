package com.bhu.vas.business.bucache.redis.serviceimpl.devices;

import java.util.List;

import redis.clients.jedis.JedisPool;

import com.bhu.vas.business.bucache.redis.serviceimpl.BusinessKeyDefine;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.RedisKeyEnum;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.RedisPoolManager;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.impl.AbstractRelationStringCache;
import com.smartwork.msip.cores.helper.StringHelper;

/**
 * 设备与移动用户设备之间关联关系
 * 用于发送用户push消息的冗余数据
 * 存储DeviceMobilePresentDTO json
 * @author tangzichao
 *
 */
public class WifiDeviceMobilePresentStringService extends AbstractRelationStringCache{
	
	private static class ServiceHolder{ 
		private static WifiDeviceMobilePresentStringService instance =new WifiDeviceMobilePresentStringService(); 
	}
	/**
	 * 获取工厂单例
	 * @return
	 */
	public static WifiDeviceMobilePresentStringService getInstance() { 
		return ServiceHolder.instance; 
	}
	
	private WifiDeviceMobilePresentStringService(){
		
	}
	
	private static String generateKey(String mac){
		StringBuilder sb = new StringBuilder(BusinessKeyDefine.Present.WifiDeviceMobilePresentPrefixKey);
		sb.append(StringHelper.POINT_CHAR_GAP).append(mac);
		return sb.toString();
	}
	
	private static String[] generateKeys(List<String> macs){
		if(macs == null || macs.isEmpty()) return null;
		String[] keys = new String[macs.size()];
		int cursor = 0;
		for(String mac : macs){
			keys[cursor] = generateKey(mac);
			cursor++;
		}
		return keys;
	}
	
	public void setMobilePresent(String mac, String value){
		super.set(generateKey(mac), value);
//		super.expire(key, seconds); 可设置过期时间 如果mobile设备超过过期时间内都没有调用mobile register接口 就会过期 不会再发送push
	}
	
	public void setMobilePresents(List<String> macs, String value){
		String[] keys = generateKeys(macs);
		String[] values = new String[keys.length];
		for(int i = 0;i<values.length;i++){
			values[i] = value;
		}
		super.mset(keys, values);
	}
	
	public String getMobilePresent(String mac){
		return super.get(generateKey(mac));
	}
	
	public void destoryMobilePresent(List<String> macs){
		String[] keys = generateKeys(macs);
		if(macs == null ||macs.isEmpty()) return;
		super.del(keys);
	}
	
	@Override
	public String getRedisKey() {
		return null;
	}
	@Override
	public String getName() {
		return WifiDeviceMobilePresentStringService.class.getName();
	}
	@Override
	public JedisPool getRedisPool() {
		return RedisPoolManager.getInstance().getPool(RedisKeyEnum.PRESENT);
	}
}
