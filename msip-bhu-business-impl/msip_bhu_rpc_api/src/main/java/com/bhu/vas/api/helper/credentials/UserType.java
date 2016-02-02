package com.bhu.vas.api.helper.credentials;

import java.util.HashMap;
import java.util.Map;

import com.smartwork.msip.cores.helper.authorization.SafetyBitMarkHelper;

public enum UserType {
	//管理员账户 系统创建
	SuperAdmin(SafetyBitMarkHelper.All,"SA","管理员用户",false),
	Normal(SafetyBitMarkHelper.None,"NO","普通用户"),
	
	//增值平台
	//增值平台管理员
	VapAdmin(SafetyBitMarkHelper.A31,"VA","管理员用户",false),
	
	//分销商用户
	AgentAdmin(SafetyBitMarkHelper.A26,"AA","分销商用户"),
	AgentNormal(SafetyBitMarkHelper.A27,"AN","分销商用户"),
	AgentSellor(SafetyBitMarkHelper.A28,"AS","销售用户"),
	AgentFinance(SafetyBitMarkHelper.A29,"AF","财务用户"),
	AgentWarehouseManager(SafetyBitMarkHelper.A30,"AW","仓储用户"),
	
	//商业wifi用户
	//商业wifi 管理帐号 系统创建
	BusinessAdmin(SafetyBitMarkHelper.A23,"BA","商业 wifi 管理员",false),
	//商业 wifi 小型运营商客户
	BusinessNormal(SafetyBitMarkHelper.A24,"BN","商业 wifi 小型运营商客户"),
	//自运营的商户
	BusinessSelfor(SafetyBitMarkHelper.A25,"BS","自运营的商户"),
	;
	//private String index;
	private int index;
	//short name
	private String sname;
	//full name
	private String fname;
	//是否可以通过通用api进行创建
	private boolean apiGen;
	
	private static Map<String, UserType> 	allUserTypesWithSName;
	private static Map<Integer, UserType> 	allUserTypesWithIndex;
	
	UserType(int index,String sname,String fname){
		this(index,sname,fname,true);
	}
	
	UserType(int index,String sname,String fname,boolean apiGen){
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

	public static UserType getBySName(String sname) {
		UserType utype = allUserTypesWithSName.get(sname);
		if(utype != null)
			return utype;
		else
			return UserType.Normal;
	}	
	
	public static UserType getByIndex(int index) {
		UserType utype = allUserTypesWithIndex.get(index);
		if(utype != null)
			return utype;
		else
			return UserType.Normal;
	}
	
	static {
		allUserTypesWithSName = new HashMap<String,UserType>();
		allUserTypesWithIndex = new HashMap<Integer,UserType>();
		UserType[] types = values();
		for (UserType type : types){
			allUserTypesWithSName.put(type.sname, type);
			allUserTypesWithIndex.put(type.index, type);
		}
			
	}
	
		
}
