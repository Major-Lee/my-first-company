package com.wecite.toplines.business.bucache.redis.serviceimpl.updown;

import java.util.List;

import redis.clients.jedis.JedisPool;

import com.smartwork.msip.cores.cache.relationcache.impl.jedis.RedisKeyEnum;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.RedisPoolManager;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.impl.AbstractRelationHashCache;
import com.smartwork.msip.cores.helper.ArithHelper;
import com.smartwork.msip.cores.helper.StringHelper;
import com.wecite.toplines.business.bucache.redis.serviceimpl.BusinessKeyDefine;

/**
 * 承载业务 
 * 	1、文章顶踩业务
 * 	2、文章评星业务
 * @author edmond
 *
 */
public class UserBuinessMarkHashService extends AbstractRelationHashCache{
	
	
	private static class ServiceHolder{ 
		private static UserBuinessMarkHashService instance =new UserBuinessMarkHashService(); 
	}
	/**
	 * 获取工厂单例
	 * @return
	 */
	public static UserBuinessMarkHashService getInstance() { 
		return ServiceHolder.instance; 
	}
	private UserBuinessMarkHashService(){
		
	}
	private static String generateMarkPrefixKey(String businessKey,String uid){
		StringBuilder sb = new StringBuilder(BusinessKeyDefine.Present.UserMarkPrefixKey);
		sb.append(businessKey).append(uid);
		return sb.toString();
	}
	
	/*************************Subject 顶踩 业务标记开始******************************/
	//顶踩业务---------------------------------------------------
	private static final String UserUpAndDownMarkedPrefixKey = "UD.";//"Up&Down";
	public static final String UpValue = "1";
	public static final String DownValue = "0";
	//顶踩业务
	public void upAndDownMarked(String uid,String subject_id,String value){
		this.hset(generateMarkPrefixKey(UserUpAndDownMarkedPrefixKey,uid), subject_id, value);
	}

	public String upAndDownWhat(String uid,String subject_id){
		//System.out.println("key:"+generateMarkPrefixKey(UserUpAndDownMarkedPrefixKey,uid));
		String upOrDown = this.hget(generateMarkPrefixKey(UserUpAndDownMarkedPrefixKey,uid), subject_id);
		return upOrDown;
	}
	
	public List<String> upAndDownWhats(String uid,String[] subject_ids){
		return this.hmget(generateMarkPrefixKey(UserUpAndDownMarkedPrefixKey,uid), subject_ids);
	}
	
	public void upAndDownRemove(String uid,String subject_id){
		this.hdel(generateMarkPrefixKey(UserUpAndDownMarkedPrefixKey,uid), subject_id);
	}	
	
	//顶踩结束---------------------------------------------------
	/*****************************Subject 顶踩 结束************************************/
	/*****************************Subject 评星值标记开始************************************/
	//评星业务---------------------------------------------------
	private static final String UserEstimateMarkedPrefixKey = "US.";//"Up&Down";
	
	//评星业务
	public void estimateMarked(String uid,String subject_id,String estimate){
		this.hset(generateMarkPrefixKey(UserEstimateMarkedPrefixKey,uid), subject_id, estimate);
	}
	
	public double estimateMarkedAndSummay(String uid,String subject_id,String estimate){
		this.estimateMarked(uid, subject_id, estimate);
		return this.estimateSummaryMarked(subject_id, estimate);
	}

	public String estimateWhat(String uid,String subject_id){
		String upOrDown = this.hget(generateMarkPrefixKey(UserEstimateMarkedPrefixKey,uid), subject_id);
		return upOrDown;
	}
	
	public List<String> estimateWhats(String uid, String... subjectids){
		return this.hmget(generateMarkPrefixKey(UserEstimateMarkedPrefixKey,uid), subjectids);
	}
	
	public void estimateRemove(String uid,String subject_id){
		this.hdel(generateMarkPrefixKey(UserEstimateMarkedPrefixKey,uid), subject_id);
	}	
	
	private static final String EstimateSummaryMarkedPrefixKey = "ES.";//"Up&Down";
	public static final String EstimateSummary_Field_Number_Of_People = "nop";//评星的总人数
	public static final String EstimateSummary_Field_Sum_Of_Estimate = "soe";//评星的总星级
	
	//评星业务中记录文章被评星的总人数和总分数
	public double estimateSummaryMarked(String subject_id,String estimate){
		long numberOfPeople = this.hincrby(generateMarkPrefixKey(EstimateSummaryMarkedPrefixKey,subject_id), 
					EstimateSummary_Field_Number_Of_People, 1);
		long sumOfEstimate = this.hincrby(generateMarkPrefixKey(EstimateSummaryMarkedPrefixKey,subject_id), 
					EstimateSummary_Field_Sum_Of_Estimate, Long.parseLong(estimate));
		return ArithHelper.div(sumOfEstimate, numberOfPeople, 2);
	}
	/**
	 * 获取文章的评星总人数
	 * @param subject_id
	 * @return
	 */
	public long estimateNumberOfPeopleWhat(String subject_id){
		String numberOfPeople = this.hget(generateMarkPrefixKey(EstimateSummaryMarkedPrefixKey,subject_id),
				EstimateSummary_Field_Number_Of_People);
		if(StringHelper.isNotEmpty(numberOfPeople)){
			return Long.parseLong(numberOfPeople);
		}
		return 0;
	}
	
	/*****************************Subject 评星值结束************************************/
	
	
	
	/*****************************每篇文章每个用户分享的次数************************************/
	//评星业务---------------------------------------------------
	private static final String ArticleSharePerUserMarkedPrefixKey = "ASPU.";//"Article Share PerUser";
	
	//评星业务
	public long articleShareIncrMarked(String uid,String subject_id){
		return this.hincrby(generateMarkPrefixKey(ArticleSharePerUserMarkedPrefixKey,uid), subject_id, 1l);
		//this.hset(generateMarkPrefixKey(ArticleSharePerUserMarkedPrefixKey,uid), subject_id, estimate);
	}
	/*public String articleShareWhat(String uid,String subject_id){
		String upOrDown = this.hget(generateMarkPrefixKey(ArticleSharePerUserMarkedPrefixKey,uid), subject_id);
		return upOrDown;
	}*/
	
	public void articleShareRemove(String uid,String subject_id){
		this.hdel(generateMarkPrefixKey(ArticleSharePerUserMarkedPrefixKey,uid), subject_id);
	}	
	/*****************************每篇文章每个用户分享的次数 值结束************************************/
	@Override
	public String getRedisKey() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public String getName() {
		return UserBuinessMarkHashService.class.getName();
	}
	@Override
	public JedisPool getRedisPool() {
		return RedisPoolManager.getInstance().getPool(RedisKeyEnum.PRESENT);
	}
}
