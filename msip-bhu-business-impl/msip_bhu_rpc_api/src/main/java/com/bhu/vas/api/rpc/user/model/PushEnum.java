package com.bhu.vas.api.rpc.user.model;

import com.smartwork.msip.cores.helper.StringHelper;

/**
 * 用于ios push 发送的证书类型
 * @author lawliet
 *
 */
public enum PushEnum {
	
	Store("S"),//商店版
	Production("P"),//企业版
	;
	String type;
	PushEnum(String type){
		this.type = type;
	}
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
	public static boolean isStore(String type){
		if(StringHelper.isEmpty(type)) return true;
		if(type.equals(Store.getType())) return true;
		return false;
	}
}
