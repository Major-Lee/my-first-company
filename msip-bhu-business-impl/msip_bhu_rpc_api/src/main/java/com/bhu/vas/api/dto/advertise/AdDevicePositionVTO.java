package com.bhu.vas.api.dto.advertise;

import java.util.List;

@SuppressWarnings("serial")
public class AdDevicePositionVTO implements java.io.Serializable{
	private List<String> list; 
	private long count = 0L;
	
	public List<String> getList() {
		return list;
	}
	public void setList(List<String> list) {
		this.list = list;
	}
	public long getCount() {
		return count;
	}
	public void setCount(long count) {
		this.count = count;
	}
	
	public static boolean isFilter(String str){
		String reg = "^[a-zA-Z]+$";
		return str.trim().substring(0, 1).matches(reg);
	}
}
