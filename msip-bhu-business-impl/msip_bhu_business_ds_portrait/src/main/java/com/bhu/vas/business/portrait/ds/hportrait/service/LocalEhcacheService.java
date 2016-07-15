package com.bhu.vas.business.portrait.ds.hportrait.service;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Service;

import com.bhu.vas.api.rpc.payment.vto.PaymentReckoningVTO;
import com.smartwork.msip.cores.cache.entitycache.Cache;
import com.smartwork.msip.cores.cache.entitycache.CacheService;
import com.smartwork.msip.cores.cache.entitycache.impl.ehcache.EhCache;
import com.smartwork.msip.cores.cache.entitycache.impl.ehcache.EhCacheService;
import com.smartwork.msip.cores.helper.StringHelper;
/**
 * 容器本地缓存实现
 * @author Pengyu
 *
 */
@Service
public class LocalEhcacheService {
	private final String CACHEKEY_FETCH_FILTER_PREFIX = "PORTRAIT";
	
	private CacheService localCacheService;
	private EhCache localCache;
	@PostConstruct
	protected void init() {
        EhCacheService ehcacheService = new EhCacheService();
        ehcacheService.setMaxLiveSecond(3600);
        ehcacheService.setMaxSize(10000);
        ehcacheService.setDefaultCacheName(this.getClass().getName()+".local");
        ehcacheService.init();
        localCacheService = ehcacheService;
		//Assert.notNull(this.cacheService, "cacheService must be set!");
		//getEntityCache();
		getLocalCache();
	}
	
    private Cache getLocalCache() {
        if(localCache == null){
            localCache = (EhCache) localCacheService.getDefaultCache();
           /// localCache.getCache().getCacheEventNotificationService().registerListener(new ClickClearEvent<MODEL>(this));
        }
        return localCache;
    }   
    
    private String generateKey(String orderId){
		StringBuilder sb = new StringBuilder(CACHEKEY_FETCH_FILTER_PREFIX);
		sb.append(StringHelper.POINT_CHAR_GAP).append(orderId);
		return sb.toString();
	}
    public void putLocalCache(String orderId, PaymentReckoningVTO result){
		String key = generateKey(orderId);
		getLocalCache().remove(key);
		getLocalCache().put(key, result);
	}
    @SuppressWarnings("unchecked")
	public PaymentReckoningVTO getLocalCache(String orderId){
		String key = generateKey(orderId);
		Object existCache = getLocalCache().get(key);
		if(existCache == null) return null;
		return PaymentReckoningVTO.class.cast(existCache);
	}
}
