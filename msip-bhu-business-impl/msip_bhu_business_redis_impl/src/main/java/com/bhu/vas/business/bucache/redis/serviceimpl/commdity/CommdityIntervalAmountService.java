package com.bhu.vas.business.bucache.redis.serviceimpl.commdity;

import redis.clients.jedis.JedisPool;

import com.bhu.vas.business.bucache.redis.serviceimpl.BusinessKeyDefine;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.RedisKeyEnum;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.RedisPoolManager;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.impl.AbstractRelationStringCache;
import com.smartwork.msip.cores.helper.StringHelper;
/**
 * 用于存储限时上网商品类似的区间金额
 * 当用户支付成功订单 会清除相应记录
 * 每天会统一清除所有随进金额缓存
 * @author tangzichao
 *
 */
public class CommdityIntervalAmountService extends AbstractRelationStringCache{
	
	private static class ServiceHolder{ 
		private static CommdityIntervalAmountService instance =new CommdityIntervalAmountService(); 
	}

	public static CommdityIntervalAmountService getInstance() { 
		return ServiceHolder.instance; 
	}
	
	public String generateKey(String mac, String umac, Integer commdityid){
		StringBuilder sb = new StringBuilder();
		sb.append(BusinessKeyDefine.CommdityRAmount.CommdityIntervalAmountPrefixKey).append(StringHelper.POINT_CHAR_GAP);
		sb.append(mac).append(StringHelper.POINT_CHAR_GAP);
		sb.append(umac).append(StringHelper.POINT_CHAR_GAP);
		sb.append(commdityid);
		return sb.toString();
	}
	
	public void addRAmount(String mac, String umac, Integer commdityid, String amount){
		super.set(generateKey(mac, umac, commdityid), amount);
	}
	
	public Long addNx_RAmount(String mac, String umac, Integer commdityid, String amount){
		return super.setnx(generateKey(mac, umac, commdityid), amount);
	}
	
	public String getRAmount(String mac, String umac, Integer commdityid){
		return super.get(generateKey(mac, umac, commdityid));
	}

	public void removeRAmount(String mac, String umac, Integer commdityid){
		super.del(generateKey(mac, umac, commdityid));
	}

	@Override
	public String getRedisKey() {
		return null;
	}

	@Override
	public String getName() {
		return CommdityIntervalAmountService.class.getName();
	}

	@Override
	public JedisPool getRedisPool() {
		return RedisPoolManager.getInstance().getPool(RedisKeyEnum.COMMDITYRAMOUNT);
	}
}
