package com.bhu.vas.api.helper;

import java.util.HashMap;
import java.util.Map;

/**
 * 对设备配置的具体操作定义
 * @author lawliet
 *
 */
public enum OperationDS {
	
	DS_Power("02","修改信号强度"),
	DS_Power_multi("42","修改信号强度"),
	DS_VapPassword("03","修改vap密码"),
	DS_VapPassword_multi("43","修改vap密码"),
	DS_AclMacs("04","修改黑名单列表名单"),
	DS_RateControl("05","修改流量控制"),
	DS_AdminPassword("06","修改管理密码"),
	DS_MM("07","修改终端别名"),
	DS_VapGuest("08","修改vap访客网络开关"),
	DS_LinkMode("10","修改上网方式"),
	
	DS_VapHidessid_multi("48","修改vap hide ssid"),
	DS_Multi_Combine("49","双频合一修改"),
	DS_RealChannel("11","切换信道"),
	
	
	DS_Http_Ad_Start("01","开启广告注入"),
	
	//DS_SharedNetworkWifi_Start("15","开启共享网络"),
	//DS_SharedNetworkWifi_Stop("16" ,"关闭共享网络"),
	
	//DS_Http_404_Start("15","开启404错误页面"),
	//DS_Http_Redirect_Start("16","开启http redirect"),
	
	//DS_Http_Portal_Start("17","开启http portal"),
	//DS_Http_Portal_Stop("18","关闭http portal"),
	
	DS_SharedNetworkWifi_Limit("16","访客网络限速"),
	DS_SharedNetworkWifi_Start("17","开启访客网络"),
	DS_SharedNetworkWifi_Stop("18" ,"关闭访客网络"),
	
	DS_Http_Ad_Stop("19","关闭广告注入"),
	//DS_Http_404_Stop("20","关闭404错误页面"),
	//DS_Http_Redirect_Stop("21","关闭http redirect"),
	
	DS_Http_VapModuleCMD_Start("25","ModuleCMD","开启全部增值指令-404、redirect、品牌及渠道指令"),
	DS_Http_VapModuleCMD_Stop("26","ModuleCMD","关闭全部增值指令-404、redirect、品牌及渠道指令"),
	
	DS_Switch_WorkMode("30","设备工作模式切换指令"),
	
	DS_Plugins("50","plugins"),
	
	//DS_PassThrough("99","透传配置"),
	;

	public static final String Empty_OperationDS = "00";
	static Map<String, OperationDS> allOperationDSs;
	/*public static int Opt_Length = 3;
	public static int Taskid_Length = 7;*/
	String no;
	String ref;
	String desc;
	
	OperationDS(String no,String desc){
		this.no = no;
		this.desc = desc;
	}
	OperationDS(String no,String ref,String desc){
		this.no = no;
		this.ref = ref;
		this.desc = desc;
	}
	static {
		allOperationDSs = new HashMap<String,OperationDS>();
		OperationDS[] types = values();
		for (OperationDS type : types)
			allOperationDSs.put(type.getNo(), type);
	}

	public String getNo() {
		return no;
	}

	public void setNo(String no) {
		this.no = no;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getRef() {
		return ref;
	}
	public void setRef(String ref) {
		this.ref = ref;
	}
	public boolean hasRef(){
		return ref != null;
	}
	public static OperationDS getOperationDSFromNo(String no) {
		return allOperationDSs.get(no);
	}
}
