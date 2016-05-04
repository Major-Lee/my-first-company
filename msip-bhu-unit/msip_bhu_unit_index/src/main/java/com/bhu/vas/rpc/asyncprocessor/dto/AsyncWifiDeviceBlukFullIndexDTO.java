package com.bhu.vas.rpc.asyncprocessor.dto;

import java.util.List;

public class AsyncWifiDeviceBlukFullIndexDTO extends AsyncIndexDTO{

	private List<String> macs;
	
	public List<String> getMacs() {
		return macs;
	}

	public void setMacs(List<String> macs) {
		this.macs = macs;
	}

	@Override
	public String getActionType() {
		return AsyncIndexMessageType.WifiDevice_BlukFullIndex.getType();
	}

}
