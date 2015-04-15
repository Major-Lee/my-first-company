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
		payloads.add(CMDBuilder.builderDeviceSpeedQuery(mac, CMDBuilder.device_setting_taskid_fragment.getNextSequence()));
		//获取地理位置
		//设备上行首先发送查询地理位置指令
		payloads.add(CMDBuilder.builderDeviceLocationStep1Query(mac, CMDBuilder.location_taskid_fragment.getNextSequence()));
		//WifiCmdNotifyDTO dto = JsonHelper.getDTO(message, WifiCmdNotifyDTO.class);
		daemonRpcService.wifiDeviceCmdsDown(null, mac, payloads);
	}
}
