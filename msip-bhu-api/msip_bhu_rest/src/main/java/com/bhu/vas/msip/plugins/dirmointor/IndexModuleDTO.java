package com.bhu.vas.msip.plugins.dirmointor;

public class IndexModuleDTO {
	private Long lastUpdated_at;
	private Long lastAlerted_at;
	public Long getLastUpdated_at() {
		return lastUpdated_at;
	}
	public void setLastUpdated_at(Long lastUpdated_at) {
		this.lastUpdated_at = lastUpdated_at;
	}
	public Long getLastAlerted_at() {
		return lastAlerted_at;
	}
	public void setLastAlerted_at(Long lastAlerted_at) {
		this.lastAlerted_at = lastAlerted_at;
	}
}
