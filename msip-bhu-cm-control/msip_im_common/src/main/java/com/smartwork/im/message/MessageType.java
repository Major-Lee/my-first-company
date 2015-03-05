package com.smartwork.im.message;

import java.util.HashMap;
import java.util.Map;

public enum MessageType {
	
	CliSignonRequest("ClSQ"),
	CliSignonResponse("ClSP"),
	
	CliSignoffRequest("ClOQ"),
	CliSignoffResponse("ClOP"),
	
	
	CmRegisterRequest("CmRQ"),
	CmRegisterResponse("CmRP"),
	
	CmClientSessionAuthenticatedNotifyRequest("CmAN"),
	CmClientSessionClosedNotifyRequest("CmCN"),
	
	
	DispatcherServerHaltedRequest("DSHR"),
	
	//被废弃的消息******
	ChatDeprecatedMediaMessage("ChPM"),
	BroadcastDeprecatedMessage("BrCA"),
	//****************
	
	
	BroadcastMessage("BaCA"),
	//ChatPeerTextMessage("ChPT"),
	ChatPeerMediaMessage("CaPM"),
	ChatGroupMediaMessage("ChGM"),
	ChatReadedMessage("ChRD"),
	ChatReceiptMessage("ChRT"),
	
	KickSimpleHintMessage("SiHn"),
	
	//从其他系统来的消息，例如 题 被答成功失败后通知 复活 题的所有人，此类型消息 用户在线的时候直接转发，不在线存入离线消息库中
	/*NotifyAnswerActionMessage("NaAc"),
	NotifyReviveActionMessage("NrAc"),*/
	
	NotifyCommonActionMessage("NcAc"),
	NotifyUserFrdApplyActionMessage("Nufa"),//添加好友申请通知
	NotifyUserFrdJoinActionMessage("Nufj"),//新增好友通知
	NotifyUserFrdRemovedActionMessage("Nufr"),//删除好友通知
	NotifyUserFrdIntroActionMessage("Nufi"),//介绍好友通知
	
	UnkownMessage("Unkn");
	
	private String display;
	private static Map<String, MessageType> mapKeyDisplay;
	private MessageType(String display){
		this.display = display;
	}

	public String getDisplay() {
		return display;
	}

	public void setDisplay(String display) {
		this.display = display;
	}
	
	static {
		mapKeyDisplay = new HashMap<String, MessageType>();
		MessageType[] types = values();//new ThumbType[] {SMALL, MIDDLE, LARGE, ORIGINAL};
		for (MessageType type : types){
			mapKeyDisplay.put(type.getDisplay(), type);
		}
	}
	public static MessageType getByDisplay(String display) {
		MessageType ret = (MessageType) mapKeyDisplay.get(display);
		return ((ret != null) ? ret : UnkownMessage);
	}
	
	public static boolean isUserType(String display){
		MessageType ret = getByDisplay(display);
		if(ret == NotifyUserFrdApplyActionMessage || ret == NotifyUserFrdJoinActionMessage
				|| ret == NotifyUserFrdRemovedActionMessage || ret == NotifyUserFrdIntroActionMessage){
			return true;
		}
		return false;
	}
	
	public static boolean isUserType(MessageType type){
		if(type == NotifyUserFrdApplyActionMessage || type == NotifyUserFrdJoinActionMessage
				|| type == NotifyUserFrdRemovedActionMessage || type == NotifyUserFrdIntroActionMessage){
			return true;
		}
		return false;
	}
	
	public static void main(String[] args){
		System.out.println(isUserType("Nufa"));
	}
}
