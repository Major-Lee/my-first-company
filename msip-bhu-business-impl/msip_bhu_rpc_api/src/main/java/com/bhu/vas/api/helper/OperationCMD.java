package com.bhu.vas.api.helper;

import java.util.HashMap;
import java.util.Map;

/**
 * 由于设备端限制任务号 FFFFFFFF 对应为 4294967295
 * 业务取前3位为 429，为了不溢出，最大值为428 后7位为任务id，组合下行给设备 数据库中任务id超过9999999后需要重置，0-100000区间保留给自动触发任务
 * OperationCMD no 区间 100~428
 * 
 * 
 * 备注：特殊处理Param指令，不涉及到任务id，设备上线后需要下发这些指令 < 100的定义不加入 特殊处理
 * @author Edmond
 *
 */
public enum OperationCMD {
	//1. 查询当前在线终端，下发此命令后触发设备主动上报一次,不走任务号机制  下发管理参数触发设备自动上报用户通知并同步终端
	ParamQueryTeminals("001","Param指令查询当前在线终端","param",
			"00001001%s0010000000000"+"000000000006"+"<param><ITEM wlan_user_notify=\"enable\" trap=\"disable\" wlan_user_sync=\"1\" /></param>"),
	//2. 设备终端探测开启关闭指令
	ParamWifiSinffer("002","Param指令设备终端探测开启关闭","param",
			"00001001%s0020000000000"+"000000000006"+"<param><ITEM sta_sniffer=\"%s\" sta_sniffer_batch_num=\"%s\" sta_sniffer_delay=\"%s\" sta_sniffer_url=\"%s\"/></param>"),
	//3. 查询当前在线终端以sync的方式报到服务器
	ParamQuerySyncDeviceOnlineTeminals("003","查询当前在线终端","param","00001001%s0010000000000"+"000000000006"+"<param><ITEM  wlan_user_notify=\"disable\" wlan_user_sync=\"1\" /></param>"),		
	
	//1. 查询cpu,内存利用率
	QueryDeviceStatus("100","查询设备cpu,内存利用率","sysperf",
			"00001001%s%s%s"+"000100000001"+"<cmd><ITEM index=\"1\" cmd=\"sysperf\"/></cmd>"),
	//1. 查询设备流量
	QueryDeviceFlow("101","查询设备流量","if_stat",
			"00001001%s%s%s"+"000100000001"+"<cmd><ITEM index=\"1\" cmd=\"if_stat\"/></cmd>"),
			
/*	//1. 查询wifi地理位置命令第一步
	QueryDeviceLocationS1("030","查询设备地理位置Step1","",
			"00001001%s%s%s"+"000100000001"+"<cmd><ITEM cmd=\"sysdebug\" supercmd=\"wifiloc -a\" /></cmd>"),*/
	//1. 查询wifi地理位置命令第二步
	QueryDeviceLocationS2("040","查询设备地理位置Step2","sysdebug",
			"00001001%s%s%s"+"000100000001"+"<report><ITEM cmd=\"sysdebug\" serial=\"%s\" op=\"get\"/></report>"),
		
	//1. 查询wifi地理位置 等待设备通知结果
	QueryDeviceLocationNotify("102","查询设备地理位置notify","",
			"00001001%s%s%s"+"000100000001"+"<cmd><ITEM cmd=\"sysdebug\" supercmd=\"wifiloc -a\" __notify=\"true\" serial=\"%s\"/></cmd>"),
			
	QueryDeviceSetting("103","查询设备配置","",
			"00001001%s%s%s"+"000100000001"+"<query><ITEM path=\"dev.wifi.radio,dev.wifi.vap,dev.wifi.acllist,dev.net.interface,dev.net.rate_control,dev.net.ad,dev.mod.basic.wan,dev.sys.users,dev.sys.config,dev.net.mac_management\"/></query>"),
			//dev.wifi.radio 信号强度
			//dev.wifi.vap WiFi设置（SSID、密码、是否加密）
			//dev.wifi.vap  dev.wifi.acllist 黑名单
			//dev.net.interface  dev.net.rate_control 限速
			//dev.mod.basic.wan  上网方式设置（PPPoE、DHCP、Static）
			//dev.sys.users		登陆密码
	//cmd="wlanstatus" interface="wlan0"
	//QueryDeviceTerminals("104","查询设备终端","","00001001%s%s%s"+"000100000001"+"<cmd><ITEM cmd=\"wlanstatus\" interface=\"%s\"/></cmd>"),
	QueryDeviceTerminals("104","查询设备终端","","00001001%s%s%s"+"000100000001"+"<cmd><ITEM cmd=\"wlanstatus\" period=\"%s\" duration=\"%s\" __notify=\"true\"  serial=\"%s\"/></cmd>"),
			
	//QueryDeviceSpeedNotify("105","查询设备网速","","00001001%s%s%s"+"000100000001"+"<cmd><ITEM cmd=\"net_speed_test\" max_test_time=\"%s\" combine=\"1\" __notify=\"true\"  serial=\"%s\" url=\"http://vap.bhunetworks.com/speedtest/speedtest.tar.gz\"/></cmd>"),
	
