package com.wecite.toplines.business.asyn.web.builder;

public enum DeliverMessageType {
	AC("ActionMessage" ,'A'),
	PM("PrivateMessage",'P'),
	CM("CustomMessage" ,'C'),
	SM("SystemMessage" ,'S');
	
	String name;
	char prefix;
	private DeliverMessageType(String name,char prefix)
    {
    	this.name = name;
    	this.prefix = prefix;
    }
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public char getPrefix() {
		return prefix;
	}
	public void setPrefix(char prefix) {
		this.prefix = prefix;
	}
	public static DeliverMessageType fromPrefix(char prefix){
		DeliverMessageType[] types = DeliverMessageType.values();
		for(DeliverMessageType type:types){
			if(type.getPrefix() == prefix)
				return type;
		}
		return null;
	}
}
