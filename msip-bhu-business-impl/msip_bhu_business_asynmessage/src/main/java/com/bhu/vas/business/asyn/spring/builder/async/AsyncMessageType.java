package com.bhu.vas.business.asyn.spring.builder.async;

import java.util.HashMap;
import java.util.Map;

public enum AsyncMessageType {
		BatchImportConfirm("出库库房设备导入","shipment import","BICF"),
		BatchSharedealModify("批量更新设备分成","batch sharedeal modify","BSMF"),
		;
		static Map<String, AsyncMessageType> allAsyncMessageTypes;
		public final static int prefix_length = 4;
		private String cname;
		private String name;
		private String prefix;
		private AsyncMessageType(String cname,String name, String prefix){
			this.cname = cname;
			this.name = name;
			this.prefix = prefix;
		}
		
		public String getCname() {
			return cname;
		}
		public void setCname(String cname) {
			this.cname = cname;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getPrefix() {
			return prefix;
		}
		public void setPrefix(String prefix) {
			this.prefix = prefix;
		}
		public static AsyncMessageType fromPrefix(String prefix){
			return allAsyncMessageTypes.get(prefix);
		}
		static {
			allAsyncMessageTypes = new HashMap<String,AsyncMessageType>();
			AsyncMessageType[] types = values();//new ImageType[] {JPG, BMP, GIF, PNG, TIFF};
			for (AsyncMessageType type : types){
				allAsyncMessageTypes.put(type.getPrefix(), type);
			}
		}
}
