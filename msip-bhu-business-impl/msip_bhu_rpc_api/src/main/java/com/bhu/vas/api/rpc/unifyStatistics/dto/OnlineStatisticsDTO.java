package com.bhu.vas.api.rpc.unifyStatistics.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

@SuppressWarnings("serial")
public class OnlineStatisticsDTO implements Serializable{
	private String name;
	private List<String> key;
	private List<Long> val;
	
	public List<String> getKey() {
		return key;
	}
	public void setKey(List<String> key) {
		this.key = key;
	}
	public List<Long> getVal() {
		return val;
	}
	public void setVal(List<Long> val) {
		this.val = val;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public int sortMaptoList(Map<String,Long> map){
		List<Map.Entry<String, Long>> list = new ArrayList<Map.Entry<String, Long>>(map.entrySet());
		Collections.sort(list,new Comparator<Map.Entry<String, Long>>() {   
		    public int compare(Map.Entry<String, Long> o1, Map.Entry<String, Long> o2) {      
		    	return (o1.getKey()).toString().compareTo(o2.getKey());
		    }
		});
		for (int i = 0; i < list.size(); i++) {
			this.getKey().add(list.get(i).getKey());
			this.getVal().add(list.get(i).getValue());
		}
		
		return 0;
	}
}
