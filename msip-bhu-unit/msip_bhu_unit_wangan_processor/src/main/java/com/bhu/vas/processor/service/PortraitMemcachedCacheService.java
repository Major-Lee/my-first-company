package com.bhu.vas.processor.service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.smartwork.msip.cores.cache.entitycache.Cache;
import com.smartwork.msip.cores.cache.entitycache.CacheService;

/**
 * Memcached缓存服务
 * @author  PengYu Zhang
 *
 */
@Service
public class PortraitMemcachedCacheService {
	
	//记录设备对于某终端的上线通知时间记录
	private final String PORTRAITCachePrefixKey = "PORTRAITCacheKey.";
	    
	@Resource(name="coreCacheService")    
	CacheService cacheService;
	Cache entityCache;    
	  
	@PostConstruct   
	protected void init() {  
		entityCache = cacheService.addCache(this.getClass().getName(),10*10000,2*24*3600);//2小时
    }    
	
	public String generatePortraitCacheKeyBy(String mac){
		StringBuilder sb = new StringBuilder();   
		sb.append(PORTRAITCachePrefixKey).append(mac);
		return sb.toString();
	}
	
	public void storePortraitCacheResult(String mac,String result){
		String key = generatePortraitCacheKeyBy(mac);
		this.entityCache.remove(key);
		this.entityCache.put(key, result);
	}
	
	public String getPortraitOrderCacheByOrderId(String mac){
		Object cacheObj = this.entityCache.get(generatePortraitCacheKeyBy(mac));
		if(cacheObj == null) return null;
		return cacheObj.toString();
	}
}
