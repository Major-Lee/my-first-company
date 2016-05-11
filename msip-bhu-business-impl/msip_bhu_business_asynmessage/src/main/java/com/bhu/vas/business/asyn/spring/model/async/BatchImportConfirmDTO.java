package com.bhu.vas.business.asyn.spring.model.async;

import com.bhu.vas.business.asyn.spring.builder.async.AsyncDTO;
import com.bhu.vas.business.asyn.spring.builder.async.AsyncMessageType;

public class BatchImportConfirmDTO extends AsyncDTO {
	private String batchno;
	
	public String getBatchno() {
		return batchno;
	}
	public void setBatchno(String batchno) {
		this.batchno = batchno;
	}

	@Override
	public String getAsyncType() {
		return AsyncMessageType.BatchImportConfirm.getPrefix();
	}
}
