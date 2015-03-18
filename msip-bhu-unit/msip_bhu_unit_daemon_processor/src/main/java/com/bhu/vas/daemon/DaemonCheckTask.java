package com.bhu.vas.daemon;

import java.util.Iterator;
import java.util.TimerTask;

import com.bhu.vas.daemon.observer.DaemonObserverManager;
import com.smartwork.msip.cores.helper.StringHelper;
import com.smartwork.msip.localunit.RandomData;
/**
 * 上端下发消息：
	1001. 任务下发：消息体格式12字节mac地址，后接10字节任务id。
                    设备报文主类型(4字节)，子类型(8字节)
                    后接xml格式的任务字符串.
                    任务id为0，表明任务不需要回复。
 * <param>开头的那个报文，这两个数字是0000和00000006 但先改成0000和00000000，为了调试
 * <cmd>之类的，是0001和00000001
 * @author Edmond
 *
 */
public class DaemonCheckTask extends TimerTask{
	//1. 查询当前在线终端，下发此命令后触发设备主动上报一次
	private String query_device_teminals_cmd_template = "00001001%s%s"+"000000000000"+"<param><ITEM wlan_user_notify=\"enable\" trap=\"disable\" wlan_user_sync=\"1\" /></param>";
	
	//1. 查询cpu,内存利用率
	private String query_device_status_cmd_template =   "00001001%s%s"+"000100000001"+"<cmd><ITEM index=\"1\" cmd=\"sysperf\"/></cmd>";
	
	
	//1. 查询wifi地理位置命令第一步
	private String query_device_location_step1_cmd_template = "00001001%s%s"+"000100000001"+"<cmd><ITEM cmd=\"sysdebug\" supercmd=\"wifiloc -a\" /></cmd>";
	
	//1. 查询wifi地理位置命令第二步
	private String query_device_location_step2_cmd_template = "<report><ITEM cmd=\"sysdebug\" serial=\"1\" op=\"get\"/></report>";
	
	public static final String SuffixTemplete = "%010d";
	@Override
	public void run() {
		System.out.println("DaemonCheckTask starting...");
    	Iterator<String> iter = SessionManager.getInstance().keySet().iterator();
    	while(iter.hasNext()){
    		String wifi_mac = iter.next();
    		String ctx = SessionManager.getInstance().getSession(wifi_mac);//.get(key);
    		DaemonObserverManager.CmdDownObserver.notifyCmdDown(ctx, wifi_mac, String.format(query_device_teminals_cmd_template, StringHelper.unformatMacAddress(wifi_mac),String.format(String.format(SuffixTemplete, RandomData.intNumber(1005, 10000080)), RandomData.intNumber(1005, 10000080))));
    		DaemonObserverManager.CmdDownObserver.notifyCmdDown(ctx, wifi_mac, String.format(query_device_status_cmd_template, StringHelper.unformatMacAddress(wifi_mac),String.format(String.format(SuffixTemplete, RandomData.intNumber(1005, 10000080)), RandomData.intNumber(1005, 10000080))));
    		DaemonObserverManager.CmdDownObserver.notifyCmdDown(ctx, wifi_mac, String.format(query_device_location_step1_cmd_template, StringHelper.unformatMacAddress(wifi_mac),String.format(String.format(SuffixTemplete, RandomData.intNumber(1005, 10000080)), RandomData.intNumber(1005, 10000080))));
    	}
    	System.out.println("DaemonCheckTask ended!");
	}
	
	public static void main(String[] argv){
		System.out.println(String.format(SuffixTemplete, RandomData.intNumber(1005, 10000080)));
		System.out.println(StringHelper.unformatMacAddress("34:36:3b:d0:4b:ac"));
	}
}
