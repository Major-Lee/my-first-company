package com.bhu.vas.business.bucache.local.serviceimpl;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.smartwork.msip.cores.cache.entitycache.Cache;
import com.smartwork.msip.cores.cache.entitycache.CacheService;
import com.smartwork.msip.cores.helper.StringHelper;

/**
 * 依赖系统配置的缓存实现
 * @author Edmond
 *
 */
@Service
public class BusinessCacheService {
	private final String QMediaCachePrefixKey = "QMediaCacheKey.";
	//记录设备对于某终端的上线通知时间记录
	private final String QTerminalPushNotifyCachePrefixKey = "QTPNCacheKey.";
	
	@Resource(name="coreCacheService")
	CacheService cacheService;
	Cache entityCache;
	
	
	@PostConstruct
	protected void init() {
		entityCache = cacheService.addCache(this.getClass().getName(),10*10000,2*3600);//2小时
    }
	
	
	public String generateQMediaCacheKeyBy(int qhashcode){
		StringBuilder sb = new StringBuilder();
		sb.append(QMediaCachePrefixKey).append(qhashcode);
		return sb.toString();
	}
	
	public void storeQMediaCacheResult(int qhashcode,List<Object> result){
		String key = generateQMediaCacheKeyBy(qhashcode);
		this.entityCache.remove(key);
		//if(result.size() >200){
		this.entityCache.put(key, result,10*3600);//10小时
		//}else
		//	this.entityCache.put(key, result);
	}
	
	@SuppressWarnings("unchecked")
	public List<Object> getMediaCacheByQ(int qhashcode){
		Object cacheObj = this.entityCache.get(generateQMediaCacheKeyBy(qhashcode));
		if(cacheObj == null) return null;
		return (List<Object>)cacheObj;
	}
	
	
	public String generateQTerminalPushNotifyCachePrefixKeyBy(String mac, String hd_mac){
		StringBuilder sb = new StringBuilder();
		sb.append(QTerminalPushNotifyCachePrefixKey).append(mac)
			.append(StringHelper.MINUS_STRING_GAP).append(hd_mac);
		return sb.toString();
	}
	
	public void storeQTerminalPushNotifyCacheResult(String mac, String hd_mac){
		String key = generateQTerminalPushNotifyCachePrefixKeyBy(mac, hd_mac);
		//this.entityCache.remove(key);
		this.entityCache.put(key, true ,5*60);//5分钟
	}
	
	public boolean getQTerminalPushNotifyCacheByQ(String mac, String hd_mac){
		Object cacheObj = this.entityCache.get(generateQTerminalPushNotifyCachePrefixKeyBy(mac, hd_mac));
		if(cacheObj == null) return false;
		return (Boolean)cacheObj;
	}
}
