package com.bhu.vas.business.bucache.redis.serviceimpl.devices;

import java.util.List;

import redis.clients.jedis.JedisPool;

import com.bhu.vas.business.bucache.redis.serviceimpl.BusinessKeyDefine;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.RedisKeyEnum;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.RedisPoolManager;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.impl.AbstractRelationListCache;

/**
 * 设备地理位置
 * @author xiaowei
 *
 */
public class WifiDevicePositionListService extends AbstractRelationListCache{
	
	private static final int deafultStart = 0;
	private static final int deafultEnd = -1;
	
	
	private static class ServiceHolder{ 
		private static WifiDevicePositionListService instance =new WifiDevicePositionListService(); 
	}

	public static WifiDevicePositionListService getInstance() { 
		return ServiceHolder.instance; 
	}
	
	public String generateKeyAllProvince(){
		StringBuilder sb = new StringBuilder();
		sb.append(BusinessKeyDefine.Present.WifiDeviceAllProvincePrefixKey);
		return sb.toString();
	}
	
	public String generateKeyProvince(String province){
		StringBuilder sb = new StringBuilder();
		sb.append(BusinessKeyDefine.Present.WifiDeviceProvincePrefixKey).append(province);
		return sb.toString();
	}
	
	public String generateKeyCity(String city){
		StringBuilder sb = new StringBuilder();
		sb.append(BusinessKeyDefine.Present.WifiDeviceCityPrefixKey).append(city);
		return sb.toString();
	}
	
	public void generateAllProvince(String province){
		this.lpush(generateKeyAllProvince(), province);
	}
	
	public List<String> fetchAllProvince(){
		return this.lrange(generateKeyAllProvince(), deafultStart, deafultEnd);
	}
	
	public void generateProvince(String province,String city){
		this.lpush(generateKeyProvince(province), city);
	}
	
	public List<String> fetchProvince(String province){
		return this.lrange(generateKeyProvince(province), deafultStart, deafultEnd);
	}
	
	public void generateCity(String city,String district){
		this.lpush(generateKeyCity(city), district);
	}
	
	public List<String> fetchCity(String city){
		return this.lrange(generateKeyCity(city), deafultStart, deafultEnd);
	}
	
	@Override
	public String getName() {
		return WifiDevicePositionListService.class.getName();
	}

	@Override
	public String getRedisKey() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JedisPool getRedisPool() {
		return RedisPoolManager.getInstance().getPool(RedisKeyEnum.ADVERTISE);
	}

}
