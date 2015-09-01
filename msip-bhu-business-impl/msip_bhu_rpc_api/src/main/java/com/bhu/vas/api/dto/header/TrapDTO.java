package com.bhu.vas.api.dto.header;

import java.io.Serializable;

import com.bhu.vas.api.dto.WifiDeviceAlarmDTO;
import com.thoughtworks.xstream.annotations.XStreamAlias;
/**
 * wifi设备上线请求
 * @author tangzichao
 *
 */
@XStreamAlias("trap")
@SuppressWarnings("serial")
public class TrapDTO implements Serializable{
	@XStreamAlias("ITEM")
	private WifiDeviceAlarmDTO dto;

	public WifiDeviceAlarmDTO getDto() {
		return dto;
	}

	public void setDto(WifiDeviceAlarmDTO dto) {
		this.dto = dto;
	}
}
