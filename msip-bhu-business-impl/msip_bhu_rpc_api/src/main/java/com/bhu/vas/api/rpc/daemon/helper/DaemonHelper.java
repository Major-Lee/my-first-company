package com.bhu.vas.api.rpc.daemon.helper;

import java.util.ArrayList;
import java.util.List;

import com.bhu.vas.api.helper.CMDBuilder;
import com.bhu.vas.api.rpc.daemon.iservice.IDaemonRpcService;

public class DaemonHelper {
	public static void afterDeviceOnline(String mac,boolean needLocationQuery,IDaemonRpcService daemonRpcService){
		List<String> payloads = new ArrayList<String>();
		//DaemonObserverManager.CmdDownObserver.notifyCmdDown(info.getCtx(), info.getMac(), CMDBuilder.builderDeviceOnlineTeminalQuery(info.getMac()));
		//下发管理参数触发设备自动上报用户通知并同步终端
		payloads.add(CMDBuilder.builderDeviceOnlineTeminalQuery(mac));
		//获取配置指令
		payloads.add(CMDBuilder.builderDeviceSettingQuery(mac, CMDBuilder.device_setting_query_taskid_fragment.getNextSequence()));
		//获取设备测速
		deviceSpeedQuery(mac, daemonRpcService);
		//payloads.add(CMDBuilder.builderDeviceSpeedNotifyQuery(mac, CMDBuilder.device_speed_taskid_fragment.getNextSequence()));
		if(needLocationQuery){
			//获取地理位置
			payloads.add(CMDBuilder.builderDeviceLocationNotifyQuery(mac, CMDBuilder.location_taskid_fragment.getNextSequence()));
		}
		//获取地理位置
		//设备上行首先发送查询地理位置指令
//		if(needLocationQuery){
//			payloads.add(CMDBuilder.builderDeviceLocationStep1Query(mac, CMDBuilder.location_taskid_fragment.getNextSequence()));
//		}
		//WifiCmdNotifyDTO dto = JsonHelper.getDTO(message, WifiCmdNotifyDTO.class);
		daemonRpcService.wifiDeviceCmdsDown(null, mac, payloads);
	}
	
	public static void afterUserSignedon(String mac, List<String> vapnames, IDaemonRpcService daemonRpcService){
		//List<String> payloads = new ArrayList<String>();
		//获取设备测速
		//deviceSpeedQuery(mac, daemonRpcService);
		//payloads.add(CMDBuilder.builderDeviceSpeedNotifyQuery(mac, CMDBuilder.device_speed_taskid_fragment.getNextSequence()));
		//获取设备的终端列表
		deviceTerminalsQuery(mac, vapnames, daemonRpcService);
		//获取设备的实时速率
		deviceRateQuery(mac, daemonRpcService);
		
		//daemonRpcService.wifiDeviceCmdsDown(null, mac, payloads);
	}
	
	public static void daemonCmdDown(String mac,String cmd,IDaemonRpcService daemonRpcService){
		daemonRpcService.wifiDeviceCmdDown(null, mac, cmd);
	}
	
	public static void deviceTerminalsQuery(String mac,List<String> vapnames,IDaemonRpcService daemonRpcService){
		if(vapnames != null && !vapnames.isEmpty()){
			List<String> cmds = CMDBuilder.builderDeviceTerminalsQueryWithAutoTaskid(mac, vapnames);
			daemonRpcService.wifiDeviceCmdsDown(null, mac, cmds);
		}
	}
	//上报周期5秒一次
	public static final int DeviceRateQuery_Period = 5;
	//上报时长30分钟
	public static final int DeviceRateQuery_Duration = 1800;
	//wan口的实时速率
	public static final String Wan_Interface_Name = "wan";
	
	public static void deviceRateQuery(String mac,IDaemonRpcService daemonRpcService){
		deviceRateQuery(mac, Wan_Interface_Name, DeviceRateQuery_Period, DeviceRateQuery_Duration, daemonRpcService);
	}
	
	public static void deviceRateQuery(String mac,String interface_name,int period, int duration, 
			IDaemonRpcService daemonRpcService){
		String cmd = CMDBuilder.builderDeviceRateNotifyQuery(mac, CMDBuilder.device_rate_taskid_fragment.getNextSequence(), 
				interface_name, period, duration);
		daemonCmdDown(mac, cmd, daemonRpcService);
	}
	
	//设备测速时间10秒
	public static final int DeviceSpeedQuery_MaxTestTime = 10;
	
	public static void deviceSpeedQuery(String mac, IDaemonRpcService daemonRpcService){
		deviceSpeedQuery(mac, DeviceSpeedQuery_MaxTestTime, daemonRpcService);
	}
	
	public static void deviceSpeedQuery(String mac, int max_test_time, IDaemonRpcService daemonRpcService){
		String cmd = CMDBuilder.builderDeviceSpeedNotifyQuery(mac, CMDBuilder.device_speed_taskid_fragment.getNextSequence(), 
				max_test_time);
		daemonCmdDown(mac, cmd, daemonRpcService);
	}
	
	public static void deviceSettingModify(String mac, String paylod, IDaemonRpcService daemonRpcService){
		String cmd = CMDBuilder.builderDeviceSettingModify(mac, CMDBuilder.device_setting_modify_taskid_fragment.getNextSequence(),
				paylod);
		daemonCmdDown(mac, cmd, daemonRpcService);
	}
	
}
