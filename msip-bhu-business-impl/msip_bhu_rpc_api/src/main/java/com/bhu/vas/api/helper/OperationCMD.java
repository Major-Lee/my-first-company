package com.bhu.vas.api.helper;

import java.util.HashMap;
import java.util.Map;

public enum OperationCMD {
	//QueryTeminals("01","查询设备当前在线终端"),
	QueryDeviceStatus("01","查询设备cpu,内存利用率","sysperf"),
	QueryDeviceFlow("02","查询设备流量","if_stat"),
	QueryDeviceLocationS1("03","查询设备地理位置Step1",""),
	QueryDeviceLocationS2("04","查询设备地理位置Step2","sysdebug"),
	;
	
	static Map<String, OperationCMD> allOperationCMDs;
	
	String no;
	String desc;
	String cmd;
	
	OperationCMD(String no,String desc,String cmd){
		this.no = no;
		this.desc = desc;
		this.cmd = cmd;
	}
	static {
		allOperationCMDs = new HashMap<String,OperationCMD>();
		OperationCMD[] types = values();
		for (OperationCMD type : types)
			allOperationCMDs.put(type.getNo(), type);
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
	
	public String getCmd() {
		return cmd;
	}

	public void setCmd(String cmd) {
		this.cmd = cmd;
	}

	public static OperationCMD getOperationCMDFromNo(String no) {
		return allOperationCMDs.get(no);
	}
}
