package com.bhu.vas.business.bucache.redis.serviceimpl.unique.impl.nick;

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
 * 1、通过nick定位uid
 * 2、nick唯一性验证
 * 
 * 由于nickname长度2-14
 * 结构：
 * 
 * key：nick 小写 去除所有空格 简体
 * field：uid value
 * 考虑以后用户量非常多的情况，不进行拆分机制 单纯以nick为key
 * @author edmond
 *
 */
public class UniqueNickHashService extends AbstractRelationHashCache{
	
	private static class UniqueNickHashServiceHolder{ 
		private static UniqueNickHashService instance =new UniqueNickHashService(); 
	}
	/**
	 * 获取工厂单例
	 * @return
	 */
	public static UniqueNickHashService getInstance() { 
		return UniqueNickHashServiceHolder.instance; 
	}
	
	private UniqueNickHashService(){
	}
	private static String generateKey(String nick){
		StringBuilder sb = new StringBuilder(BusinessKeyDefine.Unique.NickCheck);
		sb.append(StringHelper.POINT_CHAR_GAP).append(nick);
		return sb.toString();
	}
	/**
	 * 如果nick 为空 直接返回 存在 true
	 * @param nick 格式 小写 去除所有空格 简体
	 * @return
	 */
	public boolean check(String nick){
		String formater = format(nick);
		if(formater == null) return true;
		return this.exists(generateKey(formater));
	}
	
	/*public static Map<String,String> buildFiledAndValue(String uid,String pwd){
		Map<String,String> ret = new HashMap<String,String>();
		ret.put(BusinessFieldDefine.UidFiled, uid);
		ret.put(BusinessFieldDefine.PwdFiled, pwd);
		return ret;
	}*/
	/**
	 * 
	 * @param nick 格式 小写 去除所有空格 简体
	 * @param uid
	 */
	public boolean registerOrUpdate(String nick,int uid,String old){
		String formater = format(nick);
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
	public String fetchUidByNick(String nick){
		String formater = format(nick);
		if(formater == null) return null;
		return this.hget(generateKey(formater), BusinessFieldDefine.UidFiled);
	}

	private String format(String nick){
		if(nick == null) return null;
		nick = StringHelper.replaceBlankAndLowercase(nick);//nick.trim().toLowerCase();
		if(StringUtils.isEmpty(nick)) return null;
		return JChineseConvertor.convertToZhs(nick);//.getInstance().t2s(nick);
	}
	
	
	@Override
	public String getRedisKey() {
		return null;
	}
	@Override
	public String getName() {
		return UniqueNickHashService.class.getName();
	}
	@Override
	public JedisPool getRedisPool() {
		return RedisPoolManager.getInstance().getPool(RedisKeyEnum.UNIQUE);
	}
}
