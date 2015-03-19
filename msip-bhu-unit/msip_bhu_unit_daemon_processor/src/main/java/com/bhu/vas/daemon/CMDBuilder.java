package com.bhu.vas.daemon;

import com.smartwork.msip.cores.helper.StringHelper;


public class CMDBuilder {
	//1. 查询当前在线终端，下发此命令后触发设备主动上报一次
	private static final String query_device_teminals_cmd_template = "00001001%s0000000000"+"000000000006"+"<param><ITEM wlan_user_notify=\"enable\" trap=\"disable\" wlan_user_sync=\"1\" /></param>";
	
	//1. 查询cpu,内存利用率
	private static final String query_device_status_cmd_template =   "00001001%s%s"+"000100000001"+"<cmd><ITEM index=\"1\" cmd=\"sysperf\"/></cmd>";
	
	
	//1. 查询wifi地理位置命令第一步
	private static final String query_device_location_step1_cmd_template = "00001001%s%s"+"000100000001"+"<cmd><ITEM cmd=\"sysdebug\" supercmd=\"wifiloc -a\" /></cmd>";
	
	//1. 查询wifi地理位置命令第二步
	private static final String query_device_location_step2_cmd_template = "00001001%s%s"+"000100000001"+"<report><ITEM cmd=\"sysdebug\" serial=\"%s\" op=\"get\"/></report>";
	
	public static final String SuffixTemplete = "%010d";
	
	
	public static String builderDeviceOnlineTeminalQuery(String wifi_mac){
		return String.format(query_device_teminals_cmd_template, StringHelper.unformatMacAddress(wifi_mac));
	}
	
	public static String builderDeviceStatusQuery(String wifi_mac,int taskid){
		return String.format(query_device_status_cmd_template, StringHelper.unformatMacAddress(wifi_mac),String.format(SuffixTemplete,taskid));
	}
	
	public static String builderDeviceLocationStep1Query(String wifi_mac,int taskid){
		return String.format(query_device_location_step1_cmd_template, StringHelper.unformatMacAddress(wifi_mac),String.format(SuffixTemplete,taskid));
	}
	
	public static String builderDeviceLocationStep2Query(String wifi_mac,int taskid,String serialno){
		return String.format(query_device_location_step2_cmd_template, StringHelper.unformatMacAddress(wifi_mac),String.format(SuffixTemplete,taskid),serialno);
	}
}
