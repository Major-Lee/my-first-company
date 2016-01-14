package com.bhu.vas.api.dto;

import java.util.HashMap;
import java.util.Map;

public enum UserType {
	Normal(1,"N","普通用户"),
	AgentNormal(10,"A","分销商用户"),
	AgentFinance(15,"F","财务用户"),
	AgentWarehouseManager(20,"W","仓储用户"),
	AgentSellor(30,"S","销售用户"),
	
	//商业 wifi 小型运营商客户
	BusinessNormal(40,"BN","商业 wifi 小型运营商客户"),
	//自运营的商户
	BusinessSelfor(41,"BS","自运营的商户"),
	;
	//private String index;
	private int index;
	//short name
	private String sname;
	//full name
	private String fname;
	
	private static Map<String, UserType> 	allUserTypesWithSName;
	private static Map<Integer, UserType> 	allUserTypesWithIndex;
	
	UserType(int index,String sname,String fname){
		this.index = index;
		this.sname = sname;
		this.fname = fname;
	}
		
	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public String getSname() {
		return sname;
	}

	public void setSname(String sname) {
		this.sname = sname;
	}

	public String getFname() {
		return fname;
	}

	public void setFname(String fname) {
		this.fname = fname;
	}

	public static UserType getBySName(String sname) {
		UserType utype = allUserTypesWithSName.get(sname);
		if(utype != null)
			return utype;
		else
			return UserType.Normal;
	}	
	
	public static UserType getByIndex(int index) {
		UserType utype = allUserTypesWithIndex.get(index);
		if(utype != null)
			return utype;
		else
			return UserType.Normal;
	}
	
	static {
		allUserTypesWithSName = new HashMap<String,UserType>();
		allUserTypesWithIndex = new HashMap<Integer,UserType>();
		UserType[] types = values();
		for (UserType type : types){
			allUserTypesWithSName.put(type.sname, type);
			allUserTypesWithIndex.put(type.index, type);
		}
			
	}
	
		
}
