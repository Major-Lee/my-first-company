package com.bhu.vas.business.bucache.redis.serviceimpl.devices;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Set;

import redis.clients.jedis.JedisPool;

import com.bhu.vas.api.helper.AdvertiseHelper;
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
		sb.append(BusinessKeyDefine.Present.WifiDeviceProvincePrefixKey).append(stringencoding(province));
		return sb.toString();
	}
	
	public String generateKeyCity(String city){
		StringBuilder sb = new StringBuilder();
		sb.append(BusinessKeyDefine.Present.WifiDeviceCityPrefixKey).append(stringencoding(city));
		return sb.toString();
	}
	
	public void generateAllProvince(String province){
		this.lpush(generateKeyAllProvince(), stringencoding(province));
	}
	
	public List<String> fetchAllProvince(){
		return this.lrange(generateKeyAllProvince(), deafultStart, deafultEnd);
	}
	
	public void generateProvince(String province,String city){
		this.lpush(generateKeyProvince(province), stringencoding(city));
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
	
	private String stringencoding(String str) {
		String newStr = null;
		try {
			newStr = new String(str.getBytes(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}  
		return newStr;
	}
	
	public void wifiDeviceLocationChanged(String province,String city,String district){
		Set<String> provinceSet = this.keys(BusinessKeyDefine.Present.WifiDeviceProvincePrefixKey+province);
		if(provinceSet.isEmpty()){
			generateAllProvince(province);
			generateProvince(province, city);
			generateCity(city, district);
		}else{
			Set<String> citySet = this.keys(BusinessKeyDefine.Present.WifiDeviceCityPrefixKey+city);
			if(citySet.isEmpty()){
				generateProvince(province, city);
				generateCity(city, district);
			}else{
				boolean newdistrict = false;
				for(String dis : citySet){
					if(dis.equals(district)){
						newdistrict = true;
					}
				}
				if(newdistrict){
					generateCity(city, district);
				}
			}
		}
	}
}
