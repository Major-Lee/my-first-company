package com.bhu.vas.business.bucache.redis.serviceimpl.marker;

import java.util.Map;

import redis.clients.jedis.JedisPool;

import com.bhu.vas.business.bucache.redis.serviceimpl.BusinessFieldDefine;
import com.bhu.vas.business.bucache.redis.serviceimpl.BusinessKeyDefine;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.RedisKeyEnum;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.RedisPoolManager;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.impl.AbstractRelationHashCache;

/**
 * 用户 是否需要充值痕迹 
 * <200虎钻 通知用户需要充值标记
 * <20虎钻  通知用户需要充值，并停用portal服务
 * 
 * @author Edmond
 *
 */
public class SnkChargingMarkerService extends AbstractRelationHashCache{
	private static class ServiceHolder{ 
		private static SnkChargingMarkerService instance =new SnkChargingMarkerService(); 
	}
	/**
	 * 获取工厂单例
	 * @return
	 */
	public static SnkChargingMarkerService getInstance() { 
		return ServiceHolder.instance; 
	}
	private SnkChargingMarkerService(){
		
	}
	private static String generateMarkPrefixKey(int uid){
		StringBuilder sb = new StringBuilder(BusinessKeyDefine.Marker.SnkCharging);
		sb.append(uid);
		return sb.toString();
	}
	
	private static final String DefaultValue = "-";
	
	/**
	 * 使用setnx 
	 * 	对于指定field存在的情况下，值会写入不成功，并返回0
	 *  对于指定field不存在的情况下，值会写入成功，并返回1
	 * @param uid
	 * @param field
	 * @return
	 */
	public long marker(int uid,String field){
		Long hsetnx = this.hsetnx(generateMarkPrefixKey(uid), field, DefaultValue);
		return hsetnx.longValue();
	}
	
	public long level2marker(int uid){
		return this.marker(uid, BusinessFieldDefine.SnkChargingLevel2NotifyFiled);
	}
	
	/**
	 * needcharging
	 * @param uid
	 * @return
	 */
	public long level1marker(int uid){
		return this.marker(uid, BusinessFieldDefine.SnkChargingLevel1NotifyFiled);
	}
	
	/**
	 * insufficient
	 * @param uid
	 * @return
	 */
	public Map<String,String> markerValues(int uid){
		return this.hgetall(generateMarkPrefixKey(uid));
	}
	
	/**
	 * 充值成功后，虚拟币超出阀值则清除此标记
	 * @param uid
	 */
	public void clear(int uid){
		this.expire(generateMarkPrefixKey(uid), 0);
	}
	
	@Override
	public String getRedisKey() {
		return null;
	}
	@Override
	public String getName() {
		return SnkChargingMarkerService.class.getName();
	}
	@Override
	public JedisPool getRedisPool() {
		return RedisPoolManager.getInstance().getPool(RedisKeyEnum.MARKER);
	}
	
	public static void main(String[] argv){
		StringBuilder sb = new StringBuilder(BusinessKeyDefine.Present.UserMarkPrefixKey);
		sb.append(33).append(12);
		System.out.println(sb.toString());
		System.out.println(SnkChargingMarkerService.getInstance().markerValues(100153));
		SnkChargingMarkerService.getInstance().clear(100153);
	}
}
