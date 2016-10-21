package com.bhu.vas.business.asyn.spring.model.async.device;

import java.util.List;

import com.bhu.vas.api.rpc.advertise.model.Advertise;
import com.bhu.vas.business.asyn.spring.builder.ActionDTO;
import com.bhu.vas.business.asyn.spring.builder.async.AsyncMessageType;

public class BatchDeviceApplyAdvertiseDTO extends ActionDTO {
	private List<Advertise> adList;
	
	public List<Advertise> getAdList() {
		return adList;
	}

	public void setAdList(List<Advertise> adList) {
		this.adList = adList;
	}

	@Override
	public String getActionType() {
		return AsyncMessageType.BatchDeviceApplyAdvertise.getPrefix();
	}
	
}
