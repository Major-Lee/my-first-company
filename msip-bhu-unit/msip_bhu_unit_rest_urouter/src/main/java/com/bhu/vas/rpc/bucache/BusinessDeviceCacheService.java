package com.bhu.vas.rpc.bucache;

import java.util.Collection;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.bhu.vas.api.vto.GeoMapVTO;
import com.smartwork.msip.cores.cache.entitycache.Cache;
import com.smartwork.msip.cores.cache.entitycache.CacheService;

/**
 * 依赖系统配置的缓存实现
 * @author lawliet
 *
 */
@Service
public class BusinessDeviceCacheService {
	private final String DeviceGeoMapCachePrefixKey = "DGM.";
	
	@Resource(name="coreCacheService")
	CacheService cacheService;
	Cache entityCache;
	
	
	@PostConstruct
	protected void init() {
		entityCache = cacheService.addCache(this.getClass().getName(),10*10000,2*3600);//2小时
    }
	
	public String generateDeviceGeoMapKeyBy(){
		return DeviceGeoMapCachePrefixKey;
	}
	
	public void storeDeviceGeoMapCacheResult(Collection<GeoMapVTO> result){
		String key = generateDeviceGeoMapKeyBy();
		this.entityCache.remove(key);
		this.entityCache.put(key, result);
	}
	
	@SuppressWarnings("unchecked")
	public Collection<GeoMapVTO> getDeviceGeoMapCacheByQ(){
		Object cacheObj = this.entityCache.get(generateDeviceGeoMapKeyBy());
		if(cacheObj == null) return null;
		return (Collection<GeoMapVTO>)cacheObj;
	}
}
