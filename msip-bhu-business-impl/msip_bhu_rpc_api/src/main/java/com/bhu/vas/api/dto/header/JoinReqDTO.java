package com.bhu.vas.api.dto.header;

import java.io.Serializable;

import com.bhu.vas.api.dto.WifiDeviceDTO;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias("join_req")
@SuppressWarnings("serial")
public class JoinReqDTO implements Serializable{
	@XStreamAlias("ITEM")
	@XStreamAsAttribute
	private WifiDeviceDTO dto;

	public WifiDeviceDTO getDto() {
		return dto;
	}

	public void setDto(WifiDeviceDTO dto) {
		this.dto = dto;
	}
}
