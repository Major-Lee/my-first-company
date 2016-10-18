package com.bhu.vas.business.bucache.redis.serviceimpl.devices;

import redis.clients.jedis.JedisPool;

import com.smartwork.msip.cores.cache.relationcache.impl.jedis.RedisKeyEnum;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.RedisPoolManager;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.impl.AbstractRelationListCache;

/**
 * 设备地理位置
 * @author xiaowei
 *
 */
public class WifiDevicePositionListService extends AbstractRelationListCache{

	@Override
	public String getName() {
		return WifiDevicePositionListService.class.getName();
	}

	@Override
	public String getRedisKey() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JedisPool getRedisPool() {
		return RedisPoolManager.getInstance().getPool(RedisKeyEnum.ADVERTISE);
	}

}
