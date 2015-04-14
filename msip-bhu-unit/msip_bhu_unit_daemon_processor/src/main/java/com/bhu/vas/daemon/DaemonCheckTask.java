package com.bhu.vas.daemon;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TimerTask;

import com.bhu.vas.api.helper.CMDBuilder;
import com.bhu.vas.daemon.observer.DaemonObserverManager;
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
		int total=0;
    	int unsended = 0;
    	int sended = 0;
    	int removed = 0;
		long current = System.currentTimeMillis();
		Map<String, SessionInfo>[] sessionInfoCaches = SessionManager.getInstance().sessionInfoCaches();
		for (Map<String, SessionInfo> cache : sessionInfoCaches) {
			Iterator<Entry<String, SessionInfo>> iter = cache.entrySet().iterator();
			while(iter.hasNext()){
				total++;
				Entry<String, SessionInfo> next = iter.next();
				SessionInfo info = next.getValue();
				if(info != null){
					if(info.canBeExecute(current)){
						DaemonObserverManager.CmdDownObserver.notifyCmdDown(info.getCtx(), info.getMac(), CMDBuilder.builderDeviceOnlineTeminalQuery(info.getMac()));
						//String.format(query_device_teminals_cmd_template, StringHelper.unformatMacAddress(wifi_mac)));//,String.format(String.format(SuffixTemplete, RandomData.intNumber(1005, 10000080)), RandomData.intNumber(1005, 10000080))));
						DaemonObserverManager.CmdDownObserver.notifyCmdDown(info.getCtx(), info.getMac(), CMDBuilder.builderDeviceStatusQuery(info.getMac(), CMDBuilder.timer_device_status_taskid_fragment.getNextSequence()));
						DaemonObserverManager.CmdDownObserver.notifyCmdDown(info.getCtx(), info.getMac(), CMDBuilder.builderDeviceFlowQuery(info.getMac(), CMDBuilder.timer_device_flow_taskid_fragment.getNextSequence()));
						//DaemonObserverManager.CmdDownObserver.notifyCmdDown(info.getCtx(), info.getMac(), CMDBuilder.builderDeviceSettingQuery(info.getMac(), CMDBuilder.device_setting_taskid_fragment.getNextSequence()));
						//DaemonObserverManager.CmdDownObserver.notifyCmdDown(info.getCtx(), info.getMac(), CMDBuilder.builderDeviceSpeedQuery(info.getMac(), CMDBuilder.device_speed_taskid_fragment.getNextSequence()));
						info.setRects(current);
						sended++;
					}else{
						unsended++;
					}
				}else{
					iter.remove();
					removed++;
					continue;
				}
			}
		}
		System.out.println(String.format("DaemonCheckTask ended! Total[%s] Sended[%s] UnSended[%s] removed[%s]",total,sended,unsended,removed));
		/*for (Map<String, T> cache : caches) {
			values.addAll(cache.values());
		}
		
    	Iterator<String> iter = SessionManager.getInstance().keySet().iterator();
    	int i=0;
    	while(iter.hasNext()){
    		String wifi_mac = iter.next();
    		String ctx = SessionManager.getInstance().getSession(wifi_mac);//.get(key);
    		DaemonObserverManager.CmdDownObserver.notifyCmdDown(ctx, wifi_mac, CMDBuilder.builderDeviceOnlineTeminalQuery(wifi_mac));
    				//String.format(query_device_teminals_cmd_template, StringHelper.unformatMacAddress(wifi_mac)));//,String.format(String.format(SuffixTemplete, RandomData.intNumber(1005, 10000080)), RandomData.intNumber(1005, 10000080))));
    		DaemonObserverManager.CmdDownObserver.notifyCmdDown(ctx, wifi_mac, CMDBuilder.builderDeviceStatusQuery(wifi_mac, CMDBuilder.timer_device_status_taskid_fragment.getNextSequence()));
    		DaemonObserverManager.CmdDownObserver.notifyCmdDown(ctx, wifi_mac, CMDBuilder.builderDeviceFlowQuery(wifi_mac, CMDBuilder.timer_device_flow_taskid_fragment.getNextSequence()));
    				//String.format(query_device_status_cmd_template, StringHelper.unformatMacAddress(wifi_mac),String.format(SuffixTemplete, RandomData.intNumber(1005, 10000080))));//, RandomData.intNumber(1005, 10000080))));
    		//DaemonObserverManager.CmdDownObserver.notifyCmdDown(ctx, wifi_mac, 
    		//		String.format(query_device_location_step1_cmd_template, StringHelper.unformatMacAddress(wifi_mac),String.format(SuffixTemplete, RandomData.intNumber(1005, 10000080))));//, RandomData.intNumber(1005, 10000080))));
    		i++;
    	}*/
	}
	
	public static void main(String[] argv){
		//System.out.println(String.format(SuffixTemplete, RandomData.intNumber(1005, 10000080)));
		//System.out.println(StringHelper.unformatMacAddress("34:36:3b:d0:4b:ac"));
	}
}
