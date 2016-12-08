package com.bhu.vas.api.rpc.message.dto;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.fasterxml.jackson.annotation.JsonProperty;

@SuppressWarnings("serial")
public class TimGetPushReportDTO implements java.io.Serializable{
	@JsonProperty("TaskIds")
	private List<String> taskIds;

	public List<String> getTaskIds() {
		return taskIds;
	}

	public void setTaskIds(List<String> taskIds) {
		this.taskIds = taskIds;
	}
	
	public static TimGetPushReportDTO buildTimGetPushReportDTO(String taskids){
		if (StringUtils.isEmpty(taskids)) 
			return null;
		TimGetPushReportDTO dto = new TimGetPushReportDTO();
		dto.setTaskIds(Arrays.asList(taskids.split(",")));
		return dto;
	}
}
