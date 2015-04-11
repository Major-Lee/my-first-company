package com.bhu.vas.business.asyn.spring.model;

import java.util.List;

import com.bhu.vas.api.dto.ret.WifiDeviceTerminalDTO;
import com.bhu.vas.business.asyn.spring.builder.ActionDTO;
import com.bhu.vas.business.asyn.spring.builder.ActionMessageType;

public class WifiDeviceTerminalNotifyDTO extends ActionDTO {
	private String ssid;//连接设备的ssid
	private String bssid;//连接设备的bssid
	private List<WifiDeviceTerminalDTO> terminals;

	public String getSsid() {
		return ssid;
	}

	public void setSsid(String ssid) {
		this.ssid = ssid;
	}

	public String getBssid() {
		return bssid;
	}

	public void setBssid(String bssid) {
		this.bssid = bssid;
	}

	public List<WifiDeviceTerminalDTO> getTerminals() {
		return terminals;
	}

	public void setTerminals(List<WifiDeviceTerminalDTO> terminals) {
		this.terminals = terminals;
	}
	
	@Override
	public String getActionType() {
		return ActionMessageType.WifiDeviceTerminalNotify.getPrefix();
	}

}
