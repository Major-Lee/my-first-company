package com.bhu.vas.business.bucache.redis.serviceimpl.unique.impl.mobleno;

import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import redis.clients.jedis.JedisPool;

import com.bhu.vas.business.bucache.redis.serviceimpl.BusinessFieldDefine;
import com.bhu.vas.business.bucache.redis.serviceimpl.BusinessKeyDefine;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.RedisKeyEnum;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.RedisPoolManager;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.impl.AbstractRelationHashCache;
import com.smartwork.msip.cores.helper.StringHelper;
import com.smartwork.msip.cores.helper.phone.PhoneHelper;

/**
 * 功能支持：
 * 1、通过Mobileno定位uid
 * 2、Mobileno唯一性验证
 * 
 * 由于Mobileno长度4-16
 * 结构：
 * 
 * key：mobileno 小写 去除所有空格
 * field：uid value
 * 考虑以后用户量非常多的情况，不进行拆分机制 单纯以mobileno为key
 * @author edmond
 *
 */
public class UniqueMobilenoHashService extends AbstractRelationHashCache{
	
	private static class UniqueMobilenoHashServiceHolder{ 
		private static UniqueMobilenoHashService instance =new UniqueMobilenoHashService(); 
	}
	/**
	 * 获取工厂单例
	 * @return
	 */
	public static UniqueMobilenoHashService getInstance() { 
		return UniqueMobilenoHashServiceHolder.instance; 
	}
	
	private UniqueMobilenoHashService(){
	}
	
	private static String generatePrefixKey(){
		StringBuilder sb = new StringBuilder(BusinessKeyDefine.Unique.MobilenoCheck);
		sb.append(StringHelper.POINT_CHAR_GAP).append("*");
		return sb.toString();
	}
	
	private static String generateKey(int countryCode,String mobileno){
		StringBuilder sb = new StringBuilder(BusinessKeyDefine.Unique.MobilenoCheck);
		if(countryCode == 0)
			sb.append(StringHelper.POINT_CHAR_GAP).append(mobileno);
		else
			sb.append(StringHelper.POINT_CHAR_GAP).append(PhoneHelper.format(countryCode, mobileno));
		return sb.toString();
	}
	/**
	 * 如果permalink 为空 直接返回 存在 true
	 * @param permalink 格式 小写 去除左右空格
	 * @return
	 */
	public boolean check(int countryCode,String mobileno){
		String formater = format(mobileno);
		if(formater == null) return true;
		return this.exists(generateKey(countryCode,formater));
	}
	
	public Set<String> keySet(){
		Set<String> set = this.keys(generatePrefixKey());
		return set;
	}
	
	/*public static Map<String,String> buildFiledAndValue(String uid,String pwd){
		Map<String,String> ret = new HashMap<String,String>();
		ret.put(BusinessFieldDefine.UidFiled, uid);
		ret.put(BusinessFieldDefine.PwdFiled, pwd);
		return ret;
	}*/
	/**
	 * 
	 * @param permalink 格式 小写 去除左右空格
	 * @param uid
	 */
	public boolean registerOrUpdate(int countryCode,String mobileno,int uid,String old){
		String formater = format(mobileno);
		if(formater == null) return false;
		this.hset(generateKey(countryCode,formater), BusinessFieldDefine.UidFiled, String.valueOf(uid));
		String formaterOld = format(old);
		if(formaterOld != null){
			this.expire(generateKey(countryCode,formaterOld), 0);
		}
		return true;
		//this.hmset(generateKey(email), buildFiledAndValue(String.valueOf(uid),pwd));
		//this.hset(generateKey(email), String.valueOf(uid), String.valueOf(token));
	}
	
	public boolean remove(int countryCode,String mobileno){
		long ret = super.del(generateKey(countryCode,mobileno));
		if(ret > 0) return true;
		return false;
	}
	
	/**
	 * 
	 * @param mobileno 格式 小写 去除左右空格
	 */
	public String fetchUidByMobileno(int countryCode,String mobileno){
		String formater = format(mobileno);
		if(formater == null) return null;
		return this.hget(generateKey(countryCode,formater), BusinessFieldDefine.UidFiled);
	}

	public String fetchUidByMobileno(String mobilenoWithCountryCode){
		return this.hget(generateKey(mobilenoWithCountryCode), BusinessFieldDefine.UidFiled);
	}
	
	private static String generateKey(String mobilenoWithCountryCode){
		return generateKey(0,mobilenoWithCountryCode);
	}
	
	public Map<String,String> fetchAll(int countryCode,String mobileno){
		String formater = format(mobileno);
		if(formater == null) return null;
		return this.hgetall(generateKey(countryCode,formater));
	}
	
	private String format(String mobileno){
		if(mobileno == null) return null;
		mobileno = StringHelper.replaceBlankAndLowercase(mobileno);//.trim().toLowerCase();
		if(StringUtils.isEmpty(mobileno)) return null;
		return mobileno;
	}
	
	@Override
	public String getRedisKey() {
		return null;
	}
	@Override
	public String getName() {
		return UniqueMobilenoHashService.class.getName();
	}
	@Override
	public JedisPool getRedisPool() {
		return RedisPoolManager.getInstance().getPool(RedisKeyEnum.PRESENT);
	}
}
