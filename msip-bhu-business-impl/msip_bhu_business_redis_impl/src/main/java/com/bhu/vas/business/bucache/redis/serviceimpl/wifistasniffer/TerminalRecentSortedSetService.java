package com.bhu.vas.business.bucache.redis.serviceimpl.wifistasniffer;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
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
	
	public void addTerminalRecent(String mac, String hd_mac, double snifftime){
		super.zadd(generateKey(mac), snifftime, hd_mac);
	}
	
	public void addTerminalRecents(String mac, String[] hd_macs, double[] snifftimes){
		if(hd_macs == null || snifftimes == null) return;
		
		Map<Double,String> members = new HashMap<Double,String>();
		int length = hd_macs.length;
		for(int i = 0;i<length;i++){
			members.put(snifftimes[i], hd_macs[i]);
		}
		super.zadd(generateKey(mac), members);
	}
	
	public void addTerminalRecents(String mac, Map<Double,String> snifftimesAndHdmacs_map){
		if(snifftimesAndHdmacs_map == null || snifftimesAndHdmacs_map.isEmpty()) return;
		super.zadd(generateKey(mac), snifftimesAndHdmacs_map);
	}
	
	public Set<Tuple> fetchTerminalRecentWithScores(String mac,int start,int size){
		if(StringUtils.isEmpty(mac)) return Collections.emptySet();
		return super.zrevrangeWithScores(generateKey(mac), start, (start+size-1));
	}
	
	public Set<Tuple> fetchTerminalRecentByScoreWithScores(String mac,double min, double max, int start,int size){
		if(StringUtils.isEmpty(mac)) return Collections.emptySet();
		return super.zrevrangeByScoreWithScores(generateKey(mac), min, max, start, size);
	}

	public long sizeByScore(String mac,double min, double max){
		return super.zcount(generateKey(mac), min, max);
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
	
//	public static void main(String[] args){
//		String mac = "84:82:f4:19:01:0c";
//		long count = TerminalRecentSortedSetService.getInstance().sizeByScore(mac, 0, System.currentTimeMillis());
//		System.out.println(count);
//		Set<Tuple> tuples = TerminalRecentSortedSetService.getInstance().fetchTerminalRecentByScoreWithScores(mac, 
//				0, System.currentTimeMillis(), 0, 5);
//		
//		ArrayList<URouterWSRecentVTO> vto_list = new ArrayList<URouterWSRecentVTO>();
//		for(Tuple tuple : tuples){
//			URouterWSRecentVTO vto = new URouterWSRecentVTO();
//			vto.setHd_mac(tuple.getElement());
//			vto.setLast_ts(Double.valueOf(tuple.getScore()).longValue());
//			vto.setTt(MacDictParserFilterHelper.prefixMactch(tuple.getElement(),true,false));
//			vto_list.add(vto);
//		}
//		System.out.println(vto_list.size());
//		Map<String, Object> payload = PageHelper.partialAllList(vto_list, count, 0, 5);
//		System.out.println(payload);
//	}
	
}
