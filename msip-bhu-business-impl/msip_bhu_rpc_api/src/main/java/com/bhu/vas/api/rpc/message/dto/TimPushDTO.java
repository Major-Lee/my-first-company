package com.bhu.vas.api.rpc.message.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.smartwork.msip.localunit.RandomData;

@SuppressWarnings("serial")
public class TimPushDTO<T> implements java.io.Serializable{
	@JsonProperty("MsgRandom")
	private int msgRandom;
	
	@JsonProperty("MsgLifeTime")
	private int msgLifeTime;
	
	@JsonProperty("From_Account")
	@JsonInclude(Include.NON_NULL)
	private String fromAccount;
	
	@JsonProperty("Condition")
	@JsonInclude(Include.NON_NULL)
	private TimPushConditionDTO condition;
	
	public TimPushConditionDTO getCondition() {
		return condition;
	}
	public void setCondition(TimPushConditionDTO condition) {
		this.condition = condition;
	}

	@JsonProperty("MsgBody")
	private List<TimPushMsgBodyDTO<T>> msgBodyList;
	
	public int getMsgRandom() {
		return msgRandom;
	}
	public void setMsgRandom(int msgRandom) {
		this.msgRandom = msgRandom;
	}
	public int getMsgLifeTime() {
		return msgLifeTime;
	}
	public void setMsgLifeTime(int msgLifeTime) {
		this.msgLifeTime = msgLifeTime;
	}
	public String getFromAccount() {
		return fromAccount;
	}
	public void setFromAccount(String fromAccount) {
		this.fromAccount = fromAccount;
	}
	
	public List<TimPushMsgBodyDTO<T>> getMsgBodyList() {
		return msgBodyList;
	}
	public void setMsgBodyList(List<TimPushMsgBodyDTO<T>> msgBodyList) {
		this.msgBodyList = msgBodyList;
	}
	public TimPushDTO(){
		//离线时间默认7天
		this.msgRandom = RandomData.intNumber(999999999);
	}
}
