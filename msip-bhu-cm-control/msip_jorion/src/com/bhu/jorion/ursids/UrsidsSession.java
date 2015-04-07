package com.bhu.jorion.ursids;

import java.util.Iterator;
import java.util.Map;
import java.util.Queue;
import java.util.UUID;
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
	private String name;
	private boolean joinedFlag;
	IoSession session;
	private Map<String, Queue<PendingTask>> pendingTask;
	private long devCount;
	private String balanceUrl;
	private String redirectUrl;
	private long maxClient;
	private long reservedConection;
	private UUID sessionId;
	
	public UrsidsSession(IoSession session, String name, String id) {
		pendingTask = new ConcurrentHashMap<String, Queue<PendingTask>>();
		this.session = session;
		this.id = id;
		this.joinedFlag = false;
		this.devCount = 0;
		this.balanceUrl = "";
		this.name = name;
		this.redirectUrl = null;
		this.maxClient = 0;
		this.reservedConection = 0;
	}
	 
	
	public long getReservedConection() {
		return reservedConection;
	}


	public void setReservedConection(long reservedConection) {
		this.reservedConection = reservedConection;
	}


	public String getRedirectUrl() {
		return redirectUrl;
	}


	public void setRedirectUrl(String redirectUrl) {
		this.redirectUrl = redirectUrl;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public long getMaxClient() {
		return maxClient;
	}


	public void setMaxClient(long maxClient) {
		this.maxClient = maxClient;
	}


	public String getBalanceUrl() {
		return balanceUrl;
	}

	public void setBalanceUrl(String url) {
		this.balanceUrl = url;
	}
	
	public synchronized long getDevCount() {
		return devCount;
	}


	public synchronized void setDevCount(long devCount) {
		this.devCount = devCount;
	}

	public boolean getJoinedFlag() {
		return joinedFlag;
	}

	public void setJoinedFlag(boolean join_flag) {
		this.joinedFlag = join_flag;
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
	
	public UUID getSessionId() {
		return sessionId;
	}


	public void setSessionId(UUID sessionId) {
		this.sessionId = sessionId;
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
				LOGGER.debug("mac:" + mac + "  taskid:" + taskid + " removed");
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
	
	public synchronized void clearDeviceTasks(String mac){
		LOGGER.debug("remove queue data for mac:" + mac);
		Queue<PendingTask> q = pendingTask.get(mac);
		if(q == null)
			return;
		q.clear();
		pendingTask.remove(mac);
	}
	
	public synchronized void clearAllDevs(){
		LOGGER.debug("Clear all devs's queue on ursids:" + this.getId());
		Iterator<String> it = pendingTask.keySet().iterator();
		while(it.hasNext()){
			Queue<PendingTask> q = pendingTask.get(it.next());
			q.clear();
		}
		pendingTask.clear();
		devCount = 0;
	}
	
	public synchronized Long getTaskDetail(StringBuffer sb){
		Long blockedDev = Long.valueOf(0);
		Iterator<String> it = pendingTask.keySet().iterator();
		while(it.hasNext()){
			String mac = it.next();
			Queue<PendingTask> q = pendingTask.get(mac);
			sb.append(mac);
			sb.append("  count:" + q.size());
			if(!q.isEmpty()){
				PendingTask t = q.peek();
				sb.append(" first taskid:" + t.getTaskid());
			}
			sb.append("\n");
			blockedDev ++;
		}
		return blockedDev;
	}
}
