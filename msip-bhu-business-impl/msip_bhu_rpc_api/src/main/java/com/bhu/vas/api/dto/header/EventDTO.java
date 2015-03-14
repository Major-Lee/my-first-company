package com.bhu.vas.api.dto.header;

import java.io.Serializable;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("event")
@SuppressWarnings("serial")
public class EventDTO implements Serializable{
	@XStreamAlias("trap")
	private TrapDTO trapDto;
	
	@XStreamAlias("wlan")
	private WlanDTO wlanDto;

	public TrapDTO getTrapDto() {
		return trapDto;
	}

	public void setTrapDto(TrapDTO trapDto) {
		this.trapDto = trapDto;
	}

	public WlanDTO getWlanDto() {
		return wlanDto;
	}

	public void setWlanDto(WlanDTO wlanDto) {
		this.wlanDto = wlanDto;
	}

}
