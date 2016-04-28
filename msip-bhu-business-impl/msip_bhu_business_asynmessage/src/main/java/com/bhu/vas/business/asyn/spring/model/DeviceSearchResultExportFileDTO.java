package com.bhu.vas.business.asyn.spring.model;

import com.bhu.vas.business.asyn.spring.builder.ActionDTO;
import com.bhu.vas.business.asyn.spring.builder.ActionMessageType;

public class DeviceSearchResultExportFileDTO extends ActionDTO {
	private int uid;
	//搜索条件json
	private String message;
	//导出文件路径
	private String exportFilePath;
	
	
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


	public String getExportFilePath() {
		return exportFilePath;
	}


	public void setExportFilePath(String exportFilePath) {
		this.exportFilePath = exportFilePath;
	}


	@Override
	public String getActionType() {
		return ActionMessageType.WifiDeviceResultExportFile.getPrefix();
	}

}
