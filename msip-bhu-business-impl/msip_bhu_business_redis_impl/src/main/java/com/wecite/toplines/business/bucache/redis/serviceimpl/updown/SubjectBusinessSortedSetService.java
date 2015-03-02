package com.wecite.toplines.business.bucache.redis.serviceimpl.updown;

import java.util.Set;

import redis.clients.jedis.JedisPool;

import com.smartwork.msip.cores.cache.relationcache.impl.jedis.RedisKeyEnum;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.RedisPoolManager;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.impl.AbstractRelationSortedSetCache;
import com.wecite.toplines.business.bucache.redis.serviceimpl.BusinessKeyDefine;

/**
 * 文章的业务redis service
 * 1：文章顶过的用户id 列表 按顶的时间排序
 * @Lawliet
 */
public class SubjectBusinessSortedSetService extends AbstractRelationSortedSetCache{
	
	
	private static class ServiceHolder{ 
		private static SubjectBusinessSortedSetService instance =new SubjectBusinessSortedSetService(); 
	}
	/**
	 * 获取工厂单例
	 * @return
	 */
	public static SubjectBusinessSortedSetService getInstance() { 
		return ServiceHolder.instance; 
	}
	
	private SubjectBusinessSortedSetService(){
		
	}
	
	private static String generateSubjectPrefixKey(String businessKey,String subjectid){
		StringBuilder sb = new StringBuilder(BusinessKeyDefine.Present.SubjectPrefixKey);
		sb.append(businessKey).append(subjectid);
		return sb.toString();
	}
	
	/*************************Subject 顶业务的用户记录 开始******************************/
	private static final String SubjectUserUpAndDownMarkedPrefixKey = "SUD.";//"Up&Down";
	
	public void addSubjectUpUser(String subjectid, String uid){
		super.zadd(generateSubjectPrefixKey(SubjectUserUpAndDownMarkedPrefixKey, subjectid), System.currentTimeMillis(), uid);
	}
	
	public void removeSubjectUpUser(String subjectid, String uid){
		super.zrem(generateSubjectPrefixKey(SubjectUserUpAndDownMarkedPrefixKey, subjectid), uid);
	}
	
	public Set<String> fetchSubjectUpUser(String subjectid,int start,int size){
		return super.zrevrange(generateSubjectPrefixKey(SubjectUserUpAndDownMarkedPrefixKey, subjectid), start, start+size-1);
	}
	
	/*************************Subject 顶业务的用户记录 结束******************************/
	@Override
	public String getRedisKey() {
		return null;
	}
	
	@Override
	public String getName() {
		return SubjectBusinessSortedSetService.class.getName();
	}
	
	@Override
	public JedisPool getRedisPool() {
		return RedisPoolManager.getInstance().getPool(RedisKeyEnum.PRESENT);
	}
}
