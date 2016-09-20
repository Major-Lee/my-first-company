package com.bhu.vas.business.bucache.redis.serviceimpl.commdity;

import redis.clients.jedis.JedisPool;

import com.bhu.vas.business.bucache.redis.serviceimpl.BusinessKeyDefine;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.RedisKeyEnum;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.RedisPoolManager;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.impl.AbstractRelationHashCache;
import com.smartwork.msip.cores.helper.StringHelper;
/**
 * 用于存储限时上网商品类似的区间金额
 * 当用户支付成功订单 会清除相应记录
 * 每天会统一清除所有随进金额缓存
 * @author tangzichao
 *
 */
public class RewardOrderAmountHashService extends AbstractRelationHashCache{
	
	private static class ServiceHolder{ 
		private static RewardOrderAmountHashService instance =new RewardOrderAmountHashService(); 
	}

	public static RewardOrderAmountHashService getInstance() { 
		return ServiceHolder.instance; 
	}
	
	public String generateKey(String mac){
		StringBuilder sb = new StringBuilder();
		sb.append(BusinessKeyDefine.CommdityRAmount.CommdityIntervalAmountPrefixKey);
		sb.append(StringHelper.POINT_CHAR_GAP).append(mac);
		return sb.toString();
	}
	
	public String[] generateKeys(String... macs){
		if(macs == null) return null;
		String[] keys = new String[macs.length];
		int cursor = 0;
		for(String mac : macs){
			keys[cursor] = generateKey(mac);
			cursor++;
		}
		return keys;
	}
	
	public String generateField(String umac, Integer commdityid, Integer umactype){
		StringBuilder sb = new StringBuilder();
		sb.append(umac).append(StringHelper.POINT_CHAR_GAP).append(commdityid);
		sb.append(StringHelper.POINT_CHAR_GAP).append(umactype);
		return sb.toString();
	}
	
/*	public void addRAmount(String mac, String umac, Integer commdityid, String amount){
		super.set(generateKey(mac, umac, commdityid), amount);
	}*/
	
	public Long addNx_RAmount(String mac, String umac, Integer commdityid, Integer umactype, String amount){
		//return super.setnx(generateKey(mac, umac, commdityid), amount);
		return super.hsetnx(generateKey(mac), generateField(umac, commdityid, umactype), amount);
	}
	
	public String getRAmount(String mac, String umac, Integer commdityid, Integer umactype){
		//return super.get(generateKey(mac, umac, commdityid));
		return super.hget(generateKey(mac), generateField(umac, commdityid, umactype));
	}

	public void removeRAmount(String mac, String umac, Integer commdityid, Integer umactype){
		//super.del(generateKey(mac, umac, commdityid));
		super.hdel(generateKey(mac), generateField(umac, commdityid, umactype));
	}
	
	public void removeAllRAmountByMacs(String... macs){
		if(macs == null || macs.length == 0) return;
		super.del(generateKeys(macs));
	}

	@Override
	public String getRedisKey() {
		return null;
	}

	@Override
	public String getName() {
		return RewardOrderAmountHashService.class.getName();
	}

	@Override
	public JedisPool getRedisPool() {
		return RedisPoolManager.getInstance().getPool(RedisKeyEnum.COMMDITYRAMOUNT);
	}
}
