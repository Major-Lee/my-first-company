package com.bhu.vas.daemon;

import java.util.Set;

import com.smartwork.msip.cores.cache.entitycache.impl.local.DefaultCacheImpl;
import com.smartwork.msip.cores.helper.task.TaskEngine;

public class SessionManager {
	private static class SessionCacheFacadeHolder{ 
		private static SessionManager instance =new SessionManager(); 
	}

	private SessionManager(){
		TaskEngine.getInstance().schedule(new DaemonCheckTask(), 60*1000,60*1000);//5*60*1000,5*60*1000);//5*60*1000);
	}
	
	/**
	 * 获取工厂单例
	 * @return
	 */
	public static SessionManager getInstance() { 
		return SessionCacheFacadeHolder.instance; 
	}
	
	//wifi mac --> cm  caches
	private DefaultCacheImpl<String> sessions = new DefaultCacheImpl<String>(60,10);
	
	
	public String getSession(String wifi_mac) {
		return sessions.get(wifi_mac);
    }
	
	public void addSession(String wifi_mac, String ctx) {
		sessions.put(wifi_mac, ctx);
    }

	public void removeSession(String wifi_mac) {
		if(wifi_mac == null) return;
		sessions.remove(wifi_mac);
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
}
