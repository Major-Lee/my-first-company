package com.bhu.vas.business.bucache.redis.serviceimpl.token;

import org.apache.commons.lang.StringUtils;

import redis.clients.jedis.JedisPool;

import com.bhu.vas.business.bucache.redis.serviceimpl.BusinessKeyDefine;
import com.bhu.vas.exception.TokenValidateBusinessException;
import com.smartwork.msip.business.token.ITokenService;
import com.smartwork.msip.business.token.service.TokenServiceHelper;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.RedisKeyEnum;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.RedisPoolManager;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.impl.AbstractRelationHashCache;
import com.smartwork.msip.cores.helper.HashAlgorithmsHelper;
import com.smartwork.msip.cores.helper.StringHelper;

/**
 * 考虑以后用户量非常多的情况，类似拆表数据存储实现机制
 * 目前拆成2000个redis hash存储数据
 * 拆分对象为uid，存储数据为token
 * @author edmond
 *
 */
public class IegalTokenHashService extends AbstractRelationHashCache{
	
	private static class IegalTokenHashServiceHolder{ 
		private static IegalTokenHashService instance =new IegalTokenHashService(); 
	}
	/**
	 * 获取工厂单例
	 * @return
	 */
	public static IegalTokenHashService getInstance() { 
		return IegalTokenHashServiceHolder.instance; 
	}
	
	private IegalTokenHashService(){
	}
	//private static final String UserTokenPrefixKey = "com.et.business.bucache.redis.service.token.IegalTokenHashService.JingUserTokenHash";
	//private static final String UserTokenPrefixKey = "JingUserTokenHash";
	//暫時假定1百萬用戶，保證每個hashkey中存儲的不超過1000條數據，遵循redis 對於hash結構在不超過1000條數據的情況下壓縮及性能最優
	public static final int hasPrimeValue = 2000;
	
	private static String generateKey(int uid){
		return generateKey(String.valueOf(uid));
	}
	
	private static String generateKey(String uid){
		int hashvalue = HashAlgorithmsHelper.additiveHash(uid, hasPrimeValue);
		return generateKeyByHashValue(hashvalue);
		/*StringBuilder sb = new StringBuilder(BusinessKeyDefine.Present.UserTokenPrefixKey);
		sb.append(StringHelper.POINT_CHAR_GAP).append(String.format("%04d", hashvalue));
		return sb.toString();*/
	}
	
	private static String generateKeyByHashValue(int hashvalue){
		StringBuilder sb = new StringBuilder(BusinessKeyDefine.Present.UserTokenPrefixKey);
		sb.append(StringHelper.POINT_CHAR_GAP).append(String.format("%04d", hashvalue));
		return sb.toString();
	}
	
	public void userTokenRegister(int uid,String token){
		this.hset(generateKey(uid), String.valueOf(uid), String.valueOf(token));
	}
	
	public void userTokenRemove(int uid){
		this.hdel(generateKey(uid), String.valueOf(uid));//(generateKey(uid), String.valueOf(uid), String.valueOf(System.currentTimeMillis()));
	}
	
	public boolean userAndTokenExist(int uid,String token){
		String rtoken = this.hget(generateKey(uid), String.valueOf(uid));
		if(rtoken == null) return false;
		return rtoken.equals(token);
	}

	public boolean validateUserToken(String token){
		try{
			if(TokenServiceHelper.isNotExpiredAccessToken4User(token)){
				Integer uid = TokenServiceHelper.parserAccessToken4User(token);
				return userAndTokenExist(uid.intValue(),token);
			}else return false;
		}catch(Exception ex){
			System.out.println("token validate failure:"+token);
			ex.printStackTrace(System.out);
			return false;
		}
	}
	
	/**
	 * uidParam 不为空的情况下进行uidParam和token中解析出来的uid判断
	 * 如果uidParam为空，则验证反而弱了？
	 * @param token
	 * @param uidParam
	 * @return
	 */
	public boolean validateUserToken(String token,int uidParam){
		try{
			if(TokenServiceHelper.isNotExpiredAccessToken4User(token)){
				Integer uid = TokenServiceHelper.parserAccessToken4User(token);
				if(uid.intValue() != uidParam){
					throw new TokenValidateBusinessException(uid,ITokenService.Access_Token_CONTENT_UID_NotMatch);
					//return false;
				}
				/*if(StringUtils.isNotEmpty(uidParam)){
					if(uid.intValue() != Integer.parseInt(uidParam)){
						throw new TokenValidateBusinessException(uid,ITokenService.Access_Token_CONTENT_UID_NotMatch);
						//return false;
					}
				}*/
				return userAndTokenExist(uid.intValue(),token);
			}else return false;
		}catch(NumberFormatException ex){
			ex.printStackTrace(System.out);
			throw new TokenValidateBusinessException(ITokenService.Access_Token_CONTENT_UID_NotMatch);
			//return false;
		}
	}
	
	public void clearOrResetAll(){
		for(int i=0;i<hasPrimeValue;i++){
			this.expire(generateKeyByHashValue(i),0);
		}
	}
	@Override
	public String getRedisKey() {
		return null;
	}
	@Override
	public String getName() {
		return IegalTokenHashService.class.getName();
	}
	@Override
	public JedisPool getRedisPool() {
		return RedisPoolManager.getInstance().getPool(RedisKeyEnum.PRESENT);
	}

}
