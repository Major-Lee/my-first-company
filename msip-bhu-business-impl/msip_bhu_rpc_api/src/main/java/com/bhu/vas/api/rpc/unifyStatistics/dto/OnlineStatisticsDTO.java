package com.bhu.vas.api.rpc.unifyStatistics.dto;

import java.io.Serializable;
import java.util.Map;

<<<<<<< Updated upstream
@SuppressWarnings("serial")
public class OnlineStatisticsDTO implements Serializable{
=======
public class OnlineStatisticsDTO implements java.io.Serializable{
>>>>>>> Stashed changes
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
