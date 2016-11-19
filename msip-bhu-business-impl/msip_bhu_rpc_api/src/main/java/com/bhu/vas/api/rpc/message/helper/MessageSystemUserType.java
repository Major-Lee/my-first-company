package com.bhu.vas.api.rpc.message.helper;

import java.util.HashMap;
import java.util.Map;

public enum MessageSystemUserType {
	Visitor(0,"V","匿名用户"),
	Normal(1,"N","普通用户"),
	BHUUnion(2,"U","必虎联盟用户"),
	;
	//private String index;
	private int index;
	//short name
	private String sname;
	//full name
	private String fname;
	//是否可以通过通用api进行创建
	private boolean apiGen;
	
	private static Map<String, MessageSystemUserType> 	allUserTypesWithSName;
	private static Map<Integer, MessageSystemUserType> 	allUserTypesWithIndex;
	
	MessageSystemUserType(int index,String sname,String fname){
		this(index,sname,fname,true);
	}
	
	MessageSystemUserType(int index,String sname,String fname,boolean apiGen){
		this.index = index;
		this.sname = sname;
		this.fname = fname;
		this.apiGen = apiGen;
	}
		
	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public String getSname() {
		return sname;
	}

	public void setSname(String sname) {
		this.sname = sname;
	}

	public String getFname() {
		return fname;
	}

	public void setFname(String fname) {
		this.fname = fname;
	}

	public boolean isApiGen() {
		return apiGen;
	}

	public void setApiGen(boolean apiGen) {
		this.apiGen = apiGen;
	}

	public static MessageSystemUserType getBySName(String sname) {
		MessageSystemUserType utype = allUserTypesWithSName.get(sname);
		if(utype != null)
			return utype;
		else
			return MessageSystemUserType.Normal;
	}	
	
	public static MessageSystemUserType getByIndex(int index) {
		MessageSystemUserType utype = allUserTypesWithIndex.get(index);
		if(utype != null)
			return utype;
		else
			return MessageSystemUserType.Normal;
	}
	
	static {
		allUserTypesWithSName = new HashMap<String,MessageSystemUserType>();
		allUserTypesWithIndex = new HashMap<Integer,MessageSystemUserType>();
		MessageSystemUserType[] types = values();
		for (MessageSystemUserType type : types){
			allUserTypesWithSName.put(type.sname, type);
			allUserTypesWithIndex.put(type.index, type);
		}
			
	}
	
		
}
