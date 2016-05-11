package com.bhu.vas.business.asyn.spring.model.async;

import com.bhu.vas.business.asyn.spring.builder.ActionDTO;
import com.bhu.vas.business.asyn.spring.builder.async.AsyncMessageType;

public class BatchImportConfirmDTO extends ActionDTO {
	private String batchno;
	
	public String getBatchno() {
		return batchno;
	}
	public void setBatchno(String batchno) {
		this.batchno = batchno;
	}

	@Override
	public String getActionType() {
		return AsyncMessageType.BatchImportConfirm.getPrefix();
	}
}
