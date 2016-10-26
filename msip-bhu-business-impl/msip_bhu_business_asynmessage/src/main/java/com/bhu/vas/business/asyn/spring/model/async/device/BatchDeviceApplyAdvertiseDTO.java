package com.bhu.vas.business.asyn.spring.model.async.device;

import java.util.List;

import com.bhu.vas.business.asyn.spring.builder.async.AsyncDTO;
import com.bhu.vas.business.asyn.spring.builder.async.AsyncMessageType;

public class BatchDeviceApplyAdvertiseDTO extends AsyncDTO {
	private List<Integer> adList;
	private char dto_type;

	public List<Integer> getAdList() {
		return adList;
	}

	public void setAdList(List<Integer> adList) {
		this.adList = adList;
	}

	public char getDto_type() {
		return dto_type;
	}

	public void setDto_type(char dto_type) {
		this.dto_type = dto_type;
	}

	@Override
	public String getAsyncType() {
		return AsyncMessageType.BatchDeviceApplyAdvertise.getPrefix();
	}

}
