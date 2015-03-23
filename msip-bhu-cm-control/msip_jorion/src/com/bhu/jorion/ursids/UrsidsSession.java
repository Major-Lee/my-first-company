package com.bhu.jorion.ursids;

import java.util.Iterator;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.jms.TextMessage;

import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bhu.jorion.PendingTask;

public class UrsidsSession {
    private final static Logger LOGGER = LoggerFactory.getLogger(UrsidsSession.class);
	private String id;
	private boolean joined_flag;
	IoSession session;
	private Map<String, Queue<PendingTask>> pendingTask;

	public UrsidsSession(IoSession session, String id) {
		pendingTask = new ConcurrentHashMap<String, Queue<PendingTask>>();
		this.session = session;
		this.id = id;
		this.joined_flag = false;
	}
	 
	
	public boolean getJoined_flag() {
		return joined_flag;
	}


	public void setJoined_flag(boolean join_flag) {
		this.joined_flag = join_flag;
	}


	public String getId() {
		return id;
	}


	public void setId(String id) {
		this.id = id;
	}

	public IoSession getSession() {
		return session;
	}

	public void setSession(IoSession session) {
		this.session = session;
	}


	public synchronized boolean isTaskPending(String mac){
		Queue<PendingTask> q = pendingTask.get(mac);
		if(q == null)
			return false;
		return q.isEmpty();
	}
	
	public synchronized void addTask(String mac, long taskid, TextMessage msg){
		Queue<PendingTask> q = pendingTask.get(mac);
		if(q == null){
			q = new ConcurrentLinkedQueue<PendingTask>();
			pendingTask.put(mac, q);
		}
		PendingTask task = new PendingTask(taskid, msg);
		q.add(task);
		LOGGER.debug("Queue size now:" + q.size());
	}
	
	public synchronized void removeTask(String mac, long taskid){
		Queue<PendingTask> q = pendingTask.get(mac);
		if(q == null){
			return;
		}
		PendingTask t = q.peek();
		if(t != null){
			if(t.getTaskid() == taskid){
				if(t.getStatus() == PendingTask.STATUS_PENDING){
					LOGGER.error("remove task error, message has not been sent, mac:" + mac + " taskid:" + taskid);
					return;
				}
				q.remove();
				LOGGER.error("mac:" + mac + "  taskid:" + taskid + " removed");
			} else {
				LOGGER.error("mac:" + mac + "  taskid:" + taskid + "doesn't match the head of queue");
			}
		}
		if(q.isEmpty()){
			q.clear();
			pendingTask.remove(mac);
		}
	}
	
	public synchronized PendingTask getNextTask(String mac){
		Queue<PendingTask> q = pendingTask.get(mac);
		if(q != null && !q.isEmpty()){
			PendingTask t = q.peek();
			LOGGER.debug("now size:" + q.size());
			return t;
		}
		return null;
	}
	
	public synchronized void removeDevice(String mac){
		LOGGER.debug("remove queue data for mac:" + mac);
		Queue<PendingTask> q = pendingTask.get(mac);
		if(q == null)
			return;
		q.clear();
		pendingTask.remove(mac);
	}
	
	public synchronized void clearAllTasks(){
		LOGGER.debug("Clear all devs's queue on ursids:" + this.getId());
		Iterator<String> it = pendingTask.keySet().iterator();
		while(it.hasNext()){
			Queue<PendingTask> q = pendingTask.get(it.next());
			q.clear();
		}
		pendingTask.clear();
	}
}
