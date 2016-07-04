package com.bhu.vas.api.dto.search.increment;

import java.util.HashMap;
import java.util.Map;

public enum IncrementActionEnum{
	WD_FullCreate("WD_FCT","设备索引完整创建数据"),
	;
	static Map<String, IncrementActionEnum> incrementActions;
	String key;
	String desc;
	
	IncrementActionEnum(String key,String desc){
		this.key = key;
		this.desc = desc;
	}

	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}
	static {
		incrementActions = new HashMap<String,IncrementActionEnum>();
		IncrementActionEnum[] types = values();
		for (IncrementActionEnum type : types)
			incrementActions.put(type.getKey(), type);
	}
	
	public static IncrementActionEnum getIncrementActionFromKey(String key) {
		return incrementActions.get(key);
	}
}
