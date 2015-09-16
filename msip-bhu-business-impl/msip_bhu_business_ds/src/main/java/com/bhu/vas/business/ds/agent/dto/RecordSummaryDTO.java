package com.bhu.vas.business.ds.agent.dto;

public class RecordSummaryDTO {
	private String id;
	private long total_onlineduration;
	private int  total_connecttimes;
	private long total_tx_bytes;
	private long total_rx_bytes;
	private int  total_handsets;
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
	
	public int getTotal_handsets() {
		return total_handsets;
	}
	public void setTotal_handsets(int total_handsets) {
		this.total_handsets = total_handsets;
	}
	public void incr(RecordSummaryDTO dto){
		if(dto == null) return;
		total_onlineduration += dto.getTotal_onlineduration();
		total_connecttimes += dto.getTotal_connecttimes();
		total_tx_bytes += dto.getTotal_tx_bytes();
		total_rx_bytes += dto.getTotal_rx_bytes();
		total_handsets += dto.getTotal_handsets();
	}
	
	public String toString(){
		return String.format("id[%s] onlineduration[%s] connecttimes[%s] handsets[%s] tx_bytes[%s] rx_bytes[%s]", 
				id,total_onlineduration,total_connecttimes,total_handsets,total_tx_bytes,total_rx_bytes);
	}
}
