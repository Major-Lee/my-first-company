package com.wecite.toplines.business.bucache.local.serviceimpl;

import java.util.List;

import javax.annotation.PostConstruct;

import com.smartwork.msip.cores.cache.entitycache.Cache;
import com.smartwork.msip.cores.cache.entitycache.CacheService;
import com.smartwork.msip.cores.cache.entitycache.impl.ehcache.EhCache;
import com.smartwork.msip.cores.cache.entitycache.impl.ehcache.EhCacheService;
import com.smartwork.msip.cores.helper.StringHelper;
/**
 * 容器本地缓存实现
 * @author Edmond
 *
 */
//@Service
public class LocalEhcacheService {
	private final String CACHEKEY_FETCH_FILTER_PREFIX = "FetchFilterPrefix";
	
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
    
    private String generateKey(String type){
		StringBuilder sb = new StringBuilder(CACHEKEY_FETCH_FILTER_PREFIX);
		sb.append(StringHelper.POINT_CHAR_GAP).append(type);
		return sb.toString();
	}
    public void putLocalCache(String stype, List<Object> result){
		String key = generateKey(stype);
		getLocalCache().put(key, result);
	}
    @SuppressWarnings("unchecked")
	public List<Object> getLocalCache(String type){
		String key = generateKey(type);
		Object existCache = getLocalCache().get(key);
		if(existCache != null){
			return (List<Object>)existCache;
		}
		return null;
	}
}
