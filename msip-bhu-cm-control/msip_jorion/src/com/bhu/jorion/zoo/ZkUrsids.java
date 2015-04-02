package com.bhu.jorion.zoo;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ZkUrsids implements Serializable{
	private String jorionId;
	private String id;
	private Long lastReportCount;
	private String balanceUrl;
	private Long maxClient;
	private Long reservedConnection;
	private Map<String, Long> devCountMap;
	private Long currentClient;
	
	public ZkUrsids(){
		jorionId = "";
		id = "";
		devCountMap = new HashMap<String, Long>();
		lastReportCount = Long.valueOf(0);;
		balanceUrl = "";
		maxClient = Long.valueOf(0);
		currentClient = Long.valueOf(0);
		reservedConnection = Long.valueOf(0);
	}
	
	public ZkUrsids(String jorionId, String id, String wanUrl, Long maxClient, Long reservedConnection){
		this.jorionId = jorionId;
		this.id = id;
		this.balanceUrl = wanUrl;
		this.devCountMap = new HashMap<String, Long>();
		this.lastReportCount = Long.valueOf(-1);
		this.maxClient = maxClient;
		this.currentClient = Long.valueOf(0);
		this.reservedConnection = reservedConnection;
	}
	
	public Long getCurrentClient() {
		return currentClient;
	}

	public int getFreeConnections(){
		return (int)((maxClient - reservedConnection) * devCountMap.size() - currentClient);
	}
	
	public float getConnectedRate(){
		if(maxClient == 0)
			return 0;
		return ((float)currentClient)/(maxClient * devCountMap.size());
	}
	
	public Long getReservedConnection() {
		return reservedConnection;
	}

	public void setReservedConnection(Long reservedConnection) {
		this.reservedConnection = reservedConnection;
	}

	public Long getMaxClient() {
		return maxClient;
	}

	public void setMaxClient(Long maxClient) {
		this.maxClient = maxClient;
	}

	public long getLastReportCount() {
		return lastReportCount;
	}
	public void setLastReportCount(long lastReportCount) {
		this.lastReportCount = lastReportCount;
	}
	public String getJorionId() {
		return jorionId;
	}
	public void setJorionId(String jorionId) {
		this.jorionId = jorionId;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getBalanceUrl() {
		return balanceUrl;
	}
	public void setBalanceUrl(String wanUrl) {
		this.balanceUrl = wanUrl;
	}

	private Long getAllDevCount(){
		Long total = Long.valueOf(0);
		Iterator<String> it = this.devCountMap.keySet().iterator();
		while(it.hasNext()){
			total += devCountMap.get(it.next());
		}
		return total;
	}
	
	public void setDevCountOfProcess(String number, Long devCount) {
		devCountMap.put(number, devCount);
		currentClient = this.getAllDevCount();
	}
	
	public String toString(){
		StringBuffer sb = new StringBuffer();
		sb.append("Urisds[" + this.getId() + "]"); 
		sb.append(" connected with[" + this.getJorionId() + "]");
		sb.append(" Porcess:[" + devCountMap.size() + "]");
		sb.append(" maxClient[" + this.getMaxClient() + "]");
		sb.append(" reserved[" + this.getReservedConnection() + "]");
		sb.append(" clients[" + this.getCurrentClient() + "]");
		sb.append(" free[" + this.getFreeConnections() + "]");
		sb.append(" rate[" + this.getConnectedRate() + "]");
		sb.append(" url[" + this.getBalanceUrl() + "]");
		return sb.toString();
	}
}
