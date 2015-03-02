package com.wecite.toplines.business.bucache.redis.serviceimpl.statistics;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Tuple;

import com.smartwork.msip.cores.cache.relationcache.impl.jedis.RedisKeyEnum;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.RedisPoolManager;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.impl.AbstractRelationSortedSetCache;
import com.smartwork.msip.cores.helper.StringHelper;
import com.wecite.toplines.business.bucache.redis.serviceimpl.BusinessKeyDefine;

/**
 *  用户收到所有聊天消息信息存储空间
 *  ZSET 
 *  	key：user 
 *  	score 消息接收时间
 *  	value msgid
 *  包括	
 *  	聊天离线消息
 * @author edmond
 *
 */
public class SubjectStatisticsFragmentService extends AbstractRelationSortedSetCache{
	
	private static class ServiceHolder{ 
		private static SubjectStatisticsFragmentService instance =new SubjectStatisticsFragmentService(); 
	}
	/**
	 * 获取工厂单例
	 * @return
	 */
	public static SubjectStatisticsFragmentService getInstance() { 
		return ServiceHolder.instance; 
	}
	
	private SubjectStatisticsFragmentService(){
	}
	
	private static String generateKey(String fragment){
		StringBuilder sb = new StringBuilder(BusinessKeyDefine.Statistics.SubjectFragment);
		sb.append(StringHelper.POINT_CHAR_GAP).append(fragment);
		return sb.toString();
	}
	private static String generateKeyWithTag(String fragment,String tag){
		StringBuilder sb = new StringBuilder(BusinessKeyDefine.Statistics.SubjectFragment);
		sb.append(StringHelper.POINT_CHAR_GAP).append(fragment).append(StringHelper.POINT_CHAR_GAP).append(tag);
		return sb.toString();
	}
	
	public Double subjectClickComming(String fragment,String subjectid,double upMinusDown,
			Collection<Integer> tags, Collection<String> domains){
		//System.out.println("2 - 1");
		Double ret = this.zincrby(generateKey(fragment), upMinusDown, subjectid);
		System.out.println("subjectClickComming ret:"+ret);
//		if(tags != null && !tags.isEmpty()){
//			Set<String> keys = new HashSet<String>();
//			for(Integer tagid:tags){
//				keys.add(generateKeyWithTag(fragment,String.valueOf(tagid)));
//			}
//			this.pipelineZIncr_diffKeyWithSameMember(keys, upMinusDown, subjectid);
//		}
		//System.out.println("2 - 2");
		this.subjectClickTagsComming(fragment, subjectid, upMinusDown, tags);
		//System.out.println("2 - 3");
		this.subjectClickDomainsComming(fragment, subjectid, upMinusDown, domains);
		//System.out.println("2 - 4");
		return ret;
	}
	
	public void subjectClickTagsComming(String fragment,String subjectid,double upMinusDown,Collection<Integer> tags){
		if(tags != null && !tags.isEmpty()){
			Set<String> keys = new HashSet<String>();
			for(Integer tagid:tags){
				keys.add(generateKeyWithTag(fragment,String.valueOf(tagid)));
			}
			this.pipelineZIncr_diffKeyWithSameMember(keys, upMinusDown, subjectid);
		}
	}
	
	public void subjectClickDomainsComming(String fragment,String subjectid,double upMinusDown,Collection<String> domains){
		if(domains != null && !domains.isEmpty()){
			Set<String> keys = new HashSet<String>();
			for(String domain:domains){
				keys.add(generateKeyWithTag(fragment,domain));
			}
			this.pipelineZIncr_diffKeyWithSameMember(keys, upMinusDown, subjectid);
		}
	}
	
	/**
	 * 获取score
	 * score == null 则 member不存在
	 * @param fragment
	 * @param tag
	 * @param member
	 * @return
	 */
	public Double subjectClickScore(String fragment,String tag,String member){
		if(StringHelper.isEmpty(tag)){
			return this.zscore(generateKey(fragment), member);
		}else{
			return this.zscore(generateKeyWithTag(fragment,tag), member);
		}
	}
	
