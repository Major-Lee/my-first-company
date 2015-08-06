package com.bhu.vas.business.bucache.redis.serviceimpl.wifistasniffer;

import java.util.Map;

import redis.clients.jedis.JedisPool;

import com.bhu.vas.business.bucache.redis.serviceimpl.BusinessKeyDefine;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.RedisKeyEnum;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.RedisPoolManager;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.impl.AbstractRelationHashCache;
import com.smartwork.msip.cores.helper.StringHelper;

/**
 * 记录出现在同一设备的终端的不同型号的次数
 * 型号通过终端mac地址进行解析
 * @author lawliet
 *
 */
public class TerminalDeviceTypeCountHashService extends AbstractRelationHashCache{
	
	private static class ServiceHolder{ 
		private static TerminalDeviceTypeCountHashService instance =new TerminalDeviceTypeCountHashService(); 
	}
	/**
	 * 获取工厂单例
	 * @return
	 */
	public static TerminalDeviceTypeCountHashService getInstance() { 
		return ServiceHolder.instance; 
	}
	private TerminalDeviceTypeCountHashService(){
		
	}
	private static String generateMarkPrefixKey(String mac){
		StringBuilder sb = new StringBuilder(BusinessKeyDefine.WifiStasniffer.TerminalDeviceTypeCount);
		sb.append(StringHelper.POINT_STRING_GAP).append(mac);
		return sb.toString();
	}
	
	public long incrby(String mac, String terminal_type){
		return super.hincrby(generateMarkPrefixKey(mac), terminal_type, 1);
	}
	
	public Map<String,String> getAll(String mac){
		return super.hgetall(generateMarkPrefixKey(mac));
	}
	
	public void del(String mac){
		super.del(generateMarkPrefixKey(mac));
	}
	
	@Override
	public String getRedisKey() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public String getName() {
		return TerminalDeviceTypeCountHashService.class.getName();
	}
	@Override
	public JedisPool getRedisPool() {
		return RedisPoolManager.getInstance().getPool(RedisKeyEnum.WIFISTASNIFFER);
	}
}
