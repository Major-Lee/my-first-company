package com.bhu.vas.business.bucache.redis.serviceimpl.unique;

import redis.clients.jedis.JedisPool;

import com.bhu.vas.api.rpc.sequence.helper.IRedisSequenceGenable;
import com.bhu.vas.business.bucache.redis.serviceimpl.BusinessKeyDefine;
import com.smartwork.msip.business.runtimeconf.RuntimeConfiguration;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.RedisKeyEnum;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.RedisPoolManager;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.impl.AbstractRelationHashCache;
import com.smartwork.msip.localunit.RandomData;

/**
 * 任务id生成规则
 * 考虑以后设备量非常多的情况，
 * 采用hash结构，field是mac地址通过hash算法 权值为1024 
 * 类似拆表数据存储实现机制，并且不用通过数据库遍历可以把所有数据提取出来
 * 拆分对象为mac地址
 * @author edmond
 *
 */
public class TaskSequenceService extends AbstractRelationHashCache {
	//private static Map<String,SequenceRange> tableSequenceStart = new HashMap<String,SequenceRange>();
	public static final int hasPrimeValue = 1024;
	SequenceRange sRange = new SequenceRange(100000,9999999,true);
	//int hashvalue = HashAlgorithmsHelper.additiveHash(mac, hasPrimeValue);
	private static class ServiceHolder {
		private static TaskSequenceService instance = new TaskSequenceService();
	}
	/**
	 * 获取工厂单例
	 * 
	 * @return
	 */
	public static TaskSequenceService getInstance() {
		return ServiceHolder.instance;
	}

	private TaskSequenceService() {
		//tableSequenceStart.put("com.bhu.vas.api.user.model.User", new SequenceRange(200000,Integer.MAX_VALUE,true));
		//tableSequenceStart.put("com.bhu.vas.api.subject.model.Subject",new SequenceRange(10000,Integer.MAX_VALUE,true));
		//tableSequenceStart.put("com.bhu.vas.api.rpc.task.model.WifiDeviceDownTask",new SequenceRange(100000,9999999,true));
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
			//System.out.println("------------2:"+nextid);
		if(!sRange.wasInRange(nextid.longValue())){
			//System.out.println("------------2:"+nextid);
			//TODO：如果是用于数据库中，需要清除表里的记录重新开始记录，例如WifiDeviceDownTask
			this.hset(BusinessKeyDefine.Unique.Sequence, name, String.valueOf(sRange.getStart()));
			return sRange.getStart();
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
		return TaskSequenceService.class.getName();
	}
	@Override
	public JedisPool getRedisPool() {
		return RedisPoolManager.getInstance().getPool(RedisKeyEnum.UNIQUE);
	}
}
