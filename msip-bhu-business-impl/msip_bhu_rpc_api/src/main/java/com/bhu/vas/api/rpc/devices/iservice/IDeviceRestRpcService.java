package com.bhu.vas.api.rpc.devices.iservice;

import java.util.List;

import com.bhu.vas.api.rpc.devices.model.WifiDevice;
import com.bhu.vas.api.vto.WifiDeviceMaxBusyVTO;
import com.smartwork.msip.cores.orm.support.page.TailPage;



public interface IDeviceRestRpcService {
	public List<WifiDeviceMaxBusyVTO> fetchWDevicesOrderMaxHandset(int pageNo, int pageSize);
	public TailPage<WifiDevice> fetchWDevicesOnline(int pageNo, int pageSize);
}
