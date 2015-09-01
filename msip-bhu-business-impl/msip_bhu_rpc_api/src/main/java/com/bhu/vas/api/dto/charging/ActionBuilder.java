package com.bhu.vas.api.dto.charging;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.smartwork.msip.cores.helper.JsonHelper;

public class ActionBuilder {
	
	public enum ActionMode {
		DeviceOnline("DO"),
		DeviceOffline("DF"),
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
		return action;
	}
	public static DeviceOfflineAction builderDeviceOfflineAction(String mac,long ts){
		DeviceOfflineAction action = new DeviceOfflineAction();
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
	
	public static HandsetOfflineAction builderHandsetOfflineAction(String hmac,String mac,long ts){
		HandsetOfflineAction action = new HandsetOfflineAction();
		action.setHmac(hmac);
		action.setMac(mac);
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
