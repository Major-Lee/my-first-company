package com.smartwork.multiplexer;

import java.util.Iterator;

import com.smartwork.im.net.Session;
import com.smartwork.multiplexer.spi.cache.SessionCacheImpl;


public class SessionManager {
	
	private SessionCacheImpl sessions = new SessionCacheImpl(20);
	
	private static class SessionCacheFacadeHolder{ 
		private static SessionManager instance =new SessionManager(); 
	}
	private SessionManager(){
	}
	
	/**
	 * 获取工厂单例
	 * @return
	 */
	public static SessionManager getInstance() { 
		return SessionCacheFacadeHolder.instance; 
	}
	
	
	public Session getSession(String streamID) {
		return sessions.get(streamID);
    }
	
	public void addSession(String streamID, Session session) {
		sessions.put(streamID, session);
    }

	public void removeSession(String streamID) {
		if(streamID == null) return;
		sessions.remove(streamID);
    }
	
	public boolean containsUser(String user){
		return sessions.containsKey(user);
	}
	
	public void broadcast(String message) {
        synchronized (sessions) {
        	Iterator<String> iter = sessions.keySet().iterator();
        	while(iter.hasNext()){
        		String key = iter.next();
        		Session session = sessions.get(key);
        		session.deliver(message);
        		//session.deliver("BROADCAST OK " + message);
        	}
        	/*Iterator<Entry<String, Session>> iter = sessions..entrySet().iterator();
        	while(iter.hasNext()){
        		Entry<String, Session> next = iter.next();
        		Session session = next.getValue();
        		session.deliver("BROADCAST OK " + message);
        	}*/
        }
    }
	
	public void closeAll() {
        for (Session session : sessions.values()) {
            session.close(true);
        }
    }
	
	public String sessionsStat(boolean printDetail){
		return sessions.sessionsStat(printDetail);
	}
	
	
	
/*	private List<String> sequenceIDs = new ArrayList<String>();//{"0","1","2","3","4","5","6","7","8","9"};
	
	private void init(){
		for(int i=0;i<100;i++){
			sequenceIDs.add(String.valueOf(i));
		}
	}
	
	public synchronized String assignSessionSequenceID(String streamID){
		List<Session> list = sessions.get(streamID);
		if(list == null){
			return RandomPicker.pick(sequenceIDs);
		}else{
			List<String> tmpSequenceIDs = new ArrayList<String>(sequenceIDs);
			for(Session session:list){
				tmpSequenceIDs.remove(session.getSequenceID());
			}
			return RandomPicker.pick(tmpSequenceIDs);
		}
	}
	
    
    
    
    public boolean containsUser(String user){
    	return locates.get(user)!=null;
    }
    
	public String getUserLocate(String user) {
		String locate = locates.get(user);
		return locate;
    }
	
	public void addUserLocate(String user,String streamID) {
		locates.put(user, streamID);
    }
	
	public void removeUserLocate(String user){
		locates.remove(user);
	}

	public SessionCacheImpl getSessions() {
		return sessions;
	}

	public UserLocateCacheImpl getLocates() {
		return locates;
	}*/
			
    
}
