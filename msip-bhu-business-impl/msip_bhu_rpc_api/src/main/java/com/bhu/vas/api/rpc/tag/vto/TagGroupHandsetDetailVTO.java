package com.bhu.vas.api.rpc.tag.vto;

import java.io.Serializable;
import java.util.Map;

@SuppressWarnings("serial")
public class TagGroupHandsetDetailVTO implements Serializable{
	private String hdMac;
	private String mobileno;
	private String manu;
	private Long count;
	private String firstTime;
	private String lastTime;
	
	public String getHdMac() {
		return hdMac;
	}
	public void setHdMac(String hdMac) {
		this.hdMac = hdMac;
	}
	public String getMobileno() {
		return mobileno;
	}
	public void setMobileno(String mobileno) {
		this.mobileno = mobileno;
	}
	public String getManu() {
		return manu;
	}
	public void setManu(String manu) {
		this.manu = manu;
	}
	public Long getCount() {
		return count;
	}
	public void setCount(Long count) {
		this.count = count;
	}
	public String getFirstTime() {
		return firstTime;
	}
	public void setFirstTime(String firstTime) {
		this.firstTime = firstTime;
	}
	public String getLastTime() {
		return lastTime;
	}
	public void setLastTime(String lastTime) {
		this.lastTime = lastTime;
	}
	
	public TagGroupHandsetDetailVTO toVto(Map<String,Object> map){
		TagGroupHandsetDetailVTO vto = new TagGroupHandsetDetailVTO();
		vto.setHdMac((String)map.get("hdmac"));
		vto.setMobileno((String)map.get("mobileno"));
		vto.setCount((Long)map.get("count"));
		vto.setFirstTime((String)map.get("min"));
		vto.setLastTime((String)map.get("max"));
		return vto;
	}
	
}
