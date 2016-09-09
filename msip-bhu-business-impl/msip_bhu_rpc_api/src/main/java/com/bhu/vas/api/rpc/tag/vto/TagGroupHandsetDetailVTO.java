package com.bhu.vas.api.rpc.tag.vto;

import java.io.Serializable;


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
		if(mobileno != null){
			StringBuilder sb = new StringBuilder(mobileno);
			sb.replace(6,10, "****");
			this.mobileno = sb.toString();
		}
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
	
	public boolean isFilter(int count,String mobileno){
		
		boolean flag = false;
		
		if(mobileno != null){
			if(!mobileno.equals(this.mobileno) && this.count < count){
				flag = true;
			}
		}else{
			if(this.count < count){
				flag = true;
			}
		}
		
		return flag;
	}
}
