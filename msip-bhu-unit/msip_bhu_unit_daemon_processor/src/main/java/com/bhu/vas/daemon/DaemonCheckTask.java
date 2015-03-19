package com.bhu.vas.daemon;

import java.util.Iterator;
import java.util.TimerTask;

import com.bhu.vas.api.helper.CMDBuilder;
import com.bhu.vas.daemon.observer.DaemonObserverManager;
import com.smartwork.msip.localunit.RandomData;
/**
 * 上端下发消息：
	1001. 任务下发：消息体格式12字节mac地址，后接10字节任务id。
                    设备报文主类型(4字节)，子类型(8字节)
                    后接xml格式的任务字符串.
                    任务id为0，表明任务不需要回复。
 * <param>开头的那个报文，这两个数字是0000和00000006；// 但先改成0000和00000000，为了调试
 * <cmd>之类的，是0001和00000001
 * @author Edmond
 *
 */
public class DaemonCheckTask extends TimerTask{
	@Override
	public void run() {
		System.out.println("DaemonCheckTask starting...");
    	Iterator<String> iter = SessionManager.getInstance().keySet().iterator();
    	int i=0;
    	while(iter.hasNext()){
    		String wifi_mac = iter.next();
    		String ctx = SessionManager.getInstance().getSession(wifi_mac);//.get(key);
    		DaemonObserverManager.CmdDownObserver.notifyCmdDown(ctx, wifi_mac, CMDBuilder.builderDeviceOnlineTeminalQuery(wifi_mac));
    				//String.format(query_device_teminals_cmd_template, StringHelper.unformatMacAddress(wifi_mac)));//,String.format(String.format(SuffixTemplete, RandomData.intNumber(1005, 10000080)), RandomData.intNumber(1005, 10000080))));
    		DaemonObserverManager.CmdDownObserver.notifyCmdDown(ctx, wifi_mac, CMDBuilder.builderDeviceStatusQuery(wifi_mac, RandomData.intNumber(1, 100000)));
    				//String.format(query_device_status_cmd_template, StringHelper.unformatMacAddress(wifi_mac),String.format(SuffixTemplete, RandomData.intNumber(1005, 10000080))));//, RandomData.intNumber(1005, 10000080))));
    		//DaemonObserverManager.CmdDownObserver.notifyCmdDown(ctx, wifi_mac, 
    		//		String.format(query_device_location_step1_cmd_template, StringHelper.unformatMacAddress(wifi_mac),String.format(SuffixTemplete, RandomData.intNumber(1005, 10000080))));//, RandomData.intNumber(1005, 10000080))));
    		i++;
    	}
    	System.out.println("DaemonCheckTask ended! ["+i+"] devices");
	}
	
	public static void main(String[] argv){
		//System.out.println(String.format(SuffixTemplete, RandomData.intNumber(1005, 10000080)));
		//System.out.println(StringHelper.unformatMacAddress("34:36:3b:d0:4b:ac"));
	}
}
