package com.bhu.vas.rpc.asyncprocessor.dto;

import java.util.HashMap;
import java.util.Map;

public enum AsyncIndexMessageType {
	WifiDevice_BlukFullIndex("设备全量覆盖索引","设备全量覆盖索引","WFBFI"),
	;
	
	static Map<String, AsyncIndexMessageType> allActionMessageTypes;
	private String name;
	private String desc;
	private String type;
	private AsyncIndexMessageType(String name, String desc, String type){
		this.name = name;
		this.desc = desc;
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public static AsyncIndexMessageType fromPrefix(String prefix){
		return allActionMessageTypes.get(prefix);
	}
	
	static {
		allActionMessageTypes = new HashMap<String,AsyncIndexMessageType>();
		AsyncIndexMessageType[] types = values();//new ImageType[] {JPG, BMP, GIF, PNG, TIFF};
		for (AsyncIndexMessageType type : types){
			allActionMessageTypes.put(type.getType(), type);
		}
	}
}
