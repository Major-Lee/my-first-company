package com.bhu.vas.api.rpc.message.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

@SuppressWarnings("serial")
public class TimResponseBasicDTO implements java.io.Serializable{
	@JsonProperty("ActionStatus")
	private String actionStatus;
	
	@JsonProperty("ErrorInfo")
	private String errorInfo;
	
	@JsonProperty("ErrorCode")
	private int errorCode;
	//推送id
	@JsonProperty("TaskId")
	@JsonInclude(Include.NON_NULL)
	private String taskId;
	
	//批量导入用户时未成功的用户链表
	@JsonProperty("FailAccounts")
	@JsonInclude(Include.NON_NULL)
	private List<String> failAccounts;
	
	//批量导入用户时未成功的用户链表
	@JsonProperty("Reports")
	@JsonInclude(Include.NON_NULL)
	private List<TimResponseImPushReportDTO> reports;
	
	public List<TimResponseImPushReportDTO> getReports() {
		return reports;
	}

	public void setReports(List<TimResponseImPushReportDTO> reports) {
		this.reports = reports;
	}
	
	public List<String> getFailAccounts() {
		return failAccounts;
	}

	public void setFailAccounts(List<String> failAccounts) {
		this.failAccounts = failAccounts;
	}

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public String getActionStatus() {
		return actionStatus;
	}

	public void setActionStatus(String actionStatus) {
		this.actionStatus = actionStatus;
	}

	public String getErrorInfo() {
		return errorInfo;
	}

	public void setErrorInfo(String errorInfo) {
		this.errorInfo = errorInfo;
	}

	public int getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}
	
	public boolean isExecutedSuccess(){
		return this.getActionStatus().equals("OK");
	}
	
	
}
