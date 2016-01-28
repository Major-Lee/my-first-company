package com.bhu.vas.api.helper;

import java.util.HashMap;
import java.util.Map;

import org.springframework.util.StringUtils;

public class WifiDeviceDocumentEnumType {
	
	public enum OnlineEnum{
		Online("1","在线"),
		Offline("0","上线后离线"),
		NeverOnline("-1","从未上线");
		
		String type;
		String name;
		
		OnlineEnum(String type, String name){
			this.type = type;
			this.name = name;
		}
		static Map<String, OnlineEnum> allOperationOnline;
		
		static {
			allOperationOnline = new HashMap<String,OnlineEnum>();
			OnlineEnum[] types = values();
			for (OnlineEnum type : types)
				allOperationOnline.put(type.getType(), type);
		}

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
		
		public static OnlineEnum getOnlineEnumFromType(String type) {
			if(StringUtils.isEmpty(type)) return null;
			return allOperationOnline.get(type);
		}
	}

	public enum MOnlineEnum{
		MOnline("1","模块在线"),
		MOffline("0","模块上线后离线"),
		MNeverOnline("-1","模块从未上线");
		
		String type;
		String name;
		
		MOnlineEnum(String type, String name){
			this.type = type;
			this.name = name;
		}
		static Map<String, MOnlineEnum> allOperationMOnline;
		
		static {
			allOperationMOnline = new HashMap<String,MOnlineEnum>();
			MOnlineEnum[] types = values();
			for (MOnlineEnum type : types)
				allOperationMOnline.put(type.getType(), type);
		}

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
		
		public static MOnlineEnum getMOnlineEnumFromType(String type) {
			return allOperationMOnline.get(type);
		}
	}
	
	public enum UBindedEnum{
		UBinded("1", "已绑定"),
		UNOBinded("0", "未绑定");
		
		String type;
		String name;
		
		UBindedEnum(String type, String name){
			this.type = type;
			this.name = name;
		}
		static Map<String, UBindedEnum> allOperationUBinded;
		
		static {
			allOperationUBinded = new HashMap<String,UBindedEnum>();
			UBindedEnum[] types = values();
			for (UBindedEnum type : types)
				allOperationUBinded.put(type.getType(), type);
		}

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
		
		public static UBindedEnum getUBindedEnumFromType(String type) {
			return allOperationUBinded.get(type);
		}
	}
	
	public enum OperateEnum{
		Operate("1", "可运营"),
		NOOperate("0", "不可运营");
		
		String type;
		String name;
		
		OperateEnum(String type, String name){
			this.type = type;
			this.name = name;
		}
		static Map<String, OperateEnum> allOperationOperate;
		
		static {
			allOperationOperate = new HashMap<String,OperateEnum>();
			OperateEnum[] types = values();
			for (OperateEnum type : types)
				allOperationOperate.put(type.getType(), type);
		}

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
		
		public static OperateEnum getOperateEnumFromType(String type) {
			return allOperationOperate.get(type);
		}
	}
}
