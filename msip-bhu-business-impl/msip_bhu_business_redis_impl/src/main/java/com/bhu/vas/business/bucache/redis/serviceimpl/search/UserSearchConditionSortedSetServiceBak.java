package com.bhu.vas.business.bucache.redis.serviceimpl.search;
//package com.bhu.vas.business.bucache.redis.serviceimpl.search;
//
//import java.util.Set;
//
//import redis.clients.jedis.JedisPool;
//import redis.clients.jedis.Tuple;
//
//import com.bhu.vas.business.bucache.redis.serviceimpl.BusinessKeyDefine;
//import com.smartwork.msip.cores.cache.relationcache.impl.jedis.RedisKeyEnum;
//import com.smartwork.msip.cores.cache.relationcache.impl.jedis.RedisPoolManager;
//import com.smartwork.msip.cores.cache.relationcache.impl.jedis.impl.AbstractRelationSortedSetCache;
//import com.smartwork.msip.cores.helper.StringHelper;
//
///**
// *  保存用户的多组合条件的搜索记录
// *  ZSET 
// *  	key：uid
// *  	score 保存搜索的时间戳
// *  	value 搜索条件的数据格式
// * @author lawliet
// *
// */
//public class UserSearchConditionSortedSetService extends AbstractRelationSortedSetCache{
//	
//	private static class ServiceHolder{ 
//		private static UserSearchConditionSortedSetService instance = new UserSearchConditionSortedSetService(); 
//	}
//	/**
//	 * 获取工厂单例
//	 * @return
//	 */
//	public static UserSearchConditionSortedSetService getInstance() { 
//		return ServiceHolder.instance; 
//	}
//	
//	private UserSearchConditionSortedSetService(){
//	}
//	
//	private static String generateKey(int uid){
//		StringBuilder sb = new StringBuilder(BusinessKeyDefine.Present.UserSearchCondition);
//		sb.append(StringHelper.POINT_CHAR_GAP).append(uid);
//		return sb.toString();
//	}
//	
//	/**
//	 * 保存用户的搜索条件数据
//	 * @param uid
//	 * @param dtojson
//	 * @return
//	 */
//	public Long storeUserSearchCondition(int uid, String dtojson){
//		return storeUserSearchCondition(uid, System.currentTimeMillis(), dtojson);
//	}
//	
//	public Long storeUserSearchCondition(int uid, double score, String dtojson){
//		return super.zadd(generateKey(uid), score, dtojson);
//	}
//	
//	public Double zscore(int uid, String dtojson){
//		return super.zscore(generateKey(uid), dtojson);
//	}
//	
//	/**
//	 * 移除用户的搜索条件数据
//	 * @param uid
//	 * @param ts
//	 * @return
//	 */
//	public Long removeUserSearchCondition(int uid, long ts){
//		return super.zremrangeByScore(generateKey(uid), ts, ts);
//	}
//	
//	/**
//	 * 移除用户的搜索数据(多个)
//	 * @param uid
//	 * @param scores_array 搜索条件ts的数组
//	 */
//	public void removeUserSearchConditions(int uid, String[] scores_array){
//		if(scores_array == null || scores_array.length == 0) return;
//		int length = scores_array.length;
//		double[] startScores = new double[length];
//		double[] endScores = new double[length];
//		for(int i = 0;i<length;i++){
//			double score = Double.parseDouble(scores_array[i]);
//			startScores[i] = score;
//			endScores[i] = score;
//		}
//		super.pipelineZRemrangeByScore_sameKeyWithDiffScore(generateKey(uid), startScores, endScores);
//	}
//	
//	/**
//	 * 查询用户保存的搜索条件列表
//	 * @param uid
//	 * @param start
//	 * @param size
//	 * @return
//	 */
//	public Set<Tuple> fetchUserSearchConditions(int uid,int start,int size){
//		return super.zrevrangeWithScores(generateKey(uid), start, (start+size-1));
//	}
//
//	public Set<Tuple> fetchUserSearchConditionsByPage(int uid,int pageNo,int pageSize){
//		int start = pageNo * pageSize;
//		int end = start + pageSize - 1;
//		return super.zrevrangeWithScores(generateKey(uid), start, end);
//	}
//	
//	public long countUserSearchCondition(int uid){
//		return super.zcard(generateKey(uid));
//	}
//	
//	@Override
//	public String getRedisKey() {
//		return null;
//	}
//	
//	@Override
//	public String getName() {
//		return UserSearchConditionSortedSetService.class.getName();
//	}
//	
//	@Override
//	public JedisPool getRedisPool() {
//		return RedisPoolManager.getInstance().getPool(RedisKeyEnum.PRESENT);
//	}
//	
//}
