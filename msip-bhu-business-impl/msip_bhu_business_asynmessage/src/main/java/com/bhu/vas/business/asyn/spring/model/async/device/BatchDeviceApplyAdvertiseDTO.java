package com.bhu.vas.business.asyn.spring.model.async.device;

import java.util.List;

import com.bhu.vas.business.asyn.spring.builder.async.AsyncDTO;
import com.bhu.vas.business.asyn.spring.builder.async.AsyncMessageType;

public class BatchDeviceApplyAdvertiseDTO extends AsyncDTO{
	
	private List<String> ids;
	private char dtoType;

	public List<String> getIds() {
		return ids;
	}
	public void setIds(List<String> ids) {
		this.ids = ids;
	}
	public char getDtoType() {
		return dtoType;
	}

	public void setDtoType(char dtoType) {
		this.dtoType = dtoType;
	}

	@Override
	public String getAsyncType() {
		return AsyncMessageType.BatchDeviceApplyAdvertise.getPrefix();
	}
}

