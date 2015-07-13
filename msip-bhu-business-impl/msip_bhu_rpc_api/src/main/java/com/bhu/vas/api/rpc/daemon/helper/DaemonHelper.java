package com.bhu.vas.api.rpc.daemon.helper;

import java.util.ArrayList;
import java.util.List;

import com.bhu.vas.api.dto.ret.param.ParamWifisinfferDTO;
import com.bhu.vas.api.helper.CMDBuilder;
import com.bhu.vas.api.helper.OperationCMD;
import com.bhu.vas.api.rpc.daemon.iservice.IDaemonRpcService;

public class DaemonHelper {
	public static void afterDeviceOnline(String mac, boolean needLocationQuery,boolean needWiffsniffer,
			IDaemonRpcService daemonRpcService){
		List<String> payloads = new ArrayList<String>();
		//DaemonObserverManager.CmdDownObserver.notifyCmdDown(info.getCtx(), info.getMac(), CMDBuilder.builderDeviceOnlineTeminalQuery(info.getMac()));
		//下发管理参数触发设备自动上报用户通知并同步终端
		payloads.add(CMDBuilder.builderDeviceOnlineTeminalQuery(mac));
		//获取配置指令
		payloads.add(CMDBuilder.builderDeviceSettingQuery(mac, CMDBuilder.device_setting_query_taskid_fragment.getNextSequence()));
		//获取设备测速
		//deviceSpeedQuery(mac, daemonRpcService);
		//payloads.add(CMDBuilder.builderDeviceSpeedNotifyQuery(mac, CMDBuilder.device_speed_taskid_fragment.getNextSequence()));
		if(needLocationQuery){
			//获取地理位置
			payloads.add(CMDBuilder.builderDeviceLocationNotifyQuery(mac, CMDBuilder.location_taskid_fragment.getNextSequence()));
		}
		//开启或关闭wiffsinffer
		//payloads.add(CMDBuilder.builderDeviceWifiSnifferSetting(mac, CMDBuilder.builderDeviceWifiSnifferSetting(mac,needWiffsniffer?ParamWifisinfferDTO.Start_Sta_Sniffer:ParamWifisinfferDTO.Stop_Sta_Sniffer)));
		if(needWiffsniffer){
			//开启wiffsinffer
			//String CMDBuilder.builderDeviceWifiSnifferSetting(wifiId,on?ParamWifisinfferDTO.Start_Sta_Sniffer:ParamWifisinfferDTO.Stop_Sta_Sniffer)
			payloads.add(CMDBuilder.builderDeviceWifiSnifferSetting(mac, ParamWifisinfferDTO.Start_Sta_Sniffer));
			//payloads.add(CMDBuilder.builderDeviceWifiSnifferSetting(mac, CMDBuilder.builderDeviceWifiSnifferSetting(mac,needWiffsniffer?ParamWifisinfferDTO.Start_Sta_Sniffer:ParamWifisinfferDTO.Stop_Sta_Sniffer)));
		}
		
//		if(StringUtils.isNotEmpty(dhcpcStatusQuery_interface)){
//			//如果是dhcpc模式 获取状态信息
//			payloads.add(CMDBuilder.builderDhcpcStatusQuery(mac, CMDBuilder.device_dhcpc_status_fragment.getNextSequence(),
//					dhcpcStatusQuery_interface));
//		}
		//获取地理位置
		//设备上行首先发送查询地理位置指令
//		if(needLocationQuery){
//			payloads.add(CMDBuilder.builderDeviceLocationStep1Query(mac, CMDBuilder.location_taskid_fragment.getNextSequence()));
//		}
		//WifiCmdNotifyDTO dto = JsonHelper.getDTO(message, WifiCmdNotifyDTO.class);
		//wifi定时开关 移到用户登录后给其绑定设备下发
		//payloads.add(CMDBuilder.builderCMD4Opt(OperationCMD.DeviceWifiTimerQuery.getNo(), mac, CMDBuilder.device_wifitimer_fragment.getNextSequence(), null));
		daemonRpcService.wifiDeviceCmdsDown(null, mac, payloads);
	}
	