	public List<Object> subjectClickScores(String fragment,String... members){
		return super.pipelineZScore_sameKeyWithDiffMember(generateKey(fragment), members);
	}
	
	public long subjectClickCount(String fragment){
		return this.subjectClickCount(fragment, null);
	}
	
	public long subjectClickCount(String fragment, String tag){
		if(StringHelper.isEmpty(tag)){
			return this.zcard(generateKey(fragment));
		}else{
			return this.zcard(generateKeyWithTag(fragment, tag));
		}
	}
	
	/*public boolean subjectClickExist(String fragment,String tag,String member){
		if(StringHelper.isEmpty(tag)){
			return this.zrank(generateKey(fragment), member);
		}else{
			return this.zrank(generateKeyWithTag(fragment,tag), member);
		}
	}*/
	
	public void expireFragment(String fragment){
		this.expire(generateKey(fragment), 0);
	}
	
	public Long clearAllSubjectClick(String fragment,String subjectid,Collection<Integer> tags, Collection<String> domains){
		Long ret = super.zrem(generateKey(fragment), subjectid);
		this.subjectClickTagsRemove(fragment, tags, subjectid);
		this.subjectClickDomainsRemove(fragment, domains, subjectid);
		return ret;
	}
	
	public long subjectClickRemove(String fragment, String subjectid){
		return super.zrem(generateKey(fragment), subjectid);
	}
	public long subjectClickTagRemove(String fragment,String tag, String subjectid){
		return super.zrem(generateKeyWithTag(fragment,tag), subjectid);
	}
	
	public void subjectClickTagsRemove(String fragment,Collection<Integer> tags, String subjectid){
		if(tags == null || tags.isEmpty()) return;
		int size = tags.size();
		String[] keys = new String[size];
		String[] members = new String[size];
		int i=0;
		for(Integer tag:tags){
			keys[i] = generateKeyWithTag(fragment,String.valueOf(tag));
			members[i] = subjectid;
			i++;
		}
		super.pipelineZRem_diffKeyWithDiffMember(keys, members);//.zrem_pipeline_diffkeyWithDiffvalue(keys, values);
	}
	
	public void subjectClickDomainsRemove(String fragment,Collection<String> domains, String subjectid){
		if(domains == null || domains.isEmpty()) return;
		int size = domains.size();
		String[] keys = new String[size];
		String[] members = new String[size];
		int i=0;
		for(String domain:domains){
			keys[i] = generateKeyWithTag(fragment,domain);
			members[i] = subjectid;
			i++;
		}
		super.pipelineZRem_diffKeyWithDiffMember(keys, members);//.zrem_pipeline_diffkeyWithDiffvalue(keys, values);
	}
	
	public Set<Tuple> fetchSubjectClickBy(String fragment,int start,int size){
		return fetchSubjectClickBy(fragment,null,start,size);
	}
	/**
	 * 按score大小，从大到小排序
	 * @param fragment
	 * @param start
	 * @param size
	 * @return
	 */
	public Set<Tuple> fetchSubjectClickBy(String fragment,String tag,int start,int size){
		if(StringHelper.isEmpty(tag)){
			return super.zrevrangeWithScores(generateKey(fragment), start, start+size-1);
		}else{
			return super.zrevrangeWithScores(generateKeyWithTag(fragment,tag), start, start+size-1);
		}
	}
	
	@Override
	public String getRedisKey() {
		return null;
	}
	
	@Override
	public String getName() {
		return SubjectStatisticsFragmentService.class.getName();
	}
	
	@Override
	public JedisPool getRedisPool() {
		return RedisPoolManager.getInstance().getPool(RedisKeyEnum.PRESENT);
	}
}
