package com.bhu.vas.api.helper;

import java.util.HashMap;
import java.util.Map;

/**
 * 对设备配置的具体操作定义
 * @author lawliet
 *
 */
public enum OperationDS {
	
	DS_Ad("01","修改广告配置"),
	DS_URouterDefaultAcl("02","URouter设备的约定黑名单配置");

	
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
