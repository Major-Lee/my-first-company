package com.bhu.vas.business.asyn.spring.model.async.group;

import com.bhu.vas.business.asyn.spring.builder.async.AsyncDTO;
import com.bhu.vas.business.asyn.spring.builder.async.AsyncMessageType;
import com.bhu.vas.business.asyn.spring.model.IDTO;

public class BatchGroupDeviceSnkApplyDTO extends AsyncDTO implements IDTO {
	private String message;
	private String snk_type;
	private String template;
	private char dtoType;

	public String getSnk_type() {
		return snk_type;
	}

	public void setSnk_type(String snk_type) {
		this.snk_type = snk_type;
	}

	
	@Override
	public String getAsyncType() {
		return AsyncMessageType.BatchGroupDeviceSnkApply.getPrefix();
	}

	@Override
	public char getDtoType() {
		return dtoType;
	}
	public void setDtoType(char dtoType) {
		this.dtoType = dtoType;
	}
	public String getTemplate() {
		return template;
	}

	public void setTemplate(String template) {
		this.template = template;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
