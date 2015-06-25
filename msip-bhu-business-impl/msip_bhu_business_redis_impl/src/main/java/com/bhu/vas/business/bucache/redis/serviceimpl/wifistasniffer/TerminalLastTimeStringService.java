package com.bhu.vas.business.bucache.redis.serviceimpl.wifistasniffer;

import java.util.Collections;
import java.util.List;

import redis.clients.jedis.JedisPool;

import com.bhu.vas.business.bucache.redis.serviceimpl.BusinessKeyDefine;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.RedisKeyEnum;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.RedisPoolManager;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.impl.AbstractRelationStringCache;
import com.smartwork.msip.cores.helper.StringHelper;

/**
 * 周边探测终端的最后一次探测时间
 * @author lawliet
 *
 */
public class TerminalLastTimeStringService extends AbstractRelationStringCache{
	
	private static class ServiceHolder{ 
		private static TerminalLastTimeStringService instance =new TerminalLastTimeStringService(); 
	}
	/**
	 * 获取工厂单例
	 * @return
	 */
	public static TerminalLastTimeStringService getInstance() { 
		return ServiceHolder.instance; 
	}
	private TerminalLastTimeStringService(){
		
	}
	
	private static String generateMarkPrefixKey(String mac, String hd_mac){
		StringBuilder sb = new StringBuilder(BusinessKeyDefine.WifiStasniffer.TerminalLastTime);
		sb.append(StringHelper.POINT_STRING_GAP).append(mac);
		sb.append(StringHelper.POINT_STRING_GAP).append(hd_mac);
		return sb.toString();
	}
	
	public void set(String mac, String hd_mac, long snifftime){
		super.set(generateMarkPrefixKey(mac, hd_mac), String.valueOf(snifftime));
	}
	
	public List<String> getMulti(String mac, String... hd_macs){
		if(hd_macs == null || hd_macs.length == 0) return Collections.emptyList();
		int length = hd_macs.length;
		
		String[] keys = new String[length];
		for(int i = 0;i<length;i++){
			keys[i] = generateMarkPrefixKey(mac, hd_macs[i]);
		}
		return super.mget(keys);
	}
	
	@Override
	public String getRedisKey() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public String getName() {
		return TerminalLastTimeStringService.class.getName();
	}
	@Override
	public JedisPool getRedisPool() {
		return RedisPoolManager.getInstance().getPool(RedisKeyEnum.WIFISTASNIFFER);
	}
}
