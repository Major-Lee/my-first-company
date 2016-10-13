package com.bhu.vas.api.dto;

public enum DistributorType {
	//管理员账户 系统创建
	Channel("channel"),
	City("city")
	;
	
	private String type;
	
	public String getType() {
		return type;
	}


	public void setType(String type) {
		this.type = type;
	}


	DistributorType(String val){
		type = val;
	}
	
	
	public static boolean isValidType(String type){
		if(Channel.equals(type) || City.equals(type))
			return true;
		return false;
	}
}
