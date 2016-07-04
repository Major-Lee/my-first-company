package com.bhu.vas.daemon;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;

import com.bhu.vas.api.helper.CMDBuilder;
import com.bhu.vas.business.bucache.redis.serviceimpl.devices.WifiDevicePresentCtxService;
import com.bhu.vas.daemon.observer.DaemonObserverManager;
import com.smartwork.msip.cores.orm.iterator.IteratorNotify;
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
		//long current = System.currentTimeMillis();
		final AtomicInteger atomic = new AtomicInteger(0);
		WifiDevicePresentCtxService.getInstance().iteratorAll(new IteratorNotify<Map<String,String>>(){
			@Override
			public void notifyComming(Map<String, String> onlineMap) {
				//System.out.println("~~~~~~~~~~:"+onlineMap);
				Iterator<Entry<String, String>> iter = onlineMap.entrySet().iterator();
				while(iter.hasNext()){
					Entry<String, String> next = iter.next();
					String mac = next.getKey();//mac
					String ctx = next.getValue();//ctx
					DaemonObserverManager.CmdDownObserver.notifyCmdDown(ctx, mac, CMDBuilder.builderDeviceStatusQuery(mac, CMDBuilder.auto_taskid_fragment.getNextSequence()));
					DaemonObserverManager.CmdDownObserver.notifyCmdDown(ctx, mac, CMDBuilder.builderDeviceFlowQuery(mac, CMDBuilder.auto_taskid_fragment.getNextSequence()));
					//SessionManager.getInstance().addSession(key, value);
					atomic.incrementAndGet();
					//System.out.println(String.format("Online device[%s] ctx[%s]", key,value));
				}
				//System.out.println(t);
			}
		});
		
		/*Map<String, SessionInfo>[] sessionInfoCaches = SessionManager.getInstance().sessionInfoCaches();
		for (Map<String, SessionInfo> cache : sessionInfoCaches) {
			Iterator<Entry<String, SessionInfo>> iter = cache.entrySet().iterator();
			while(iter.hasNext()){
				total++;
				Entry<String, SessionInfo> next = iter.next();
				SessionInfo info = next.getValue();
				if(info != null){
					if(info.canBeExecute(current)){
						
						//DaemonObserverManager.CmdDownObserver.notifyCmdDown(info.getCtx(), info.getMac(), CMDBuilder.builderDeviceOnlineTeminalQuery(info.getMac()));
						//String.format(query_device_teminals_cmd_template, StringHelper.unformatMacAddress(wifi_mac)));//,String.format(String.format(SuffixTemplete, RandomData.intNumber(1005, 10000080)), RandomData.intNumber(1005, 10000080))));
						DaemonObserverManager.CmdDownObserver.notifyCmdDown(info.getCtx(), info.getMac(), CMDBuilder.builderDeviceStatusQuery(info.getMac(), CMDBuilder.auto_taskid_fragment.getNextSequence()));
						DaemonObserverManager.CmdDownObserver.notifyCmdDown(info.getCtx(), info.getMac(), CMDBuilder.builderDeviceFlowQuery(info.getMac(), CMDBuilder.auto_taskid_fragment.getNextSequence()));
//						DaemonObserverManager.CmdDownObserver.notifyCmdDown(info.getCtx(), info.getMac(), CMDBuilder.builderDeviceLocationNotifyQuery(info.getMac(), CMDBuilder.location_taskid_fragment.getNextSequence()));
						
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
		}*/
		System.out.println(String.format("DaemonCheckTask ended! Total[%s] Sended[%s] UnSended[%s] removed[%s]",total,sended,unsended,removed));
	}
	
	public static void main(String[] argv){
		//System.out.println(String.format(SuffixTemplete, RandomData.intNumber(1005, 10000080)));
		//System.out.println(StringHelper.unformatMacAddress("34:36:3b:d0:4b:ac"));
	}
}
