package com.bhu.vas.business.asyn.spring.model.async.device;

import com.bhu.vas.business.asyn.spring.builder.ActionDTO;
import com.bhu.vas.business.asyn.spring.builder.async.AsyncMessageType;

public class BatchDeviceApplyAdvertiseDTO extends ActionDTO {

	
	@Override
	public String getActionType() {
		return AsyncMessageType.BatchDeviceApplyAdvertise.getPrefix();
	}
	
}
