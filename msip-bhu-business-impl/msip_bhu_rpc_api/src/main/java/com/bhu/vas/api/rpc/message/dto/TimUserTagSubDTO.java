package com.bhu.vas.api.rpc.message.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

@SuppressWarnings("serial")
public class TimUserTagSubDTO implements java.io.Serializable{
	@JsonProperty("To_Account")
	private String to_Account;
	
	@JsonProperty("Tags")
	private List<String> tags;
	
	public String getTo_Account() {
		return to_Account;
	}
	public void setTo_Account(String to_Account) {
		this.to_Account = to_Account;
	}
	public List<String> getTags() {
		return tags;
	}
	public void setTags(List<String> tags) {
		this.tags = tags;
	}
	
}
