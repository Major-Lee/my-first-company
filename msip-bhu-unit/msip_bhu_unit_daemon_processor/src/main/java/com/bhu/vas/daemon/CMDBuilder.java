package com.bhu.vas.daemon;

import com.smartwork.msip.cores.helper.StringHelper;

/**
 *  上端下发消息：
	1001. 任务下发：消息体格式12字节mac地址，后接10字节任务id。
                    设备报文主类型(4字节)，子类型(8字节)
                    后接xml格式的任务字符串.
                    任务id为0，表明任务不需要回复。
 * <param>开头的那个报文，这两个数字是0000和00000006；// 但先改成0000和00000000，为了调试
 * @author Edmond
 *
 */
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
