package com.bhu.vas.api.dto.header;

import java.io.Serializable;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias("event")
@SuppressWarnings("serial")
public class EventDTO implements Serializable{
	@XStreamAlias("wlan")
	@XStreamAsAttribute
	private WlanDTO wlanDto;

	public WlanDTO getWlanDto() {
		return wlanDto;
	}

	public void setWlanDto(WlanDTO wlanDto) {
		this.wlanDto = wlanDto;
	}
}
