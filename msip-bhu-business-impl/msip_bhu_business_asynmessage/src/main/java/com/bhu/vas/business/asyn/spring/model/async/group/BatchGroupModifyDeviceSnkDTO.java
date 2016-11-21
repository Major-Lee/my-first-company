package com.bhu.vas.business.asyn.spring.model.async.group;

import com.bhu.vas.business.asyn.spring.builder.async.AsyncDTO;
import com.bhu.vas.business.asyn.spring.builder.async.AsyncMessageType;
import com.bhu.vas.business.asyn.spring.model.IDTO;

public class BatchGroupModifyDeviceSnkDTO extends AsyncDTO implements IDTO {
	private String message;
	private String ssid;
	private int rate;
	private char dtoType;

	@Override
	public String getAsyncType() {
		return AsyncMessageType.BatchGroupModifyDeviceSnk.getPrefix();
	}

	@Override
	public char getDtoType() {
		return dtoType;
	}
	public void setDtoType(char dtoType) {
		this.dtoType = dtoType;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getSsid() {
		return ssid;
	}

	public void setSsid(String ssid) {
		this.ssid = ssid;
	}

	public int getRate() {
		return rate;
	}

	public void setRate(int rate) {
		this.rate = rate;
	}
	
	
}
