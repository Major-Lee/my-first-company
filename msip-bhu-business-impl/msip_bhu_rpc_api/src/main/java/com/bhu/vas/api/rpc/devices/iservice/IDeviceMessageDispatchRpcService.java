package com.bhu.vas.api.rpc.devices.iservice;

import com.bhu.vas.api.dto.header.ParserHeader;


public interface IDeviceMessageDispatchRpcService {
	public void messageDispatch(String ctx, String payload, ParserHeader parserHeader);
}
