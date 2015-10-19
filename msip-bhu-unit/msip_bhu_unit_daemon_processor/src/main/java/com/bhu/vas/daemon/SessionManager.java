package com.bhu.vas.daemon;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.bhu.vas.business.bucache.redis.serviceimpl.devices.WifiDevicePresentCtxService;
import com.smartwork.msip.cores.cache.entitycache.impl.local.DefaultCacheImpl;

public class SessionManager {
	private static class SessionCacheFacadeHolder{ 
		private static SessionManager instance =new SessionManager(); 
	}

	private SessionManager(){
		//TaskEngine.getInstance().schedule(new DaemonCheckTask(), 5*60*1000,5*60*1000);//5*60*1000,5*60*1000);//5*60*1000);
		//TaskEngine.getInstance().schedule(new DaemonCheckSerialTask(), 3*60*1000,3*60*1000);//5*60*1000,5*60*1000);//5*60*1000);
	}
	
	/**
	 * 获取工厂单例
	 * @return
	 */
	public static SessionManager getInstance() { 
		return SessionCacheFacadeHolder.instance; 
	}
	
	//wifi mac --> sessioninfo  caches
	private DefaultCacheImpl<SessionInfo> sessions = new DefaultCacheImpl<SessionInfo>(60,10);
	
	private Map<String,SerialTask> serialTaskmap = new ConcurrentHashMap<String,SerialTask>();
	public SessionInfo getSession(String wifi_mac) {
		SessionInfo ret = sessions.get(wifi_mac);
		if(ret == null){
			String present_ctx = WifiDevicePresentCtxService.getInstance().getPresent(wifi_mac);
			if(present_ctx != null){
				ret = addSession(wifi_mac, present_ctx);
				System.out.println(String.format("SessionManager 未发现【%s】状态，但 RedisPresent存在此状态【%s】 更新SessionManager成功！", wifi_mac,present_ctx));
			}
		}
		return ret;
    }
	
	public SessionInfo addSession(String wifi_mac, String ctx) {
		SessionInfo info = new SessionInfo(wifi_mac,ctx);
		sessions.put(wifi_mac, info);
		return info;
    }

	public void removeSession(String wifi_mac) {
		if(wifi_mac == null) return;
		sessions.remove(wifi_mac);
    }
	
	public void removeSessionByCtx(String ctx) {
		if(ctx == null) return;
		Iterator<String> iterator = this.sessions.keySet().iterator();
		while(iterator.hasNext()){
			String wifi_mac = iterator.next();
			SessionInfo sessioninfo = this.getSession(wifi_mac);
			if(sessioninfo != null && ctx.equals(sessioninfo.getCtx())){
				this.removeSession(wifi_mac);
			}
		}
    }
	
	
	public boolean contains(String wifi_mac){
		return sessions.containsKey(wifi_mac);
	}
	
	public boolean clear(){
		return sessions.clear();
	}
	
	public Set<String> keySet() {
		return sessions.keySet();
	}

	public Map<String, SerialTask> getSerialTaskmap() {
		return serialTaskmap;
	}

	public void setSerialTaskmap(Map<String, SerialTask> serialTaskmap) {
		this.serialTaskmap = serialTaskmap;
	}
	
	public Map<String,SessionInfo>[] sessionInfoCaches(){
		return this.sessions.getCaches();
	}
}
