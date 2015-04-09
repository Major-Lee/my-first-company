package com.bhu.vas.business.bucache.redis.serviceimpl.unique.impl.email;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import redis.clients.jedis.JedisPool;

import com.bhu.vas.business.bucache.redis.serviceimpl.BusinessFieldDefine;
import com.bhu.vas.business.bucache.redis.serviceimpl.BusinessKeyDefine;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.RedisKeyEnum;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.RedisPoolManager;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.impl.AbstractRelationHashCache;
import com.smartwork.msip.cores.helper.StringHelper;

/**
 * 功能支持：
 * 1、email，密码登录验证
 * 2、email唯一性验证
 * 
 * 结构：
 * key：email trim lowcase
 * field：uid value
 * field：pwd value
 * 考虑以后用户量非常多的情况，不进行拆分机制 单纯以email为key
 * @author edmond
 *
 */
public class UniqueEmailHashService extends AbstractRelationHashCache{
	
	private static class UniqueEmailHashServiceHolder{ 
		private static UniqueEmailHashService instance =new UniqueEmailHashService(); 
	}
	/**
	 * 获取工厂单例
	 * @return
	 */
	public static UniqueEmailHashService getInstance() { 
		return UniqueEmailHashServiceHolder.instance; 
	}
	
	private UniqueEmailHashService(){
	}
	private static String generateKey(String email){
		StringBuilder sb = new StringBuilder(BusinessKeyDefine.Unique.EmailCheck);
		sb.append(StringHelper.POINT_CHAR_GAP).append(email);
		return sb.toString();
	}
	
	/**
	 * 如果email 为空 直接返回 存在 true
	 * @param email
	 * @return
	 */
	public boolean check(String email){
		String formater = format(email);
		if(formater == null) return true;
		return this.exists(generateKey(formater));
	}
	
	public static Map<String,String> buildFiledAndValue(String uid,String pwd){
		Map<String,String> ret = new HashMap<String,String>();
		ret.put(BusinessFieldDefine.UidFiled, uid);
		ret.put(BusinessFieldDefine.PwdFiled, pwd);
		return ret;
	}
	
	/**
	 * 
	 * @param email 
	 * @param uid
	 * @param pwd 此值可能变更
	 */
	public boolean registerOrUpdate(String email,int uid,String pwd,String old){
		String formater = format(email);
		if(formater == null) return false;
		this.hmset(generateKey(formater), buildFiledAndValue(String.valueOf(uid),pwd));
		
		String formaterOld = format(old);
		if(formaterOld != null){
			this.expire(generateKey(formaterOld), 0);
		}
		return true;
		//this.hset(generateKey(email), String.valueOf(uid), String.valueOf(token));
	}
	
	public String fetchUidByEmail(String email){
		String formater = format(email);
		if(formater == null) return null;
		return this.hget(generateKey(formater),BusinessFieldDefine.UidFiled);
	}
	
	public Map<String,String> fetchAllByEmail(String email){
		String formater = format(email);
		if(formater == null) return null;
		return this.hgetall(generateKey(formater));
	}
	
	private String format(String email){
		if(email == null) return null;
		email = email.trim().toLowerCase();
		if(StringUtils.isEmpty(email)) return null;
		return email;
	}
	
	@Override
	public String getRedisKey() {
		return null;
	}
	@Override
	public String getName() {
		return UniqueEmailHashService.class.getName();
	}
	@Override
	public JedisPool getRedisPool() {
		return RedisPoolManager.getInstance().getPool(RedisKeyEnum.UNIQUE);
	}
	
	public static void main(String[] argv){
		//System.out.println("aa".hashCode());
	}
}
