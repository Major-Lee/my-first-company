package com.bhu.vas.business.bucache.redis.serviceimpl.wifistasniffer;

import java.util.Collections;
import java.util.Set;

import org.springframework.util.StringUtils;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Tuple;

import com.bhu.vas.business.bucache.redis.serviceimpl.BusinessKeyDefine;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.RedisKeyEnum;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.RedisPoolManager;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.impl.AbstractRelationSortedSetCache;
import com.smartwork.msip.cores.helper.StringHelper;

/**
 * wifi设备对应的最近的终端探测列表
 * 以终端探测时间倒序排序
 * @author lawliet
 *
 */
public class TerminalRecentSortedSetService extends AbstractRelationSortedSetCache{
	
	private static class ServiceHolder{ 
		private static TerminalRecentSortedSetService instance =new TerminalRecentSortedSetService(); 
	}
	/**
	 * 获取工厂单例
	 * @return
	 */
	public static TerminalRecentSortedSetService getInstance() { 
		return ServiceHolder.instance; 
	}
	
	private TerminalRecentSortedSetService(){
	}
	
	private static String generateKey(String mac){
		StringBuilder sb = new StringBuilder(BusinessKeyDefine.WifiStasniffer.TerminalRecent);
		sb.append(StringHelper.POINT_CHAR_GAP).append(mac);
		return sb.toString();
	}
	
	public Long TerminalRecentSize(String mac){
		return super.zcard(generateKey(mac));
	}
	
	public long addTerminalRecent(String mac, String hd_mac, double snifftime){
		return super.zadd(generateKey(mac), snifftime, hd_mac);
	}
	
	public Set<Tuple> fetchTerminalRecentWithScores(String mac,int start,int size){
		if(StringUtils.isEmpty(mac)) return Collections.emptySet();
		return super.zrevrangeWithScores(generateKey(mac), start, (start+size-1));
	}

	
	@Override
	public String getRedisKey() {
		return null;
	}
	
	@Override
	public String getName() {
		return TerminalRecentSortedSetService.class.getName();
	}
	
	@Override
	public JedisPool getRedisPool() {
		return RedisPoolManager.getInstance().getPool(RedisKeyEnum.WIFISTASNIFFER);
	}
	
}
