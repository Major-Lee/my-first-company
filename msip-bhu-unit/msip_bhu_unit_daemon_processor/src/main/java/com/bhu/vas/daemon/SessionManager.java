package com.bhu.vas.daemon;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.smartwork.msip.cores.cache.entitycache.impl.local.DefaultCacheImpl;
import com.smartwork.msip.cores.helper.task.TaskEngine;

public class SessionManager {
	private static class SessionCacheFacadeHolder{ 
		private static SessionManager instance =new SessionManager(); 
	}

	private SessionManager(){
		TaskEngine.getInstance().schedule(new DaemonCheckTask(), 60*1000,60*1000);//5*60*1000,5*60*1000);//5*60*1000);
		TaskEngine.getInstance().schedule(new DaemonCheckSerialTask(), 3*60*1000,3*60*1000);//5*60*1000,5*60*1000);//5*60*1000);
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
	
	private Map<String,SerialTask> serialTaskmap = new ConcurrentHashMap<String,SerialTask>();
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
	
	public void removeSessionByCtx(String ctx) {
		if(ctx == null) return;
		Iterator<String> iterator = this.sessions.keySet().iterator();
		while(iterator.hasNext()){
			String wifi_mac = iterator.next();
			String session_ctx = this.getSession(wifi_mac);
			if(ctx.equals(session_ctx)){
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
}
