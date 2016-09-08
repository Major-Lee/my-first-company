package com.bhu.vas.api.rpc.tag.vto;

import java.io.Serializable;
import java.util.Map;

public class GroupConnCountVTO implements Serializable{
	private Map<String,String> today;
	private Map<String,String> yesterday;
	public Map<String, String> getToday() {
		return today;
	}
	public void setToday(Map<String, String> today) {
		this.today = today;
	}
	public Map<String, String> getYesterday() {
		return yesterday;
	}
	public void setYesterday(Map<String, String> yesterday) {
		this.yesterday = yesterday;
	}
	
	
}
