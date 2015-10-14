package com.bhu.vas.business.bucache.local.serviceimpl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.smartwork.msip.cores.cache.entitycache.Cache;
import com.smartwork.msip.cores.cache.entitycache.CacheService;
import com.smartwork.msip.cores.helper.StringHelper;

/**
 * 终端探测的缓存service
 * 1：存储设备的绑定的uid
 * 2：存储终端探测的push发送策略延迟标记
 * @author lawliet
 *
 */
@Service
public class BusinessWSCacheService {
	//记录设备对于某终端的探测上线通知时间记录
	private final String QWSPushNotifyCachePrefixKey = "QWSPNCacheKey.";
	
	@Resource(name="coreCacheService")
	CacheService cacheService;
	Cache entityCache;
	
	
	@PostConstruct
	protected void init() {
		entityCache = cacheService.addCache(this.getClass().getName(),10*10000,10*3600);//2小时
    }
	
	public String generateQWSPushNotifyCachePrefixKeyBy(String mac, String hd_mac){
		StringBuilder sb = new StringBuilder();
		sb.append(QWSPushNotifyCachePrefixKey).append(mac)
			.append(StringHelper.MINUS_STRING_GAP).append(hd_mac);
		return sb.toString();
	}
	
	public void storeQWSPushNotifyCacheResult(String mac, String hd_mac){
		String key = generateQWSPushNotifyCachePrefixKeyBy(mac, hd_mac);
		//this.entityCache.remove(key);
		this.entityCache.put(key, true ,5*60);//5分钟
	}
	
	public boolean getQWSPushNotifyCacheByQ(String mac, String hd_mac){
		Object cacheObj = this.entityCache.get(generateQWSPushNotifyCachePrefixKeyBy(mac, hd_mac));
		if(cacheObj == null) return false;
		return (Boolean)cacheObj;
	}
	
	public List<Object> getQWSPushNotifyCacheByQ(String mac, List<String> hd_macs){
		if(StringUtils.isEmpty(mac) || hd_macs == null || hd_macs.isEmpty()) return Collections.emptyList();
		List<String> keys = new ArrayList<String>();
		for(String hd_mac : hd_macs){
			keys.add(generateQWSPushNotifyCachePrefixKeyBy(mac, hd_mac));
		}
		Map<String, ?> cacheObj = this.entityCache.getMulti(keys);
		if(cacheObj == null) return null;
		List<Object> rets = new ArrayList<Object>();
		for(String key : keys){
			rets.add(cacheObj.get(key));
		}
		return rets;
	}
}
