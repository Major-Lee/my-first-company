package com.bhu.vas.business.bucache.local.serviceimpl.wallet;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.bhu.vas.api.rpc.user.dto.ShareDealWalletSummaryProcedureVTO;
import com.smartwork.msip.cores.cache.entitycache.Cache;
import com.smartwork.msip.cores.cache.entitycache.CacheService;

/**
 * 依赖系统配置的缓存实现
 * @author Edmond
 *
 */
@Service
public class BusinessWalletCacheService {
	// 记录代理商设备状态缓存
	private final String QWalletLogStatisticsCachePrefixKey = "QWalletDSKey.";
	
	@Resource(name="coreCacheService")
	CacheService cacheService;
	Cache entityCache;
	
	@PostConstruct
	protected void init() {
		entityCache = cacheService.addCache(this.getClass().getName(), 10 * 20000, 1800);//0.5小时
    }
	
	
	public String generateWalletLogStatisticsDSCacheKeyBy(int userId){
		StringBuilder sb = new StringBuilder();
		sb.append(QWalletLogStatisticsCachePrefixKey).append(userId);
		return sb.toString();
	}
	
	public void storeWalletLogStatisticsDSCacheResult(int userId, ShareDealWalletSummaryProcedureVTO result){
		String key = generateWalletLogStatisticsDSCacheKeyBy(userId);
		this.entityCache.remove(key);
		this.entityCache.put(key, result, 1800);//1小时
	}
	
	public ShareDealWalletSummaryProcedureVTO getWalletLogStatisticsDSCacheByUser(int userId){
		Object cacheObj = this.entityCache.get(generateWalletLogStatisticsDSCacheKeyBy(userId));
		if(cacheObj == null) return null;
		return ShareDealWalletSummaryProcedureVTO.class.cast(cacheObj);
	}
	
	public void removeWalletLogStatisticsDSCacheResult(int userId) {
		String key = generateWalletLogStatisticsDSCacheKeyBy(userId);
		this.entityCache.remove(key);
	}
}
