package com.bhu.vas.api.rpc.devices.iservice;

import java.util.List;

import com.bhu.vas.api.dto.WifiDeviceDTO;
import com.bhu.vas.api.dto.header.ParserHeader;


public interface IDeviceMessageDispatchRpcService {
	public void messageDispatch(String ctx, String payload, ParserHeader parserHeader);
	
	public void wifiDeviceOnlines(String ctx, List<WifiDeviceDTO> dtos);
}
