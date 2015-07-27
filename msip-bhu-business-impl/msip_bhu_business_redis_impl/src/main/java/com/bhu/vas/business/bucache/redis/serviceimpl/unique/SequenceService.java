package com.bhu.vas.business.bucache.redis.serviceimpl.unique;

import java.util.HashMap;
import java.util.Map;

import redis.clients.jedis.JedisPool;

import com.bhu.vas.api.rpc.sequence.helper.IRedisSequenceGenable;
import com.bhu.vas.business.bucache.redis.serviceimpl.BusinessKeyDefine;
import com.smartwork.msip.business.runtimeconf.RuntimeConfiguration;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.RedisKeyEnum;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.RedisPoolManager;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.impl.AbstractRelationHashCache;
import com.smartwork.msip.localunit.RandomData;

public class SequenceService extends AbstractRelationHashCache {
	private static Map<String,SequenceRange> tableSequenceStart = new HashMap<String,SequenceRange>();
	private static class ServiceHolder {
		private static SequenceService instance = new SequenceService();
	}
	/**
	 * 获取工厂单例
	 * 
	 * @return
	 */
	public static SequenceService getInstance() {
		return ServiceHolder.instance;
	}

	private SequenceService() {
		//tableSequenceStart.put("com.bhu.vas.api.user.model.User", new SequenceRange(200000,Integer.MAX_VALUE,true));
		//tableSequenceStart.put("com.bhu.vas.api.subject.model.Subject",new SequenceRange(10000,Integer.MAX_VALUE,true));
		//4294967294
		//tableSequenceStart.put("com.bhu.vas.api.rpc.task.model.WifiDeviceDownTask",new SequenceRange(100000,4294967294l,true));
		tableSequenceStart.put("com.bhu.vas.api.rpc.task.model.WifiDeviceDownTask",new SequenceRange(100000,9999999,true));
	}

	public <T extends IRedisSequenceGenable> void onCreateSequenceKey(T model,
			boolean israndomincr) {
		if (israndomincr) {
			if (RuntimeConfiguration.AccountCreateContinuous) {
				model.setSequenceKey(getNextId(model.getClass().getName()));
			} else {
				model.setSequenceKey(getNextId(model.getClass().getName(),
						RandomData.intNumber(1, 5)));
			}
		} else
			model.setSequenceKey(getNextId(model.getClass().getName()));
	}
	
	public Long getNextId(String name, int incrnum) {
		Long nextid = this.hincrby(BusinessKeyDefine.Unique.Sequence, name, incrnum);
		//System.out.println("------------1:"+nextid);
		SequenceRange sequenceRange = tableSequenceStart.get(name);
		if(sequenceRange != null){
			//System.out.println("------------2:"+nextid);
			if(!sequenceRange.wasInRange(nextid.longValue())){
				//System.out.println("------------2:"+nextid);
				//TODO：如果是用于数据库中，需要清除表里的记录重新开始记录，例如WifiDeviceDownTask
				this.hset(BusinessKeyDefine.Unique.Sequence, name, String.valueOf(sequenceRange.getStart()));
				return sequenceRange.getStart();
			}
		}
		return nextid;
	}
	public Long getNextId(String name) {
		return getNextId(name,1);
	}
	@Override
	public String getRedisKey() {
		return null;
	}
	@Override
	public String getName() {
		return SequenceService.class.getName();
	}
	@Override
	public JedisPool getRedisPool() {
		return RedisPoolManager.getInstance().getPool(RedisKeyEnum.UNIQUE);
	}
}
