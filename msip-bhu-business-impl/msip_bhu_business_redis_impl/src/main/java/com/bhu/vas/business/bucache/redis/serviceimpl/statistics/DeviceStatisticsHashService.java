package com.bhu.vas.business.bucache.redis.serviceimpl.statistics;

import redis.clients.jedis.JedisPool;

import com.bhu.vas.business.bucache.redis.serviceimpl.BusinessKeyDefine;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.impl.AbstractRelationHashCache;

public class DeviceStatisticsHashService extends AbstractRelationHashCache{
	
	private static DeviceStatisticsHashService instance = null;
	public DeviceStatisticsHashService getInstance(){
		if(instance == null){
			instance = new DeviceStatisticsHashService();
		}
		return instance;
	}
	private DeviceStatisticsHashService(){
		
	}
	
	private static String generatePrefixKey(){
		return BusinessKeyDefine.Statistics.UserDeviceStatistics;
	}
	
	
	
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getRedisKey() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JedisPool getRedisPool() {
		// TODO Auto-generated method stub
		return null;
	}

}
