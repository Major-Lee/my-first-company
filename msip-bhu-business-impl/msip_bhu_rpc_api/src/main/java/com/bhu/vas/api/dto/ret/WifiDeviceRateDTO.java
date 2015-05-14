package com.bhu.vas.api.dto.ret;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * wifi设备的实时速率DTO
 * @author tangzichao
 *
 */
@SuppressWarnings("serial")
public class WifiDeviceRateDTO implements Serializable{
	@JsonProperty("interface")
	private String interface_name;
	//设备发送包的数量
	private String tx_pkts;
	//设备接收包的数量
	private String rx_pkts;
	//设备发送的字节数
	private String tx_bytes;
	//设备接收的字节数
	private String rx_bytes;
	//设备发送的速率
	private String tx_rate;
	//设备接收的速率
	private String rx_rate;
	
	public String getInterface_name() {
		return interface_name;
	}
	public void setInterface_name(String interface_name) {
		this.interface_name = interface_name;
	}
	public String getTx_pkts() {
		return tx_pkts;
	}
	public void setTx_pkts(String tx_pkts) {
		this.tx_pkts = tx_pkts;
	}
	public String getRx_pkts() {
		return rx_pkts;
	}
	public void setRx_pkts(String rx_pkts) {
		this.rx_pkts = rx_pkts;
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
	public String getTx_rate() {
		return tx_rate;
	}
	public void setTx_rate(String tx_rate) {
		this.tx_rate = tx_rate;
	}
	public String getRx_rate() {
		return rx_rate;
	}
	public void setRx_rate(String rx_rate) {
		this.rx_rate = rx_rate;
	}
}