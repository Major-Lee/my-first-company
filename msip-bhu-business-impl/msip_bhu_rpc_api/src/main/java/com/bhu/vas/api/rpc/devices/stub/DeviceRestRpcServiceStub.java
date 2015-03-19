package com.bhu.vas.api.rpc.devices.stub;

import java.util.List;

import com.bhu.vas.api.rpc.devices.iservice.IDeviceRestRpcService;
import com.bhu.vas.api.rpc.devices.model.WifiDevice;
import com.bhu.vas.api.vto.WifiDeviceMaxBusyVTO;
import com.bhu.vas.api.vto.WifiDeviceVTO;
import com.smartwork.msip.cores.orm.support.page.TailPage;
import com.smartwork.msip.exception.RpcBusinessI18nCodeException;
import com.smartwork.msip.jdo.ResponseErrorCode;

public class DeviceRestRpcServiceStub implements IDeviceRestRpcService{
	
	private final IDeviceRestRpcService deviceRestRpcService;
	
    // 构造函数传入真正的远程代理对象
    public DeviceRestRpcServiceStub(IDeviceRestRpcService deviceRestRpcService) {
        this.deviceRestRpcService = deviceRestRpcService;
    }

	@Override
	public List<WifiDeviceMaxBusyVTO> fetchWDevicesOrderMaxHandset(int pageNo, int pageSize) {
		if(pageNo < 0 || pageSize < 0) 
			throw new RpcBusinessI18nCodeException(ResponseErrorCode.RPC_PARAMS_VALIDATE_ILLEGAL.code());
		return deviceRestRpcService.fetchWDevicesOrderMaxHandset(pageNo, pageSize);
	}

	@Override
	public TailPage<WifiDevice> fetchWDevicesOnline(int pageNo, int pageSize) {
		if(pageNo < 0 || pageSize < 0) 
			throw new RpcBusinessI18nCodeException(ResponseErrorCode.RPC_PARAMS_VALIDATE_ILLEGAL.code());
		return deviceRestRpcService.fetchWDevicesOnline(pageNo, pageSize);
	}

}
