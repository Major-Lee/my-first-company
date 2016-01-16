package com.bhu.vas.api.rpc.task.dto;

@SuppressWarnings("serial")
public class TaskResDetailDTO extends TaskResDTO{//implements java.io.Serializable{
	private String response;

	public String getResponse() {
		return response;
	}

	public void setResponse(String response) {
		this.response = response;
	}
	
}
