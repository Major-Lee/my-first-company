package com.bhu.vas.business.asyn.spring.builder.async;

import java.util.HashMap;
import java.util.Map;

public enum AsyncMessageType {
		BatchImportConfirm("出库库房设备导入","shipment import","BICF"),
		BatchSharedealModify("批量更新设备分成","batch sharedeal modify","BSMF"),
		
		BatchDeviceOperTag("设备批量绑定标签", "batch device operation tag", "BDOT"),
		BatchGroupDownCmds("分组批量下发指令","batch Group down cmds","BGDC"),
		BatchGroupDeviceSnkApply("用户群组设备共享网络应用","batch group device sharednetwork apply","BGSN"),
		BatchDeviceSnkApply("用户设备共享网络应用","batch device sharednetwork apply","BDSN"),
		BatchDeviceSnkClear("用户设备共享网络模板清除","batch device sharednetwork clear","BDSC"),
		//BatchDeviceBindTag("设备批量绑定标签", "batch device bind tag", "BDBT"),
		//BatchDeviceDelTag("设备批量删除标签", "batch device del tag", "BDDT"),
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
