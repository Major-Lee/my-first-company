package com.bhu.vas.api.rpc.daemon.helper;

import java.util.ArrayList;
import java.util.List;

import com.bhu.vas.api.helper.CMDBuilder;
import com.bhu.vas.api.rpc.daemon.iservice.IDaemonRpcService;

public class DaemonHelper {
	public static void afterDeviceOnline(String mac,IDaemonRpcService daemonRpcService){
		List<String> payloads = new ArrayList<String>();
		//获取配置指令
		payloads.add(CMDBuilder.builderDeviceSettingQuery(mac, CMDBuilder.device_setting_taskid_fragment.getNextSequence()));
		//获取设备测速
		payloads.add(CMDBuilder.builderDeviceSpeedNotifyQuery(mac, CMDBuilder.device_speed_taskid_fragment.getNextSequence()));
		//获取地理位置
		//设备上行首先发送查询地理位置指令
		//payloads.add(CMDBuilder.builderDeviceLocationStep1Query(mac, CMDBuilder.location_taskid_fragment.getNextSequence()));
		payloads.add(CMDBuilder.builderDeviceLocationNotifyQuery(mac, CMDBuilder.location_taskid_fragment.getNextSequence()));
		//WifiCmdNotifyDTO dto = JsonHelper.getDTO(message, WifiCmdNotifyDTO.class);
		daemonRpcService.wifiDeviceCmdsDown(null, mac, payloads);
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
}
