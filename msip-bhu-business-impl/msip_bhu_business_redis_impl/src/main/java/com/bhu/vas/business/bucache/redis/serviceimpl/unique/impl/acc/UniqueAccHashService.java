package com.bhu.vas.business.bucache.redis.serviceimpl.unique.impl.acc;

import org.apache.commons.lang.StringUtils;

import redis.clients.jedis.JedisPool;

import com.bhu.vas.business.bucache.redis.serviceimpl.BusinessFieldDefine;
import com.bhu.vas.business.bucache.redis.serviceimpl.BusinessKeyDefine;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.RedisKeyEnum;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.RedisPoolManager;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.impl.AbstractRelationHashCache;
import com.smartwork.msip.cores.helper.StringHelper;
import com.smartwork.msip.cores.helper.jcc.JChineseConvertor;

/**
 * 功能支持：
 * 1、唯一帐号验证
 * 2、通过唯一帐号得到其uid
 * @author edmond
 *
 */
public class UniqueAccHashService extends AbstractRelationHashCache{
	
	private static class UniqueAccHashServiceHolder{ 
		private static UniqueAccHashService instance =new UniqueAccHashService(); 
	}
	/**
	 * 获取工厂单例
	 * @return
	 */
	public static UniqueAccHashService getInstance() { 
		return UniqueAccHashServiceHolder.instance; 
	}
	
	private UniqueAccHashService(){
	}
	private static String generateKey(String acc){
		StringBuilder sb = new StringBuilder(BusinessKeyDefine.Unique.AccCheck);
		sb.append(StringHelper.POINT_CHAR_GAP).append(acc);
		return sb.toString();
	}
	/**
	 * 如果nick 为空 直接返回 存在 true
	 * @param nick 格式 小写 去除所有空格 简体
	 * @return
	 */
	public boolean check(String acc){
		String formater = format(acc);
		if(formater == null) return true;
		return this.exists(generateKey(formater));
	}

	/**
	 * 
	 * @param nick 格式 小写 去除所有空格 简体
	 * @param uid
	 */
	public boolean registerOrUpdate(String acc,int uid,String old){
		String formater = format(acc);
		if(formater == null) return false;
		this.hset(generateKey(formater), BusinessFieldDefine.UidFiled, String.valueOf(uid));
		String formaterOld = format(old);
		if(formaterOld != null){
			this.expire(generateKey(formaterOld), 0);
		}
		return true;
		//this.hmset(generateKey(email), buildFiledAndValue(String.valueOf(uid),pwd));
		//this.hset(generateKey(email), String.valueOf(uid), String.valueOf(token));
	}
	
	/**
	 * 
	 * @param nick 格式 小写 去除所有空格 简体
	 */
	public String fetchUidByAcc(String acc){
		String formater = format(acc);
		if(formater == null) return null;
		return this.hget(generateKey(formater), BusinessFieldDefine.UidFiled);
	}

	private String format(String acc){
		if(acc == null) return null;
		acc = StringHelper.replaceBlankAndLowercase(acc);//nick.trim().toLowerCase();
		if(StringUtils.isEmpty(acc)) return null;
		return JChineseConvertor.convertToZhs(acc);//.getInstance().t2s(nick);
	}
	
	
	@Override
	public String getRedisKey() {
		return null;
	}
	@Override
	public String getName() {
		return UniqueAccHashService.class.getName();
	}
	@Override
	public JedisPool getRedisPool() {
		return RedisPoolManager.getInstance().getPool(RedisKeyEnum.UNIQUE);
	}
}
