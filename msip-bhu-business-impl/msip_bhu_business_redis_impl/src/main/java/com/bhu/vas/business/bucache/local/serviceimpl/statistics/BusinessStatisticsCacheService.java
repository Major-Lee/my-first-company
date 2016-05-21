package com.bhu.vas.business.bucache.local.serviceimpl.statistics;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.bhu.vas.api.rpc.charging.dto.DeviceGroupPaymentInfo;
import com.smartwork.msip.cores.cache.entitycache.Cache;
import com.smartwork.msip.cores.cache.entitycache.CacheService;

/**
 * 依赖系统配置的缓存实现
 * @author Edmond
 *
 */
@Service
public class BusinessStatisticsCacheService {
	//记录用户设备分组的当日收益信息缓存
	private final String QDeviceGroupPaymentStatisticsCachePrefixKey = "QDGPaymentDSKey.";
	
	@Resource(name="coreCacheService")
	CacheService cacheService;
	Cache entityCache;
	
	@PostConstruct
	protected void init() {
		entityCache = cacheService.addCache(this.getClass().getName(),10*20000,1800);//0.5小时
    }
	
	
	public String generateDeviceGroupPaymentStatisticsDSCacheKeyBy(String group_date_str){
		StringBuilder sb = new StringBuilder();
		sb.append(QDeviceGroupPaymentStatisticsCachePrefixKey).append(group_date_str);
		return sb.toString();
	}
	
	public void storeWalletLogStatisticsDSCacheResult(String group_date_str,DeviceGroupPaymentInfo result){
		String key = generateDeviceGroupPaymentStatisticsDSCacheKeyBy(group_date_str);
		this.entityCache.remove(key);
		this.entityCache.put(key, result,1800);//1小时
	}
	
	public DeviceGroupPaymentInfo getWalletLogStatisticsDSCacheByUser(String group_date_str){
		Object cacheObj = this.entityCache.get(generateDeviceGroupPaymentStatisticsDSCacheKeyBy(group_date_str));
		if(cacheObj == null) return null;
		return DeviceGroupPaymentInfo.class.cast(cacheObj);
	}
}
