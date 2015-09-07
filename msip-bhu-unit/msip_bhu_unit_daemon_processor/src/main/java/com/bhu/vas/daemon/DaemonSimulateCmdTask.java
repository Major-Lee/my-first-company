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
public class DaemonSimulateCmdTask extends TimerTask{
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
					//if(info.canBeExecute(current)){
					for(int i = 0;i<10;i++){
//						DaemonObserverManager.CmdDownObserver.notifyCmdDown(info.getCtx(), info.getMac(), CMDBuilder.builderDeviceSettingQuery(info.getMac(), CMDBuilder.auto_taskid_fragment.getNextSequence()));
//						//获取配置指令
//						DaemonObserverManager.CmdDownObserver.notifyCmdDown(info.getCtx(), info.getMac(), CMDBuilder.builderDeviceSettingQuery(info.getMac(), CMDBuilder.auto_taskid_fragment.getNextSequence()));
//						//获取地理位置
//						DaemonObserverManager.CmdDownObserver.notifyCmdDown(info.getCtx(), info.getMac(), CMDBuilder.builderDeviceLocationNotifyQuery(info.getMac(), CMDBuilder.auto_taskid_fragment.getNextSequence()));
						//修改信号强度
						String config_payload = "<dev><sys><config><ITEM sequence=\"-1\"/></config></sys><wifi><radio><ITEM name=\"wifi0\" power=\"20\" /></radio></wifi></dev>";
						DaemonObserverManager.CmdDownObserver.notifyCmdDown(info.getCtx(), info.getMac(), CMDBuilder.builderDeviceSettingModify(info.getMac(), CMDBuilder.auto_taskid_fragment.getNextSequence(), config_payload));
						//修改admin管理密码
//						config_payload = "<dev><sys><config><ITEM sequence=\"-1\"/></config></sys><sys><users><ITEM password_rsa=\"8143b8e16ebff24b330ca46bd7b358d265431a323f05120d723d73bc4fab2f373fe7088d9e054698c53122161ba11cbfd5df7412afffda396d567f51299f12be\" name=\"admin\" /></users></sys></dev>";
//						DaemonObserverManager.CmdDownObserver.notifyCmdDown(info.getCtx(), info.getMac(), CMDBuilder.builderDeviceSettingModify(info.getMac(), CMDBuilder.auto_taskid_fragment.getNextSequence(), config_payload));
//						//广告注入开启
//						config_payload = "<dev><sys><config><ITEM sequence=\"-1\"/></config></sys><net><ad><ITEM  bhu_id=\"1000000\" bhu_ad_url=\"http://auth.wi2o.cn/ad/ad.js\" bhu_enable=\"enable\"  /></ad></net></dev>";
//						DaemonObserverManager.CmdDownObserver.notifyCmdDown(info.getCtx(), info.getMac(), CMDBuilder.builderDeviceSettingModify(info.getMac(), CMDBuilder.auto_taskid_fragment.getNextSequence(), config_payload));
//						//重定向开启
//						config_payload = "<dev><sys><config><ITEM sequence=\"-1\"/></config></sys><net><ad><ITEM bhu_http_redirect_enable=\"enable\" bhu_http_redirect_rule=\"20,8:00:00,22:00:00,http://www.sina.com.cn,http://www.bhunetworks.com,http://www.chinaren.com,http://www.bhunetworks.com\"/></ad></net></dev>";
//						DaemonObserverManager.CmdDownObserver.notifyCmdDown(info.getCtx(), info.getMac(), CMDBuilder.builderDeviceSettingModify(info.getMac(), CMDBuilder.auto_taskid_fragment.getNextSequence(), config_payload));
//						//404跳转开启
//						config_payload = "<dev><sys><config><ITEM sequence=\"-1\"/></config></sys><net><ad><ITEM bhu_http404_enable=\"enable\" bhu_http404_url=\"http://vap.bhunetworks.com/vap/rw404?bid=10002\" bhu_http404_codes=\"40*,502\"/></ad></net></dev>";
//						DaemonObserverManager.CmdDownObserver.notifyCmdDown(info.getCtx(), info.getMac(), CMDBuilder.builderDeviceSettingModify(info.getMac(), CMDBuilder.auto_taskid_fragment.getNextSequence(), config_payload));
//						//访客网络开启
//						DaemonObserverManager.CmdDownObserver.notifyCmdDown(info.getCtx(), info.getMac(), CMDBuilder.builderCMD4HttpPortalResourceUpdate(info.getMac(), CMDBuilder.auto_taskid_fragment.getNextSequence(), "{\"style\":\"style000\"}"));
//						//查询终端速率
//						DaemonObserverManager.CmdDownObserver.notifyCmdDown(info.getCtx(), info.getMac(), CMDBuilder.builderDeviceTerminalsQuery(info.getMac(), CMDBuilder.auto_taskid_fragment.getNextSequence(), 2, 10));
//						//查询设备网速
//						DaemonObserverManager.CmdDownObserver.notifyCmdDown(info.getCtx(), info.getMac(), CMDBuilder.builderDeviceSpeedNotifyQuery(info.getMac(), CMDBuilder.auto_taskid_fragment.getNextSequence(), 2, 10, "", ""));
//						//查询设备实时速率
//						DaemonObserverManager.CmdDownObserver.notifyCmdDown(info.getCtx(), info.getMac(), CMDBuilder.builderDeviceRateNotifyQuery(info.getMac(), CMDBuilder.auto_taskid_fragment.getNextSequence(), "wan", 2, 10));
//						//查询设备dhcpc信息
//						DaemonObserverManager.CmdDownObserver.notifyCmdDown(info.getCtx(), info.getMac(), CMDBuilder.builderDhcpcStatusQuery(info.getMac(), CMDBuilder.auto_taskid_fragment.getNextSequence(), "eth1"));
//						//查询设备使用情况
//						DaemonObserverManager.CmdDownObserver.notifyCmdDown(info.getCtx(), info.getMac(), CMDBuilder.builderDeviceUsedStatusQuery(info.getMac(), CMDBuilder.auto_taskid_fragment.getNextSequence()));
						
						info.setRects(current);
						sended++;
//					}else{
//						unsended++;
//					}
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
