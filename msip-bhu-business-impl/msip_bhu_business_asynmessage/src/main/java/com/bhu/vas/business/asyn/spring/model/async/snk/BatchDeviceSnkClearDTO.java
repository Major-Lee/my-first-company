package com.bhu.vas.business.asyn.spring.model.async.snk;

import com.bhu.vas.business.asyn.spring.builder.async.AsyncDTO;
import com.bhu.vas.business.asyn.spring.builder.async.AsyncMessageType;

public class BatchDeviceSnkClearDTO extends AsyncDTO {
	private String snk_type;
	private String template;

	public String getSnk_type() {
		return snk_type;
	}

	public void setSnk_type(String snk_type) {
		this.snk_type = snk_type;
	}

	public String getTemplate() {
		return template;
	}

	public void setTemplate(String template) {
		this.template = template;
	}
	@Override
	public String getAsyncType() {
		return AsyncMessageType.BatchDeviceSnkClear.getPrefix();
	}
	
}
