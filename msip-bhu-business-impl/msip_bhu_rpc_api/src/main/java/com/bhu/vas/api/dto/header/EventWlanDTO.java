package com.bhu.vas.api.dto.header;

import java.io.Serializable;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("event")
@SuppressWarnings("serial")
public class EventWlanDTO implements Serializable{
	@XStreamAlias("wlan")
	private WlanDTO wlanDto;

	public WlanDTO getWlanDto() {
		return wlanDto;
	}

	public void setWlanDto(WlanDTO wlanDto) {
		this.wlanDto = wlanDto;
	}
}
