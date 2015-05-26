package com.bhu.vas.api.rpc.user.model;

import com.smartwork.msip.cores.helper.StringHelper;

/**
 * 用于ios push 发送的证书类型
 * @author lawliet
 *
 */
public enum PushTypeEnum {
	
	Official("O"),//正式版
	Development("D"),//开发版
	;
	String type;
	PushTypeEnum(String type){
		this.type = type;
	}
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
	public static boolean isOfficial(String type){
		if(StringHelper.isEmpty(type)) return true;
		if(type.equals(Official.getType())) return true;
		return false;
	}
}
