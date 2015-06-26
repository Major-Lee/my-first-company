package com.bhu.vas.business.bucache.redis.serviceimpl.wifistasniffer;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.springframework.util.StringUtils;

import redis.clients.jedis.JedisPool;

import com.bhu.vas.api.dto.wifistasniffer.TerminalDetailDTO;
import com.bhu.vas.business.bucache.redis.serviceimpl.BusinessKeyDefine;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.RedisKeyEnum;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.RedisPoolManager;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.impl.AbstractRelationSortedSetCache;
import com.smartwork.msip.cores.helper.JsonHelper;
import com.smartwork.msip.cores.helper.StringHelper;

/**
 * wifi设备对应唯一终端的最近的探测流水记录
 * 以终端探测时间倒序排序
 * value 存储 json
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
	
	public boolean addTerminalDetailOnline(String mac, String hd_mac, TerminalDetailDTO dto){
		String key = generateKey(mac, hd_mac);
		long snifftime = dto.getSnifftime();
		//判断此上下文的下线状态是否已经存在
		long count = super.zcount(key, snifftime, snifftime);
		if(count == 0){
			long ret = super.zadd(key, snifftime, JsonHelper.getJSONString(dto));
			if(ret > 0){
				return true;
			}
		}
		return false;
	}
	
	public boolean addTerminalDetailOffline(String mac, String hd_mac, TerminalDetailDTO dto){
		String key = generateKey(mac, hd_mac);
		long ret = super.zadd(key, dto.getSnifftime(), JsonHelper.getJSONString(dto));
		
		TerminalDetailDTO online_dto = new TerminalDetailDTO();
		online_dto.setSnifftime(dto.getSnifftime());
		super.zrem(key, JsonHelper.getJSONString(online_dto));
		if(ret > 0){
			return true;
		}
		return false;
	}
	
	public Set<String> fetchTerminalDetailRecent(String mac, String hd_mac, int start,int size){
		if(StringUtils.isEmpty(mac)) return Collections.emptySet();
		return super.zrevrange(generateKey(mac, hd_mac), start, (start+size-1));
	}
	
	public List<Object> fetchTerminalDetailRecents(String mac, int start,int size, String... hd_macs){
		if(StringUtils.isEmpty(mac) || hd_macs == null || hd_macs.length == 0) return Collections.emptyList();
		int length = hd_macs.length;
		String[] keys = new String[hd_macs.length];
		for(int i = 0;i<length;i++){
			keys[i] = generateKey(mac, hd_macs[i]);
		}
		return super.pipelineZRevrange_DiffKeyWithSameOffset(keys, start, (start+size-1));
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
