package com.bhu.vas.api.dto.charging;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.smartwork.msip.cores.helper.JsonHelper;

public class ActionBuilder {
	
	public enum Hint {
		LogAnalyseDone("LAD","日志截尾，补齐到当天最后时间"),
		DeviceOffline("DOF","所属设备下线"),
		ElementNotExist("NEX","不存在，强制下线，补齐到不存在指令日志时间"),
		ElementUpLoseSoCombinCurrentLastElement("ULC","缺失up，合并上条数据中的down为当前记录down时间"),
		ElementUpLoseSoPollishDaysBegin("ULP","缺失up，补齐到当天开始"),
		ElementDownLose("DLO","缺失down，补齐"),
		HandsetInSyncSoForceOnline("HIS","在sync列表中，强制上线"),
		HandsetNotInSyncSoForceOffline("HNS","不在sync列表中，强制下线"),
		;
		static Map<String, Hint> allHint;
		private String key;
		private String desc;
		Hint(String key,String desc) {
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

		public static Hint fromKey(String key){
			return allHint.get(key);
		}
		
		static {
			allHint = new HashMap<String,Hint>();
			Hint[] types = values();//new ImageType[] {JPG, BMP, GIF, PNG, TIFF};
			for (Hint type : types){
				allHint.put(type.getKey(), type);
			}
		}
	}
	
	public enum ActionMode {
		DeviceOnline("DO"),
		DeviceOffline("DF"),
		DeviceNotExist("DE"),
		HandsetOnline("HO"),
		HandsetOffline("HF"),
		HandsetSync("HS"),
		;
		static Map<String, ActionMode> allActionMode;
		private String prefix;
		ActionMode(String prefix) {
			this.prefix = prefix;
		}

		public String getPrefix() {
			return prefix;
		}


		public void setPrefix(String prefix) {
			this.prefix = prefix;
		}


		public static ActionMode fromPrefix(String prefix){
			return allActionMode.get(prefix);
		}
		
		static {
			allActionMode = new HashMap<String,ActionMode>();
			ActionMode[] types = values();//new ImageType[] {JPG, BMP, GIF, PNG, TIFF};
			for (ActionMode type : types){
				allActionMode.put(type.getPrefix(), type);
			}
		}
	}
	
	public static DeviceOnlineAction builderDeviceOnlineAction(String mac,long ts){
		DeviceOnlineAction action = new DeviceOnlineAction();
		action.setMac(mac);
		action.setTs(ts);
		action.setSm(false);
		return action;
	}
	public static DeviceOnlineAction builderDeviceSimulateOnlineAction(String mac,long ts){
		DeviceOnlineAction action = new DeviceOnlineAction();
		action.setMac(mac);
		action.setTs(ts);
		action.setSm(true);
		return action;
	}
	public static DeviceOfflineAction builderDeviceOfflineAction(String mac,long ts){
		DeviceOfflineAction action = new DeviceOfflineAction();
		action.setMac(mac);
		action.setTs(ts);
		return action;
	}
	
	public static DeviceNotExistAction builderDeviceNotExistAction(String mac,long ts){
		DeviceNotExistAction action = new DeviceNotExistAction();
		action.setMac(mac);
		action.setTs(ts);
		return action;
	}
	
	
	public static HandsetOnlineAction builderHandsetOnlineAction(String hmac,String mac,long ts){
		HandsetOnlineAction action = new HandsetOnlineAction();
		action.setHmac(hmac);
		action.setMac(mac);
		action.setTs(ts);
		return action;
	}
	
	public static HandsetSyncAction builderHandsetSyncAction(List<String> hmacs,String mac,long ts){
		HandsetSyncAction action = new HandsetSyncAction();
		action.setHmacs(hmacs);
		action.setMac(mac);
		action.setTs(ts);
		return action;
	}
	
	public static HandsetOfflineAction builderHandsetOfflineAction(String hmac,String mac,long tx_bytes,long rx_bytes,long ts){
		HandsetOfflineAction action = new HandsetOfflineAction();
		action.setHmac(hmac);
		action.setMac(mac);
		action.setTx_bytes(tx_bytes);
		action.setRx_bytes(rx_bytes);
		action.setTs(ts);
		return action;
	}
	
	public static ActionMode determineActionType(String messagejsonHasPrefix){
		if(StringUtils.isEmpty(messagejsonHasPrefix)) return null;
    	String prefix = messagejsonHasPrefix.substring(0,2);
    	ActionMode type = ActionMode.fromPrefix(prefix);
    	return type;
	}
	public static String determineActionMessage(String messagejsonHasPrefix){
		if(StringUtils.isEmpty(messagejsonHasPrefix)) return null;
    	return messagejsonHasPrefix.substring(2);
	}
	public static <T extends ChargingAction> T fromJson(String messagejson,Class<T> classz){
		if(StringUtils.isEmpty(messagejson)) return null;
		return JsonHelper.getDTO(messagejson, classz);
	}
	public static String toJsonHasPrefix(ChargingAction message){
		StringBuilder sb = new StringBuilder();
		sb.append(message.getAct()).append(toJson(message));
		return sb.toString();
	}
	public static String toJson(ChargingAction message){
		return JsonHelper.getJSONString(message,false);
	}
}
