package com.bhu.vas.business.backend.terminalstatus.asyncprocessor.service;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Service;

import com.alisoft.xplatform.asf.cache.ICacheManager;
import com.alisoft.xplatform.asf.cache.IMemcachedCache;
import com.alisoft.xplatform.asf.cache.memcached.CacheUtil;
import com.alisoft.xplatform.asf.cache.memcached.MemcachedCacheManager;

/**
 * Memcached缓存服务
 * @author  PengYu Zhang
 *
 */
@Service
public class PortraitMemcachedCacheService {
	
	//记录设备对于某终端的上线通知时间记录
	private final String PORTRAITCachePrefixKey = "PORTRAITCacheKey.";
	
	ICacheManager<IMemcachedCache> manager;  
	IMemcachedCache cache;
	
	
	@PostConstruct
	protected void init() {
//		entityCache = cacheService.addCache(this.getClass().getName(),10*10000,2*3600);//2小时
		
        manager = CacheUtil.getCacheManager(IMemcachedCache.class, MemcachedCacheManager.class.getName());  
        manager.start();  
        cache = manager.getCache("default.memcached");  
    }
	

	
	public String generatePortraitCacheKeyBy(String mac){
		StringBuilder sb = new StringBuilder();
		sb.append(PORTRAITCachePrefixKey).append(mac);
		return sb.toString();
	}
	
	public void storePortraitCacheResult(String mac,String result){
		String key = generatePortraitCacheKeyBy(mac);
		this.cache.remove(key);
		this.cache.put(key, result);
	}
	
	public String getPortraitOrderCacheByOrderId(String mac){
		Object cacheObj = this.cache.get(generatePortraitCacheKeyBy(mac));
		if(cacheObj == null) return null;
		return cacheObj.toString();
	}
}
