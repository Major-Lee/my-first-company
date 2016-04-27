package com.bhu.vas.business.asyn.spring.model;

import com.bhu.vas.business.asyn.spring.builder.ActionDTO;
import com.bhu.vas.business.asyn.spring.builder.ActionMessageType;

public class OrderSearchResultExportFileDTO extends ActionDTO {
	private int uid;
	//搜索条件json
	private String message;
	//导出文件名称
	private String exportFileName;
	
	
	public int getUid() {
		return uid;
	}


	public void setUid(int uid) {
		this.uid = uid;
	}


	public String getMessage() {
		return message;
	}


	public void setMessage(String message) {
		this.message = message;
	}


	public String getExportFileName() {
		return exportFileName;
	}


	public void setExportFileName(String exportFileName) {
		this.exportFileName = exportFileName;
	}


	@Override
	public String getActionType() {
		return ActionMessageType.OrderResultExportFile.getPrefix();
	}

}
