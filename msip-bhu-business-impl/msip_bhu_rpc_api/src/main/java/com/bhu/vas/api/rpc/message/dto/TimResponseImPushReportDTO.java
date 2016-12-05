package com.bhu.vas.api.rpc.message.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@SuppressWarnings("serial")
public class TimResponseImPushReportDTO implements java.io.Serializable{
	//推送id
	@JsonProperty("TaskId")
	private String taskId;
	
	//推送状态0(未处理) / 1（推送中) / 2(推送完成)
	@JsonProperty("Status")
	private int status;
	
	//推送开始时间
	@JsonProperty("StartTime")
	@JsonInclude(Include.NON_NULL)
	private String startTime;
	
	//已完成推送人数
	@JsonProperty("Finished")
	@JsonInclude(Include.NON_NULL)
	private int finished;
	
	//需推送总人数
	@JsonProperty("Total")
	@JsonInclude(Include.NON_NULL)
	private int total;

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public int getFinished() {
		return finished;
	}

	public void setFinished(int finished) {
		this.finished = finished;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}
	
}
