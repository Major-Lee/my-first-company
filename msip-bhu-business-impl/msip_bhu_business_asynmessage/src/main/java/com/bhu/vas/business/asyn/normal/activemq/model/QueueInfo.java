package com.bhu.vas.business.asyn.normal.activemq.model;

public class QueueInfo {
	private String host;
	private int port;
	private String queue;
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public String getQueue() {
		return queue;
	}
	public void setQueue(String queue) {
		this.queue = queue;
	}
	
	public String uniqueKey(){
		return String.format("tcp://%s:%s", host,port);
	}
	public String activeMqUrl(){
		return String.format("failover:(tcp://%s:%s?tcpNoDelay=true)", host,port);
	}
	
	public String toString(){
		return String.format("host[%s] port[%s] queue[%s]", host,port,queue);
	}
	
	public static QueueInfo build(String host,int port,String queue){
		QueueInfo info =  new QueueInfo();
		info.setHost(host);
		info.setPort(port);
		info.setQueue(queue);
		return info;
	}
}