	public static void locationStep1Query(String mac,IDaemonRpcService daemonRpcService){
		String cmd = CMDBuilder.builderDeviceLocationNotifyQuery(mac, CMDBuilder.location_taskid_fragment.getNextSequence());
		daemonRpcService.wifiDeviceCmdDown(null, mac, cmd);
	}
	
	public static void locationStep2Query(String mac,int taskid,String serialno,IDaemonRpcService daemonRpcService){
		String cmd = CMDBuilder.builderDeviceLocationStep2Query(mac, taskid, serialno);
		//String cmd = CMDBuilder.builderDeviceLocationNotifyQuery(mac, CMDBuilder.location_taskid_fragment.getNextSequence());
		daemonRpcService.wifiDeviceCmdDown(null, mac, cmd);
	}
	
	public static void afterUserSignedon(String mac,boolean needDeviceUsedQuery, IDaemonRpcService daemonRpcService){
		List<String> payloads = new ArrayList<String>();
		//用户登录后 给其绑定的设备mac地址发送设备使用情况
		if(needDeviceUsedQuery)
			payloads.add(CMDBuilder.builderDeviceUsedStatusQuery(mac));//(mac, CMDBuilder.device_speed_taskid_fragment.getNextSequence()));
		
		payloads.add(CMDBuilder.builderCMD4Opt(OperationCMD.DeviceWifiTimerQuery.getNo(), mac, CMDBuilder.device_wifitimer_fragment.getNextSequence(), null));
		//可能需要用户登录后根据其个人绑定的设备，下发配置开启wifi探测
		/*if(needWiffsniffer){
			//开启wiffsinffer
			CMDBuilder.builderDeviceWifiSnifferSetting(mac,on?ParamWifisinfferDTO.Start_Sta_Sniffer:ParamWifisinfferDTO.Stop_Sta_Sniffer)
		}*/
		
		//获取设备测速
		//deviceSpeedQuery(mac, daemonRpcService);
		//payloads.add(CMDBuilder.builderDeviceSpeedNotifyQuery(mac, CMDBuilder.device_speed_taskid_fragment.getNextSequence()));
		//获取设备的终端列表
		//deviceTerminalsRateQuery(mac, daemonRpcService);
		//获取设备的实时速率
		//deviceRateQuery(mac, daemonRpcService);
		if(!payloads.isEmpty())
			daemonRpcService.wifiDeviceCmdsDown(null, mac, payloads);
	}
	
	public static void daemonCmdDown(String mac,String cmd,IDaemonRpcService daemonRpcService){
		daemonRpcService.wifiDeviceCmdDown(null, mac, cmd);
	}
	
//	public static void deviceTerminalsQuery(String mac,List<String> vapnames,IDaemonRpcService daemonRpcService){
//		if(vapnames != null && !vapnames.isEmpty()){
//			List<String> cmds = CMDBuilder.builderDeviceTerminalsQueryWithAutoTaskid(mac, vapnames);
//			daemonRpcService.wifiDeviceCmdsDown(null, mac, cmds);
//		}
//	}
	
	//上报周期10秒一次
	public static final int DeviceRateQuery_Period = 5;
	//上报时长5分钟
	public static final int DeviceRateQuery_Duration = 300;
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
	
	public static void deviceDhcpcStatusQuery(String mac, String interface_name, IDaemonRpcService daemonRpcService){
		String cmd = CMDBuilder.builderDhcpcStatusQuery(mac, CMDBuilder.device_dhcpc_status_fragment.getNextSequence(), 
				interface_name);
		daemonCmdDown(mac, cmd, daemonRpcService);
	}
	
	public static void deviceTerminalsRateQuery(String mac,IDaemonRpcService daemonRpcService){
		deviceTerminalsRateQuery(mac, DeviceRateQuery_Period, DeviceRateQuery_Duration, daemonRpcService);
	}
	
	public static void deviceTerminalsRateQuery(String mac,int period, int duration, IDaemonRpcService daemonRpcService){
		String cmd = CMDBuilder.builderDeviceTerminalsQuery(mac, CMDBuilder.device_terminals_taskid_fragment.getNextSequence(), 
				period, duration);
		daemonCmdDown(mac, cmd, daemonRpcService);
	}
	
	//设备测速时间15秒
	public static final int DeviceSpeedQuery_MaxTestTime = 15;
	
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
