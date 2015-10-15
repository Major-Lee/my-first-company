package com.bhu.vas.business.asyn.normal.activemq.xml;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)  
@XmlType(propOrder = {  
  "host",  
  "port",
  "url",
  "queues"
})
public class XmlServer {
	@XmlAttribute
	private String host;
	@XmlAttribute
	private int port;
	@XmlAttribute
	private String url;
	private List<String> queues;
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public List<String> getQueues() {
		return queues;
	}
	public void setQueues(List<String> queues) {
		this.queues = queues;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	
	
	public boolean addQueue(String queue){
		boolean ret  = false;
		if(queues == null){
			queues = new ArrayList<String>();
			queues.add(queue);
			ret  = true;
		}else{
			if(!queues.contains(queue)){
				queues.add(queue);
				ret  = true;
			}
		}
		return ret;
	}
	public String uniqueKey(){
		return String.format("tcp://%s:%s", host,port);
	}
	public String activeMqUrl(){
		return String.format("failover:(tcp://%s:%s?tcpNoDelay=true)", host,port);
	}
}
