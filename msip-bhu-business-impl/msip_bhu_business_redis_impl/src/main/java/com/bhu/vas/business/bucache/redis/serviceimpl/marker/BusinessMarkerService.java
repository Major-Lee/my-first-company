package com.bhu.vas.business.bucache.redis.serviceimpl.marker;

import org.apache.commons.lang.StringUtils;

import redis.clients.jedis.JedisPool;

import com.bhu.vas.api.dto.redis.DeviceUsedStatisticsDTO;
import com.bhu.vas.business.bucache.redis.serviceimpl.BusinessKeyDefine;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.RedisKeyEnum;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.RedisPoolManager;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.impl.AbstractRelationHashCache;
import com.smartwork.msip.cores.helper.DateTimeHelper;
import com.smartwork.msip.cores.helper.HashAlgorithmsHelper;
import com.smartwork.msip.cores.helper.JsonHelper;
import com.smartwork.msip.cores.helper.StringHelper;

/**
 * 设备、用户 痕迹 等 通用标记类 具体标记的内容由业务定制
 * 数据量很大 所以需要散列hash
 * 并且通过程序可以获取所有的数据进行维护清除
 * field 存储 具体的业务 value 具体业务标记值
 * @author Edmond
 *
 */
public class BusinessMarkerService extends AbstractRelationHashCache{
	private static class ServiceHolder{ 
		private static BusinessMarkerService instance =new BusinessMarkerService(); 
	}
	public static final int hasPrimeValue = 1000;
	/**
	 * 获取工厂单例
	 * @return
	 */
	public static BusinessMarkerService getInstance() { 
		return ServiceHolder.instance; 
	}
	private BusinessMarkerService(){
		
	}
	private static String generateMarkPrefixKey(String businessKey,String businessField){
		int hashvalue = HashAlgorithmsHelper.additiveHash(businessField, hasPrimeValue);
		StringBuilder sb = new StringBuilder(BusinessKeyDefine.Present.MarkPrefixKey);
		sb.append(StringHelper.POINT_CHAR_GAP).append(businessKey);
		sb.append(StringHelper.POINT_CHAR_GAP).append(String.format("%04d", hashvalue));
		return sb.toString();
	}
	
	//设备使用状况查询标记 时间间隔内 不继续发起请求 timeout则继续发起请求的标记---------------------------------------------------
	private static final String DeviceUsedStatusMarkerPrefixKey = "DUSM";
	private static final long apiRequest_timeout = 1800*1000;//半个小时
	public void deviceUsedMarker(String mac){
		this.hset(generateMarkPrefixKey(DeviceUsedStatusMarkerPrefixKey,mac), mac, String.valueOf(System.currentTimeMillis()));
	}

	//
	//TODO:always return true
	/**
	 * 开关控制 是否需要发起指令下发查询设备使用情况
	 * @param mac
	 * @param apiRequest 是否通过api或者设备绑定用户第一次登录的数据预取 请求进行的发起，目前，对于api请求发起的始终返回true
	 * @return
	 */
	public boolean needNewRequestAndMarker(String mac,boolean forApiRequest){
		long current = System.currentTimeMillis();
		String key = generateMarkPrefixKey(DeviceUsedStatusMarkerPrefixKey,mac);
		if(forApiRequest){
			String previoustime = this.hget(key, mac);
			if(StringUtils.isNotEmpty(previoustime)){
				long previous_time = Long.parseLong(previoustime);
				if( current - previous_time < apiRequest_timeout){
					//return false;
					//TODO:
					return true;
				}
			}
			this.hset(key, mac, String.valueOf(current));
			return true;
		}else{
			String previoustime = this.hget(key, mac);
			if(StringUtils.isNotEmpty(previoustime)){
				long previous_time = Long.parseLong(previoustime);
				if( DateTimeHelper.isSameDay(current, previous_time)){
					return false;
				}
			}
			this.hset(key, mac, String.valueOf(current));
			return true;
		}
		
	}
	
	//设备使用状况数据存储
	private static final String DeviceUsedStatusDTOPrefixKey = "DUSD";
	public void deviceUsedStatisticsSet(String mac,DeviceUsedStatisticsDTO dto){
		this.hset(generateMarkPrefixKey(DeviceUsedStatusDTOPrefixKey,mac), mac, JsonHelper.getJSONString(dto));
	}
	public void deviceUsedStatisticsClear(String mac){
		this.hdel(generateMarkPrefixKey(DeviceUsedStatusDTOPrefixKey,mac), mac);
	}
	public DeviceUsedStatisticsDTO deviceUsedStatisticsGet(String mac){
		String value = this.hget(generateMarkPrefixKey(DeviceUsedStatusDTOPrefixKey,mac), mac);
		if(StringUtils.isNotEmpty(value)){
			return JsonHelper.getDTO(value, DeviceUsedStatisticsDTO.class);
		}else{
			return null;
		}
	}
	
	
	//设备workmode状态临时变更标记
	private static final String DeviceWorkmodeChangedPrefixKey = "DWC";
	
	public void deviceWorkmodeChangedMarker(String mac,int switchMode){
		this.hset(generateMarkPrefixKey(DeviceWorkmodeChangedPrefixKey,mac), mac, String.valueOf(switchMode));
	}
	
	public int deviceWorkmodeChangedStatusGetAndClear(String mac){
		String key = generateMarkPrefixKey(DeviceWorkmodeChangedPrefixKey,mac);
		String result = this.hget(key, mac);
		if(result != null){
			this.hdel(key, mac);
		}
		try{
			return Integer.parseInt(result);
		}catch(Exception ex){
			return -1;
		}
		
	}
	
	public Long deviceWorkmodeChangedStatusClear(String mac){
		return this.hdel(generateMarkPrefixKey(DeviceWorkmodeChangedPrefixKey,mac), mac);
	}
	
	@Override
	public String getRedisKey() {
		return null;
	}
	@Override
	public String getName() {
		return BusinessMarkerService.class.getName();
	}
	@Override
	public JedisPool getRedisPool() {
		return RedisPoolManager.getInstance().getPool(RedisKeyEnum.PRESENT);
	}
}
