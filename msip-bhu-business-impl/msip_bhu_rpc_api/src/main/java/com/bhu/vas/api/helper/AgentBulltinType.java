package com.bhu.vas.api.helper;

import java.util.HashMap;
import java.util.Map;

public enum AgentBulltinType{
	Normal("N","通用"),
	BatchImport("B","批量导入"),
	SettlementAmountNotice("S","每月结算金额"),
	ArrivalNotice("A","每月到账通知"),
	DeliveryNotice("D","发货通知"),
	;
	static Map<String, AgentBulltinType> agentBulltinTypes;
	String key;
	String desc;
	
	AgentBulltinType(String key,String desc){
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
	static {
		agentBulltinTypes = new HashMap<String,AgentBulltinType>();
		AgentBulltinType[] types = values();
		for (AgentBulltinType type : types)
			agentBulltinTypes.put(type.getKey(), type);
	}
	
	public static AgentBulltinType getAgentBulltinTypeFromKey(String key) {
		return agentBulltinTypes.get(key);
	}
}
