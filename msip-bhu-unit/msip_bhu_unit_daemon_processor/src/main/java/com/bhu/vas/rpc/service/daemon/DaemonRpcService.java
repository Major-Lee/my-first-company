package com.bhu.vas.rpc.service.daemon;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.bhu.vas.api.dto.CmInfo;
import com.bhu.vas.api.dto.WifiDeviceContextDTO;
import com.bhu.vas.api.rpc.daemon.iservice.IDaemonRpcService;

/**
 * 去除掉token存储在db中？只使用redis会比较好？
 * @author Edmond
 *
 */
@Service("daemonRpcService")
public class DaemonRpcService implements IDaemonRpcService {
	private final Logger logger = LoggerFactory.getLogger(DaemonRpcService.class);

	@Override
	public boolean wifiDeviceOnline(WifiDeviceContextDTO dto) {
		System.out.println("wifiDeviceOnline:"+dto);
		return false;
	}
	
	@Override
	public boolean wifiDeviceOffline(WifiDeviceContextDTO dto) {
		System.out.println("wifiDeviceOffline:"+dto);
		return false;
	}

	@Override
	public boolean wifiDeviceCmdDown(WifiDeviceContextDTO dto, String cmd) {
		System.out.println("wifiDeviceCmdDown:"+dto+" cmd:"+cmd);
		return false;
	}

	@Override
	public boolean cmJoinService(CmInfo info) {
		System.out.println("cmJoinService:"+info);
		return false;
	}

	@Override
	public boolean cmLeave(CmInfo info) {
		System.out.println("cmLeave:"+info);
		return false;
	}
}
