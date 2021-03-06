package com.bhu.vas.api.dto.header;

import java.io.Serializable;

import com.bhu.vas.api.dto.HandsetDeviceDTO;
import com.thoughtworks.xstream.annotations.XStreamAlias;
/**
 * wifi设备上线请求
 * @author tangzichao
 *
 */
@XStreamAlias("wlan")
@SuppressWarnings("serial")
public class WlanDTO implements Serializable{
	@XStreamAlias("ITEM")
	private HandsetDeviceDTO dto;

	public HandsetDeviceDTO getDto() {
		return dto;
	}

	public void setDto(HandsetDeviceDTO dto) {
		this.dto = dto;
	}
}
