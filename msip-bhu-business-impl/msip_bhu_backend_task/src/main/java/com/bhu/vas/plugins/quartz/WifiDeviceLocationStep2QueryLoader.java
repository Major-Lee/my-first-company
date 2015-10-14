package com.bhu.vas.plugins.quartz;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bhu.vas.api.dto.redis.SerialTaskDTO;
import com.bhu.vas.api.rpc.daemon.helper.DaemonHelper;
import com.bhu.vas.api.rpc.daemon.iservice.IDaemonRpcService;
import com.bhu.vas.business.bucache.redis.serviceimpl.devices.WifiDeviceLocationSerialTaskService;
import com.smartwork.msip.cores.orm.iterator.IteratorNotify;

/**
 * 触发老设备的地理位置第二次查询
 * @author Edmond Lee
 *
 */
public class WifiDeviceLocationStep2QueryLoader {
	private static Logger logger = LoggerFactory.getLogger(WifiDeviceLocationStep2QueryLoader.class);
	
	@Resource
	private IDaemonRpcService daemonRpcService;
	public void execute() {
		logger.info("WifiDeviceLocationStep2QueryLoader starting...");
		WifiDeviceLocationSerialTaskService.getInstance().iteratorAll(new IteratorNotify<Map<String,SerialTaskDTO>>() {
			@Override
			public void notifyComming(Map<String, SerialTaskDTO> result) {
				Set<Entry<String, SerialTaskDTO>> entrySet = result.entrySet();
				for(Entry<String, SerialTaskDTO> entry:entrySet){
					String mac = entry.getKey();
					SerialTaskDTO taskDto = entry.getValue();
					DaemonHelper.locationStep2Query(mac, taskDto.getTaskid(), taskDto.getSerialno(), daemonRpcService);//.locationQuery(device.getId(), daemonRpcService);
					WifiDeviceLocationSerialTaskService.getInstance().removeSerialTask(mac);
					logger.info("WifiDeviceLocationStep2QueryLoader mac:"+mac);
				}
			}
		});
		logger.info("WifiDeviceLocationStep2QueryLoader end");
	}
}
