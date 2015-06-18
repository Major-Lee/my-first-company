package com.bhu.vas.plugins.quartz;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bhu.vas.api.rpc.daemon.iservice.IDaemonRpcService;
/**
 * 每30分钟执行一次
 * 所有的在线设备进行索引增量,主要用于更新wifi设备的在线移动设备数
 * 如果在线设备存在经纬度，但是没有获取详细地址，也会进行获取
 * @author tangzichao
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
