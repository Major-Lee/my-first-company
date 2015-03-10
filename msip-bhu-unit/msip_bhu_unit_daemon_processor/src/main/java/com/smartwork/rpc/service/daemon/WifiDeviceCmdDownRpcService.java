package com.smartwork.rpc.service.daemon;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.bhu.vas.api.rpc.daemon.iservice.IWifiDeviceCmdDownRpcService;
import com.bhu.vas.api.rpc.devices.dto.WifiDeviceContextDTO;

/**
 * 去除掉token存储在db中？只使用redis会比较好？
 * @author Edmond
 *
 */
@Service("wifiDeviceCmdDownRpcService")
public class WifiDeviceCmdDownRpcService implements IWifiDeviceCmdDownRpcService {
	private final Logger logger = LoggerFactory.getLogger(WifiDeviceCmdDownRpcService.class);

	@Override
	public boolean wifiDeviceRegister(WifiDeviceContextDTO dto) {
		System.out.println("wifiDeviceRegister:"+dto);
		return false;
	}

	@Override
	public boolean wifiDeviceCmdDown(WifiDeviceContextDTO dto, String cmd) {
		System.out.println("wifiDeviceCmdDown:"+dto+" cmd:"+cmd);
		return false;
	}
}
