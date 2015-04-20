package com.bhu.vas.api.helper;

import java.util.HashMap;
import java.util.Map;

public enum OperationCMD {
	//QueryTeminals("01","查询设备当前在线终端"),
	//1. 查询cpu,内存利用率
	QueryDeviceStatus("01","查询设备cpu,内存利用率","sysperf",
			"00001001%s%s%s"+"000100000001"+"<cmd><ITEM index=\"1\" cmd=\"sysperf\"/></cmd>"),
	//1. 查询设备流量
	QueryDeviceFlow("02","查询设备流量","if_stat",
			"00001001%s%s%s"+"000100000001"+"<cmd><ITEM index=\"1\" cmd=\"if_stat\"/></cmd>"),
			
	//1. 查询wifi地理位置命令第一步
	QueryDeviceLocationS1("03","查询设备地理位置Step1","",
			"00001001%s%s%s"+"000100000001"+"<cmd><ITEM cmd=\"sysdebug\" supercmd=\"wifiloc -a\" /></cmd>"),
	//1. 查询wifi地理位置命令第二步
	QueryDeviceLocationS2("04","查询设备地理位置Step2","sysdebug",
			"00001001%s%s%s"+"000100000001"+"<report><ITEM cmd=\"sysdebug\" serial=\"%s\" op=\"get\"/></report>"),
			
	//1. 查询wifi地理位置 等待设备通知结果
	QueryDeviceLocationNotify("03","查询设备地理位置notify","",
			"00001001%s%s%s"+"000100000001"+"<cmd><ITEM cmd=\"sysdebug\" supercmd=\"wifiloc -a\" __notify=\"true\"  serial=\"%s\"/></cmd>"),
			
	QueryDeviceSetting("10","查询设备配置","",
			"00001001%s%s%s"+"000100000001"+"<query><ITEM path=\"dev.wifi.radio,dev.wifi.vap,dev.wifi.acllist,dev.net.interface,dev.net.rate_control,dev.mod.basic.wan,dev.sys.users\"/></query>"),
			//dev.wifi.radio 信号强度
			//dev.wifi.vap WiFi设置（SSID、密码、是否加密）
			//dev.wifi.vap  dev.wifi.acllist 黑名单
			//dev.net.interface  dev.net.rate_control 限速
			//dev.mod.basic.wan  上网方式设置（PPPoE、DHCP、Static）
			//dev.sys.users		登陆密码
	//cmd="wlanstatus" interface="wlan0"
	QueryDeviceTerminals("11","查询设备终端","","00001001%s%s%s"+"000100000001"+"<cmd><ITEM cmd=\"wlanstatus\" interface=\"%s\"/></cmd>"),

	QueryDeviceSpeedNotify("12","查询设备网速","","00001001%s%s%s"+"000100000001"+"<cmd><ITEM cmd=\"net_speed_test\" max_test_time=\"30\" __notify=\"true\"  serial=\"%s\" url=\"http://mirrors.hust.edu.cn/apache/maven/maven-3/3.2.5/binaries/apache-maven-3.2.5-bin.tar.gz\"/></cmd>"),
	;
	
	static Map<String, OperationCMD> allOperationCMDs;
	
	String no;
	String desc;
	String cmd;
	String cmdtpl;
	
	OperationCMD(String no,String desc,String cmd,String cmdtpl){
		this.no = no;
		this.desc = desc;
		this.cmd = cmd;
		this.cmdtpl = cmdtpl;
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

	public String getCmdtpl() {
		return cmdtpl;
	}

	public void setCmdtpl(String cmdtpl) {
		this.cmdtpl = cmdtpl;
	}

	public static OperationCMD getOperationCMDFromNo(String no) {
		return allOperationCMDs.get(no);
	}
}
