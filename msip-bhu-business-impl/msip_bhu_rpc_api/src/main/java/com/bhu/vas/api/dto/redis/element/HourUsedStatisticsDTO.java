package com.bhu.vas.api.dto.redis.element;

public class HourUsedStatisticsDTO {
	 //time="0" tx_bytes="1234" rx_bytes="222" sta="4"
	private String time;
	private String tx_bytes;
	private String rx_bytes;
	private String sta;
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getTx_bytes() {
		return tx_bytes;
	}
	public void setTx_bytes(String tx_bytes) {
		this.tx_bytes = tx_bytes;
	}
	public String getRx_bytes() {
		return rx_bytes;
	}
	public void setRx_bytes(String rx_bytes) {
		this.rx_bytes = rx_bytes;
	}
	public String getSta() {
		return sta;
	}
	public void setSta(String sta) {
		this.sta = sta;
	}
}
