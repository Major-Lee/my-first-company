package com.bhu.vas.business.ds.agent.dto;

public class RecordSummaryDTO {
	private String id;
	private long total_onlineduration;
	private int  total_connecttimes;
	private long total_tx_bytes;
	private long total_rx_bytes;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public int getTotal_connecttimes() {
		return total_connecttimes;
	}
	public void setTotal_connecttimes(int total_connecttimes) {
		this.total_connecttimes = total_connecttimes;
	}
	public long getTotal_onlineduration() {
		return total_onlineduration;
	}
	public void setTotal_onlineduration(long total_onlineduration) {
		this.total_onlineduration = total_onlineduration;
	}
	public long getTotal_tx_bytes() {
		return total_tx_bytes;
	}
	public void setTotal_tx_bytes(long total_tx_bytes) {
		this.total_tx_bytes = total_tx_bytes;
	}
	public long getTotal_rx_bytes() {
		return total_rx_bytes;
	}
	public void setTotal_rx_bytes(long total_rx_bytes) {
		this.total_rx_bytes = total_rx_bytes;
	}
	
}
