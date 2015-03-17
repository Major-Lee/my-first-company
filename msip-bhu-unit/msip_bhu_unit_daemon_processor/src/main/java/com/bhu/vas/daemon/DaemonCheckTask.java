package com.bhu.vas.daemon;

import java.util.Iterator;
import java.util.TimerTask;

import com.bhu.vas.daemon.observer.DaemonObserverManager;
import com.smartwork.msip.cores.helper.StringHelper;
import com.smartwork.msip.localunit.RandomData;

public class DaemonCheckTask extends TimerTask{
	//1. 查询当前在线终端，下发此命令后触发设备主动上报一次
	private String query_device_teminals_cmd_template = "00001001%s0000000000<param><ITEM wlan_user_notify=\"enable\" trap=\"disable\" wlan_user_sync=\"1\" /></param>";
	
	//1. 查询cpu,内存利用率
	private String query_device_status_cmd_template =   "00001001%s%s<cmd><ITEM index=\"1\" cmd=\"sysperf\"/></cmd>";
	
	
	//1. 查询wifi地理位置命令第一步
	private String query_device_location_step1_cmd_template = "00001001%s%s<cmd><ITEM cmd=\"sysdebug\" supercmd=\"wifiloc -a\" /></cmd>";
	
	//1. 查询wifi地理位置命令第二步
	private String query_device_location_step2_cmd_template = "<report><ITEM cmd=\"sysdebug\" serial=\"1\" op=\"get\"/></report>";
	
	public static final String TableSuffixTemplete = "%010d";
	@Override
	public void run() {
		System.out.println("DaemonCheckTask starting...");
    	Iterator<String> iter = SessionManager.getInstance().keySet().iterator();
    	while(iter.hasNext()){
    		String wifi_mac = iter.next();
    		String ctx = SessionManager.getInstance().getSession(wifi_mac);//.get(key);
    		DaemonObserverManager.CmdDownObserver.notifyCmdDown(ctx, wifi_mac, String.format(query_device_teminals_cmd_template, StringHelper.unformatMacAddress(wifi_mac)));
    		DaemonObserverManager.CmdDownObserver.notifyCmdDown(ctx, wifi_mac, String.format(query_device_status_cmd_template, StringHelper.unformatMacAddress(wifi_mac),String.format(TableSuffixTemplete, RandomData.intNumber(1005, 10000080))));
    		DaemonObserverManager.CmdDownObserver.notifyCmdDown(ctx, wifi_mac, String.format(query_device_location_step1_cmd_template, StringHelper.unformatMacAddress(wifi_mac),String.format(TableSuffixTemplete, RandomData.intNumber(1005, 10000080))));
    	}
    	System.out.println("DaemonCheckTask ended!");
	}
	
	public static void main(String[] argv){
		System.out.println(String.format(TableSuffixTemplete, RandomData.intNumber(1005, 10000080)));
		System.out.println(StringHelper.unformatMacAddress("34:36:3b:d0:4b:ac"));
	}
}
