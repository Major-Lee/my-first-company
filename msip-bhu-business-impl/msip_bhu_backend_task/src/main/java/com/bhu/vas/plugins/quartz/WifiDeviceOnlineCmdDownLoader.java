package com.bhu.vas.plugins.quartz;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bhu.vas.api.rpc.daemon.iservice.IDaemonRpcService;
/**
 * 用于触发对于在线设备需要发送的指令
 * @author Edmond Lee
 *
 */
public class WifiDeviceOnlineCmdDownLoader {
	private static Logger logger = LoggerFactory.getLogger(WifiDeviceOnlineCmdDownLoader.class);
	
	@Resource
	private IDaemonRpcService daemonRpcService;
	
	public void execute() {
		logger.info("WifiDeviceOnlineCmdDownLoader starting...");
		boolean result = daemonRpcService.wifiDevicesOnlineTimer();
		logger.info("WifiDeviceOnlineCmdDownLoader end...:"+result);
	}
	
}
