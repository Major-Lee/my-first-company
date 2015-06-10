package com.bhu.vas.business.asyn.spring.model;

import java.util.List;

import com.bhu.vas.api.dto.HandsetDeviceDTO;
import com.bhu.vas.business.asyn.spring.builder.ActionDTO;
import com.bhu.vas.business.asyn.spring.builder.ActionMessageType;

public class HandsetDeviceSyncDTO extends ActionDTO {
	private List<HandsetDeviceDTO> dtos;//同步的移动设备数据

	public List<HandsetDeviceDTO> getDtos() {
		return dtos;
	}

	public void setDtos(List<HandsetDeviceDTO> dtos) {
		this.dtos = dtos;
	}

	@Override
	public String getActionType() {
		return ActionMessageType.HandsetDeviceSync.getPrefix();
	}

}
