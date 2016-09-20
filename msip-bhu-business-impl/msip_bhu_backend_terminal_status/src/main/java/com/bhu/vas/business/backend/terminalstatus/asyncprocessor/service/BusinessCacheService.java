package com.bhu.vas.business.backend.terminalstatus.asyncprocessor.service;

import java.util.Date;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.alisoft.xplatform.asf.cache.memcached.client.MemCachedClient;
import com.alisoft.xplatform.asf.cache.memcached.client.SockIOPool;
import com.smartwork.msip.cores.cache.entitycache.Cache;
import com.smartwork.msip.cores.cache.entitycache.CacheService;

/**
 * 依赖系统配置的缓存实现
 * @author Edmond
 *
 */
@Service
public class BusinessCacheService {
	
	//记录设备对于某终端的上线通知时间记录
	private final String PORTRAITCachePrefixKey = "PORTRAITCacheKey.";
	
	@Resource(name="coreCacheService")
	CacheService cacheService;
	Cache entityCache;
	
	
	@PostConstruct
	protected void init() {
		entityCache = cacheService.addCache(this.getClass().getName(),10*10000,2*3600);//2小时
    }
	
	
//	public String generateAgentDSCacheKeyBy(int agentuser){
//		StringBuilder sb = new StringBuilder();
//		sb.append(UPortraitCachePrefixKey).append(agentuser);
//		return sb.toString();
//	}
//	
//	public void storeAgentDSCacheResult(int agentuser,AgentDeviceStatisticsVTO result){
//		String key = generateAgentDSCacheKeyBy(agentuser);
//		this.entityCache.remove(key);
//		this.entityCache.put(key, result,1*3600);//1小时
//	}
	
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
	
	
//	public AgentDeviceStatisticsVTO getAgentDSCacheByUser(int agentuser){
//		Object cacheObj = this.entityCache.get(generateAgentDSCacheKeyBy(agentuser));
//		if(cacheObj == null) return null;
//		return AgentDeviceStatisticsVTO.class.cast(cacheObj);
//	}
//	
//	public String generateQTerminalPushNotifyCachePrefixKeyBy(String mac, String hd_mac){
//		StringBuilder sb = new StringBuilder();
//		sb.append(UPortraitCachePrefixKey).append(mac)
//			.append(StringHelper.MINUS_STRING_GAP).append(hd_mac);
//		return sb.toString();
//	}
//	
//	public void storeQTerminalPushNotifyCacheResult(String mac, String hd_mac){
//		String key = generateQTerminalPushNotifyCachePrefixKeyBy(mac, hd_mac);
//		//this.entityCache.remove(key);
//		//this.entityCache.put(key, true ,15*60);//15分钟
//		this.entityCache.put(key, true , BusinessRuntimeConfiguration.Terminal_Push_Notify_Exprie_Second);
//	}
//	
//	public boolean getQTerminalPushNotifyCacheByQ(String mac, String hd_mac){
//		Object cacheObj = this.entityCache.get(generateQTerminalPushNotifyCachePrefixKeyBy(mac, hd_mac));
//		if(cacheObj == null) return false;
//		return (Boolean)cacheObj;
//	}
	
		public static void main(String[] args) {
	
			/*MemCachedClientHelper c = new MemCachedClientHelper();
			//存取一个简单的Integer
			// Store a value (async) for one hour
			MemCachedClient cacheClient = new MemCachedClient();
			cacheClient.set("someKey", 3600, new Integer(4));
			c.setCacheClient(cacheClient);
	
			MemCachedClient getcacheClient = c.getCacheClient("someKey");
			// Retrieve a value (synchronously).
	
			Object myObject = getcacheClient.get("someKey");
	
			Integer result = (Integer) myObject;
	
			System.out.println(result);*/
			
			MemCachedClient client=new MemCachedClient();  
	        String [] addr ={"192.168.66.7:11211"};  
	        Integer [] weights = {3};  
	        SockIOPool pool = SockIOPool.getInstance();  
	        pool.setServers(addr);  
	        pool.setWeights(weights);  
	        pool.setInitConn(5);  
	        pool.setMinConn(5);  
	        pool.setMaxConn(200);  
	        pool.setMaxIdle(1000*30*30);  
	        pool.setMaintSleep(30);  
	        pool.setNagle(false);  
	        pool.setSocketTO(30);  
	        pool.setSocketConnectTO(0);  
	        pool.initialize();  
	          
//	      String [] s  =pool.getServers();  
	        client.setCompressEnable(true);  
	        client.setCompressThreshold(1000*1024);  
	          
//	      将数据放入缓存  
	        client.set("test2","test2");  
//	      将数据放入缓存,并设置失效时间  
	        Date date=new Date(2000000);  
	        client.set("test1","test1", date);  
	          
//	      删除缓存数据  
//	      client.delete("test1");  
	          
//	      获取缓存数据  
	        String str =(String)client.get("test1");  
	        System.out.println(str);  
	
	
	
	}
}
