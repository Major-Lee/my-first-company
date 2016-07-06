package com.bhu.vas.business.bucache.redis.serviceimpl.devices;

import com.bhu.vas.business.bucache.redis.serviceimpl.BusinessKeyDefine;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.RedisKeyEnum;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.RedisPoolManager;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.impl.AbstractRelationSortedSetCache;
import com.smartwork.msip.cores.helper.StringHelper;

import redis.clients.jedis.JedisPool;
/**
 * 统一主网络和访客网络上下线设备
 *  *  	key：wifiId 
 *  	score 在线状态基础数值(在线是100亿，100,0000,0000 离线是0) + 时间(yyMMddhhmm)
 *  	value 移动设备的mac
 * 
 * 上线新增、线下修改socre值、sync更新
 * @author xiaowei
 *
 */
public class WifiDeviceHandsetUnitPresentSortedSetService extends AbstractRelationSortedSetCache{

	private static class ServiceHolder{ 
		private static WifiDeviceHandsetUnitPresentSortedSetService instance =new WifiDeviceHandsetUnitPresentSortedSetService(); 
	}
	/**
	 * 获取工厂单例
	 * @return
	 */
	public static WifiDeviceHandsetUnitPresentSortedSetService getInstance() { 
		return ServiceHolder.instance; 
	}
	
	//在线初始score数值 100亿 
	public static final double OnlineBaseScore = 10000000000d;
	
	private WifiDeviceHandsetUnitPresentSortedSetService(){
	}
	
	private static String generateKey(String wifiId){
		StringBuilder sb = new StringBuilder(BusinessKeyDefine.Present.WifiDeviceHandsetUnitPresentPrefixKey);
		sb.append(StringHelper.POINT_CHAR_GAP).append(wifiId);
		return sb.toString();
	}
	
	@Override
	public String getName() {
		return WifiDeviceHandsetUnitPresentSortedSetService.class.getName();

	}

	@Override
	public String getRedisKey() {
		return null;
	}

	@Override
	public JedisPool getRedisPool() {
		return RedisPoolManager.getInstance().getPool(RedisKeyEnum.PRESENT);
	}
	public static void main(String[] args) {
		
	}
}
