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
	DS_Power("02","修改信号强度"),
	DS_VapPassword("03","修改vap密码"),
	DS_AclMacs("04","修改黑名单列表名单"),
	DS_RateControl("05","修改流量控制"),
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
