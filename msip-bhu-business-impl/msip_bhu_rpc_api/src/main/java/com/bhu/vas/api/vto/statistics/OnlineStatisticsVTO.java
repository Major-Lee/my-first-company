package com.bhu.vas.api.vto.statistics;

import java.io.Serializable;
import java.util.Map;

@SuppressWarnings("serial")
public class OnlineStatisticsVTO implements Serializable{
	private String name;
	private Map<String,Long> map;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Map<String, Long> getMap() {
		return map;
	}
	public void setMap(Map<String, Long> map) {
		this.map = map;
	}		
	
}
