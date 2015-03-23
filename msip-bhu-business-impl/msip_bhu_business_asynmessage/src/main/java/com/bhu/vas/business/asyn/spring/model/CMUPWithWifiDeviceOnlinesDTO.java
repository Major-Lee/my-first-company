package com.bhu.vas.business.asyn.spring.model;

import java.util.List;

import com.bhu.vas.api.dto.WifiDeviceDTO;
import com.bhu.vas.business.asyn.spring.builder.ActionDTO;
import com.bhu.vas.business.asyn.spring.builder.ActionMessageType;

public class CMUPWithWifiDeviceOnlinesDTO extends ActionDTO {
	private String ctx;
	private List<WifiDeviceDTO> devices;

	@Override
	public String getActionType() {
		return ActionMessageType.CMUPWithWifiDeviceOnlines.getPrefix();
	}

	public String getCtx() {
		return ctx;
	}

	public void setCtx(String ctx) {
		this.ctx = ctx;
	}

	public List<WifiDeviceDTO> getDevices() {
		return devices;
	}

	public void setDevices(List<WifiDeviceDTO> devices) {
		this.devices = devices;
	}
}
