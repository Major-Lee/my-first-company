package com.bhu.vas.api.helper.credentials;

import java.util.HashMap;
import java.util.Map;

import com.smartwork.msip.cores.helper.authorization.SafetyBitMarkHelper;

/**
 * 用户角色定义
 * 或者用apache shiro实现？
 * @author Edmond
 *
 */
public enum UserRoleType {
	//管理员账户 系统创建
	SuperAdmin(SafetyBitMarkHelper.All,"SA","管理员用户",false),
	//普通用户
	Normal(SafetyBitMarkHelper.None,"NO","普通用户"),
	//运营人员
	Operator(SafetyBitMarkHelper.A01,"OP","运营人员"),
	/*
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
	BusinessSelfor(SafetyBitMarkHelper.A25,"BS","自运营的商户"),*/
	;
	//private String index;
	private int index;
	//short name
	private String sname;
	//full name
	private String fname;
	//是否可以通过通用api进行创建
	private boolean apiGen;
	
	private static Map<String, UserRoleType> 	allUserTypesWithSName;
	private static Map<Integer, UserRoleType> 	allUserTypesWithIndex;
	
	UserRoleType(int index,String sname,String fname){
		this(index,sname,fname,true);
	}
	
	UserRoleType(int index,String sname,String fname,boolean apiGen){
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

	public static UserRoleType getBySName(String sname) {
		UserRoleType utype = allUserTypesWithSName.get(sname);
		if(utype != null)
			return utype;
		else
			return UserRoleType.Normal;
	}	
	
	public static UserRoleType getByIndex(int index) {
		UserRoleType utype = allUserTypesWithIndex.get(index);
		if(utype != null)
			return utype;
		else
			return UserRoleType.Normal;
	}
	
	static {
		allUserTypesWithSName = new HashMap<String,UserRoleType>();
		allUserTypesWithIndex = new HashMap<Integer,UserRoleType>();
		UserRoleType[] types = values();
		for (UserRoleType type : types){
			allUserTypesWithSName.put(type.sname, type);
			allUserTypesWithIndex.put(type.index, type);
		}
			
	}
	
		
}
