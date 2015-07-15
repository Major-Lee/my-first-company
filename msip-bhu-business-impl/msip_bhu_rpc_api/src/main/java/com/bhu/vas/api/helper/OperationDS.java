package com.bhu.vas.api.helper;

import java.util.HashMap;
import java.util.Map;

/**
 * 对设备配置的具体操作定义
 * @author lawliet
 *
 */
public enum OperationDS {
	
	DS_Http_Ad_Start("01","开启广告注入"),
	DS_Power("02","修改信号强度"),
	DS_VapPassword("03","修改vap密码"),
	DS_AclMacs("04","修改黑名单列表名单"),
	DS_RateControl("05","修改流量控制"),
	DS_AdminPassword("06","修改管理密码"),
	DS_MM("07","修改终端别名"),
	DS_VapGuest("08","修改vap访客网络开关"),
	DS_LinkMode("10","修改上网方式"),
	
	DS_Http_404_Start("15","开启404错误页面"),
	DS_Http_Redirect_Start("16","开启http redirect"),
	DS_Http_Portal_Start("17","开启http portal"),
	
	DS_Http_Portal_Stop("18","关闭http portal"),
	DS_Http_Ad_Stop("19","关闭广告注入"),
	DS_Http_404_Stop("20","关闭404错误页面"),
	DS_Http_Redirect_Stop("21","关闭http redirect"),
	;

	
	static Map<String, OperationDS> allOperationDSs;
	/*public static int Opt_Length = 3;
	public static int Taskid_Length = 7;*/
	String no;
	String desc;
	
	OperationDS(String no,String desc){
		this.no = no;
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

	public static OperationDS getOperationCMDFromNo(String no) {
		return allOperationDSs.get(no);
	}
}
