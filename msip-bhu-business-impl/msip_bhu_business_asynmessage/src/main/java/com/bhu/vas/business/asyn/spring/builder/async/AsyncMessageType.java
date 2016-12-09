package com.bhu.vas.business.asyn.spring.builder.async;

import java.util.HashMap;
import java.util.Map;

public enum AsyncMessageType {
		BatchImportPreCheck("出库库房设备导入预查","shipment import pre-check","BIPC"),
		BatchImportConfirm("出库库房设备导入","shipment import","BICF"),
		BatchSharedealModify("批量更新设备分成","batch sharedeal modify","BSMF"),
		BatchSharedealModifyBySn("根据序列号批量更新设备分成","batch sharedeal modify by sn","BSSN"),
//		
		BatchDeviceOperTag("设备批量绑定标签", "batch device operation tag", "BDOT"),
		BatchGroupDownCmds("分组批量下发指令","batch Group down cmds","BGDC"),
		BatchGroupDeviceSnkApply("用户群组设备共享网络应用","batch group device sharednetwork apply","BGSN"),
//		BatchGroupModifyDeviceSnk("用户群组设备共享网络修改","batch group device sharednetwork modify","BGMS"),
		BatchDeviceSnkApply("用户设备共享网络应用","batch device sharednetwork apply","BDSN"),
		BatchDeviceSnkClear("用户设备共享网络模板清除","batch device sharednetwork clear","BDSC"),
//		BatchModifyDeviceSnk("修改共享网络参数","batch modify device sharednetwork param","BMSK"),
		BatchGroupSendSortMessage("分组批量发送短信","batch group send sort message","BGSM"),
		BatchUserIdentityRepair("用户身份信息修复","batch user identity repair","BUID"),
		BatchDeviceApplyAdvertise("设备批量应用广告", "batch device apply advertise", "BDAD"),
		BatchUpdateDeviceIndustry("设备批量更新行业信息", "batch update device industry", "BUDI"),
		//BatchDeviceBindTag("设备批量绑定标签", "batch device bind tag", "BDBT"),
		//BatchDeviceDelTag("设备批量删除标签", "batch device del tag", "BDDT"),
		BatchTimUserRegister("Tim用户注册","batch tim user register","BTUR"),
		BatchTimUserAddTag("Tim用户添加标签","batch tim user addtag","BUAT"),
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
