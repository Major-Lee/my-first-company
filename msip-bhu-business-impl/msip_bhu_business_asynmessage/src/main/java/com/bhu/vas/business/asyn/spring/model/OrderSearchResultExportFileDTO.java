package com.bhu.vas.business.asyn.spring.model;

import com.bhu.vas.business.asyn.spring.builder.ActionDTO;
import com.bhu.vas.business.asyn.spring.builder.ActionMessageType;

public class OrderSearchResultExportFileDTO extends ActionDTO {
	public static final int SearchCondition_MessageType = 1;
	public static final int Macs_MessageType = 2;
	
	private int uid;
	//条件数据
	private String message;
	//条件类型
	private int messagetype;
	//导出文件路径
	private String exportFilePath;
	//开始日期
	private String start_date;
	//结束日期
	private String end_date;
	
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
	

	public int getMessagetype() {
		return messagetype;
	}


	public void setMessagetype(int messagetype) {
		this.messagetype = messagetype;
	}


	public String getExportFilePath() {
		return exportFilePath;
	}


	public void setExportFilePath(String exportFilePath) {
		this.exportFilePath = exportFilePath;
	}


	public String getStart_date() {
		return start_date;
	}


	public void setStart_date(String start_date) {
		this.start_date = start_date;
	}


	public String getEnd_date() {
		return end_date;
	}


	public void setEnd_date(String end_date) {
		this.end_date = end_date;
	}


	@Override
	public String getActionType() {
		return ActionMessageType.OrderResultExportFile.getPrefix();
	}

}
