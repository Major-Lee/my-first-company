package com.bhu.vas.di.op.batchimport.shipmentlist.callback;

import com.bhu.vas.api.rpc.devices.model.WifiDevice;

public interface ExcelElementCallback {
	public WifiDevice elementCallback(String sn);
}
