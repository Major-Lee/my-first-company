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
 * wifi设备对应的最热的终端探测列表
 * 以终端探测的次数倒序排序
 * @author lawliet
 *
 */
public class TerminalHotSortedSetService extends AbstractRelationSortedSetCache{
	
	private static class ServiceHolder{ 
		private static TerminalHotSortedSetService instance =new TerminalHotSortedSetService(); 
	}
	/**
	 * 获取工厂单例
	 * @return
	 */
	public static TerminalHotSortedSetService getInstance() { 
		return ServiceHolder.instance; 
	}
	
	private TerminalHotSortedSetService(){
	}
	
	private static String generateKey(String mac){
		StringBuilder sb = new StringBuilder(BusinessKeyDefine.WifiStasniffer.TerminalHot);
		sb.append(StringHelper.POINT_CHAR_GAP).append(mac);
		return sb.toString();
	}
	
	public Long terminalHotSize(String mac){
		return super.zcard(generateKey(mac));
	}
	
	public void addTerminalHot(String mac, String hd_mac, double incr_sniffcount){
		super.zincrby(generateKey(mac), incr_sniffcount, hd_mac);
	}
	
	public void addTerminalHots(String mac, String[] hd_macs, double[] incr_sniffcounts){
		super.pipelineZIncr_SameKeyWithDiffMember(generateKey(mac), incr_sniffcounts, hd_macs);
	}
	
	public Set<Tuple> fetchTerminalHotWithScores(String mac,int start,int size){
		if(StringUtils.isEmpty(mac)) return Collections.emptySet();
		return super.zrevrangeWithScores(generateKey(mac), start, (start+size-1));
	}

	
	@Override
	public String getRedisKey() {
		return null;
	}
	
	@Override
	public String getName() {
		return TerminalHotSortedSetService.class.getName();
	}
	
	@Override
	public JedisPool getRedisPool() {
		return RedisPoolManager.getInstance().getPool(RedisKeyEnum.WIFISTASNIFFER);
	}
	
}