	QueryDeviceSpeedNotify("105","查询设备网速","","00001001%s%s%s"+"000100000001"+"<cmd><ITEM cmd=\"net_speed_test\" download=\"%s\" upload=\"%s\" __notify=\"enable\"  period=\"%s\"  duration=\"%s\" serial=\"%s\" /></cmd>"),
	
	QueryDeviceRateNotify("106","查询设备实时速率","","00001001%s%s%s"+"000100000001"+"<cmd><ITEM cmd=\"ifrate\" interface=\"%s\" period=\"%s\" duration=\"%s\" __notify=\"true\"  serial=\"%s\" /></cmd>"),
	//params:mac opt taskid serverip
	TurnOnDeviceDPINotify( "107","开启设备dpi数据上报功能","","00001001%s%s%s"+"000100000001"+"<cmd><ITEM index=\"1\" cmd=\"dpi\" enable=\"enable\" server_ip=\"%s\" server_port=\"514\" filter=\"text/html,application/html,text/plain,text/xml,application/json\"/></cmd>"),//text/html,text/plain,text/xml,application/json
	//params:mac opt taskid
	TurnOffDeviceDPINotify("108","关闭设备dpi数据上报功能","","00001001%s%s%s"+"000100000001"+"<cmd><ITEM index=\"1\" cmd=\"dpi\" enable=\"disable\" /></cmd>"),
	
	QueryDhcpcStatus("109","查询dhcp模式下的状态信息(ip,网关,dns,子网掩码)","","00001001%s%s%s"+"000100000001"+"<cmd><ITEM cmd=\"dhcpcstatus\" interface=\"%s\" /></cmd>"),
	QueryDeviceUsedStatus("110","查询设备使用情况","data_stats","00001001%s%s%s"+"000100000001"+"<cmd><ITEM cmd=\"data_stats\"/></cmd>"),
	
	QueryDeviceSysinfo("111","查询设备系统信息","sysinfo","00001001%s%s%s"+"000100000001"+"<cmd><ITEM cmd=\"sysinfo\"/></cmd>"),
	//TriggerHttp404ResourceUpdate("118","触发设备更新http404资源包","","00001001%s%s%s"+"000100000001"+"<cmd><ITEM cmd=\"resource_upgrade\" type=\"http404\" url=\"%s\" resource_ver=\"%s\" serial=\"%s\" __notify=\"true\"/></cmd>"),
	TriggerHttpPortalResourceUpdate("119","触发设备更新portal资源包","","00001001%s%s%s"+"000100000001"+"<cmd><ITEM cmd=\"resource_upgrade\" type=\"portal\" url=\"%s\" resource_ver=\"%s\"serial=\"%s\" __notify=\"true\"/></cmd>"),
	
	DeviceWifiTimerStart("125","开启wifi定时功能","","00001001%s%s%s"+"000100000001"+"<cmd><ITEM cmd=\"wifi_schedule\" enable=\"enable\" rule=\"%s-on,%s-off%s\" __notify=\"true\"/></cmd>"),
	DeviceWifiTimerStop("126","关闭wifi定时功能","","00001001%s%s%s"+"000100000001"+"<cmd><ITEM cmd=\"wifi_schedule\" enable=\"disable\" __notify=\"true\"/></cmd>"),
	DeviceWifiTimerQuery("127","查询wifi定时功能","","00001001%s%s%s"+"000100000001"+"<cmd><ITEM cmd=\"wifi_schedule\" __notify=\"true\"/></cmd>"),
	
	//<dev><net><ad><ITEM  bhu_enable=\"enable/disable\"  bhu_ad_url=\"广告url\" id=\"xxxx\" /></ad></net></dev>
	//在广告注入以后，会在广告url后附加参数gw_id=xxxx&stamac=xxxx, 第一个参数来源于配置时候的id，可用于标识AP。比如安装于某个商户，每个商户id不同。第二个参数是关联的终端mac.
	//DevHTMLInjectionNotify("150","Html注入","","00001001%s%s%s"+"000100000001"+"<dev><net><ad><ITEM  bhu_enable=\"%s\"  bhu_ad_url=\"%s\" bhu_id=\"%s\" /></ad></net></dev>"),
	//修改设备配置通用指令
	ModifyDeviceSetting("151","设备配置修改通用指令","","00001001%s%s%s"+"000100000001"+"%s"),
	
	DeviceDelayReboot("152","设备延迟重启","","00001001%s%s%s"+"000100000001"+"<cmd><ITEM cmd=\"delayreboot\" delay=\"5\"/></cmd>"),

	DeviceUpgrade("153", "设备升级","","00001001%s%s%s"+"000100000001"+"<cmd><ITEM cmd=\"firmware_upgrade\" download_timeout=\"1800\" url=\"%s\" upgrade_begin=\"%s\" upgrade_end=\"%s\" __notify=\"true\"  serial=\"%s\" /></cmd>"),
	DeviceModuleUpgrade("154", "设备module升级(设备vapmudule不为空)以上","","00001001%s0000000000000000100000012%s%s%s"+"<upgrade><ITEM url = \"%s\" retry_count=\"%s\" retry_interval=\"%s\" /></upgrade>"),

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
