package com.bhu.vas.business.bucache.local.serviceimpl;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.bhu.vas.api.rpc.agent.vto.AgentDeviceStatisticsVTO;
import com.smartwork.msip.business.runtimeconf.BusinessRuntimeConfiguration;
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
	//记录代理商设备状态缓存
	private final String QAgentDevicesStatisticsCachePrefixKey = "QAgentDSKey.";
	
	//记录设备对于某终端的上线通知时间记录
	private final String QTerminalPushNotifyCachePrefixKey = "QTPNCacheKey.";
	
	@Resource(name="coreCacheService")
	CacheService cacheService;
	Cache entityCache;
	
	
	@PostConstruct
	protected void init() {
		entityCache = cacheService.addCache(this.getClass().getName(),10*10000,2*3600);//2小时
    }
	
	
	public String generateAgentDSCacheKeyBy(int agentuser){
		StringBuilder sb = new StringBuilder();
		sb.append(QAgentDevicesStatisticsCachePrefixKey).append(agentuser);
		return sb.toString();
	}
	
	public void storeAgentDSCacheResult(int agentuser,AgentDeviceStatisticsVTO result){
		String key = generateAgentDSCacheKeyBy(agentuser);
		this.entityCache.remove(key);
		this.entityCache.put(key, result,1*3600);//1小时
	}
	
	public AgentDeviceStatisticsVTO getAgentDSCacheByUser(int agentuser){
		Object cacheObj = this.entityCache.get(generateAgentDSCacheKeyBy(agentuser));
		if(cacheObj == null) return null;
		return AgentDeviceStatisticsVTO.class.cast(cacheObj);
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
		//this.entityCache.put(key, true ,15*60);//15分钟
		this.entityCache.put(key, true , BusinessRuntimeConfiguration.Terminal_Push_Notify_Exprie_Second);
	}
	
	public boolean getQTerminalPushNotifyCacheByQ(String mac, String hd_mac){
		Object cacheObj = this.entityCache.get(generateQTerminalPushNotifyCachePrefixKeyBy(mac, hd_mac));
		if(cacheObj == null) return false;
		return (Boolean)cacheObj;
	}
}
