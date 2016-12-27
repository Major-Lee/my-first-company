package com.bhu.vas.api.rpc.message.dto;

import java.util.List;

import com.bhu.vas.api.rpc.message.helper.MessageTimHelper;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.smartwork.msip.localunit.RandomData;

@SuppressWarnings("serial")
public class TimSendMsgDTO<T> implements java.io.Serializable{
	//消息同步至发送方
	@JsonProperty("SyncOtherMachine")
	private int sync;
	
	@JsonProperty("From_Account")
	private String from_Account;
	
	@JsonProperty("To_Account")
	private String to_Account;
	
	@JsonProperty("MsgBody")
	private List<TimMsgBodyDTO<Object>> msgBodyList;
	
	public List<TimMsgBodyDTO<Object>> getMsgBodyList() {
		return msgBodyList;
	}

	public void setMsgBodyList(List<TimMsgBodyDTO<Object>> msgBodyList) {
		this.msgBodyList = msgBodyList;
	}

	@JsonProperty("MsgRandom")
	private int msgRandom;

	@JsonProperty("MsgTimeStamp")
	private int msgTimeStamp;
	
	public TimOfflinePushInfoDTO getOfflinePushInfo() {
		return offlinePushInfo;
	}

	public void setOfflinePushInfo(TimOfflinePushInfoDTO offlinePushInfo) {
		this.offlinePushInfo = offlinePushInfo;
	}

	@JsonProperty("OfflinePushInfo")
	private TimOfflinePushInfoDTO offlinePushInfo;
	
	public int getSync() {
		return sync;
	}

	public void setSync(int sync) {
		this.sync = sync;
	}

	public String getFrom_Account() {
		return from_Account;
	}

	public void setFrom_Account(String from_Account) {
		this.from_Account = from_Account;
	}

	public String getTo_Account() {
		return to_Account;
	}

	public void setTo_Account(String to_Account) {
		this.to_Account = to_Account;
	}


	public int getMsgRandom() {
		return msgRandom;
	}

	public void setMsgRandom(int msgRandom) {
		this.msgRandom = msgRandom;
	}

	public int getMsgTimeStamp() {
		return msgTimeStamp;
	}

	public void setMsgTimeStamp(int msgTimeStamp) {
		this.msgTimeStamp = msgTimeStamp;
	}

	public TimSendMsgDTO(){
		this.setSync(1);
		this.setMsgRandom(RandomData.intNumber(MessageTimHelper.MAXMSGRANDOM));
		this.setMsgTimeStamp(Integer.parseInt(String.valueOf(System.currentTimeMillis()/1000)));
	}
}
