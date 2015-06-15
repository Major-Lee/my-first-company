package com.bhu.vas.business.bucache.redis.serviceimpl.wifistasniffer;

import java.util.Collections;
import java.util.Set;

import org.springframework.util.StringUtils;

import redis.clients.jedis.JedisPool;

import com.bhu.vas.business.bucache.redis.serviceimpl.BusinessKeyDefine;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.RedisKeyEnum;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.RedisPoolManager;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.impl.AbstractRelationSortedSetCache;
import com.smartwork.msip.cores.helper.StringHelper;

/**
 * wifi设备对应唯一终端的最近的探测流水记录
 * 以终端探测时间倒序排序
 * value 存储 1) 探测上线 snifftime$#state
 * 			 2) 探测下线 snifftime$#state$#duration
 * @author lawliet
 *
 */
public class TerminalDetailRecentSortedSetService extends AbstractRelationSortedSetCache{
	
	private static class ServiceHolder{ 
		private static TerminalDetailRecentSortedSetService instance =new TerminalDetailRecentSortedSetService(); 
	}
	/**
	 * 获取工厂单例
	 * @return
	 */
	public static TerminalDetailRecentSortedSetService getInstance() { 
		return ServiceHolder.instance; 
	}
	
	private TerminalDetailRecentSortedSetService(){
	}
	
	private static String generateKey(String mac, String hd_mac){
		StringBuilder sb = new StringBuilder(BusinessKeyDefine.WifiStasniffer.TerminalDetailRecent);
		sb.append(StringHelper.POINT_CHAR_GAP).append(mac).append(StringHelper.POINT_CHAR_GAP).append(hd_mac);
		return sb.toString();
	}
	
	public Long TerminalDetailRecentSize(String mac, String hd_mac){
		return super.zcard(generateKey(mac, hd_mac));
	}
	
	public void addTerminalDetailOnline(String mac, String hd_mac, String online_value, double snifftime){
		String key = generateKey(mac, hd_mac);
		//判断此上下文的下线状态是否已经存在
		long count = super.zcount(key, snifftime, snifftime);
		if(count == 0){
			super.zadd(key, snifftime, online_value);
		}
	}
	
	public void addTerminalDetailOffline(String mac, String hd_mac, String online_value, 
			String offline_value, double snifftime){
		String key = generateKey(mac, hd_mac);
		super.zadd(key, snifftime, offline_value);
		super.zrem(key, online_value);
	}
	
	public Set<String> fetchTerminalDetailRecent(String mac, String hd_mac, int start,int size){
		if(StringUtils.isEmpty(mac)) return Collections.emptySet();
		return super.zrevrange(generateKey(mac, hd_mac), start, (start+size-1));
	}

	
	@Override
	public String getRedisKey() {
		return null;
	}
	
	@Override
	public String getName() {
		return TerminalDetailRecentSortedSetService.class.getName();
	}
	
	@Override
	public JedisPool getRedisPool() {
		return RedisPoolManager.getInstance().getPool(RedisKeyEnum.WIFISTASNIFFER);
	}
	
}
