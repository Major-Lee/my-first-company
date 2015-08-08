package com.bhu.vas.api.dto.ret;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 设备VAP上的终端信息
 * <SUB mac="88:32:9b:32:41:10" aid="1" phy_tx_rate="72M" phy_rx_rate="25M" current_phy_tx_rate="72M" 
 * current_phy_rx_rate="6M" data_tx_rate="0" data_rx_rate="224" phy_tx_errors="0" phy_rx_errors="0" 
 * phy_tx_drops="0" phy_rx_drops="680" phy_rate="72M" tx_power="3dBm" rx_chain_num="2" rssi="-28dBm" 
 * antenna_rssi0="-31dBm" antenna_rssi1="-32dBm" antenna_rssi2="-95dBm" snr="67dB" antenna_snr0="64dB" 
 * antenna_snr1="63dB" antenna_snr2="0dB" idle="0" state="27" uptime="1241" wds_list="" rx_pkts="1282" 
 * rx_bytes="111577" tx_pkts="262" tx_bytes="44097" rx_unicast="0" tx_assoc="1" />
 * @author tangzichao
 *
 */
@SuppressWarnings("serial")
public class WifiDeviceTerminalDTO implements Serializable{
	public static final String Mode_STA = "sta";//代表终端
	
	//终端mac
	private String mac;
	//上行速率
	private String data_tx_rate;
	//下行速率
	private String data_rx_rate;

	//上传流量
	private String tx_bytes;
	//下载流量
	private String rx_bytes;
	
	private String mode;
	
	//vap名称
	@JsonProperty("interface") 
	private String vapname;
	
	public String getMac() {
		return mac;
	}
	public void setMac(String mac) {
		this.mac = mac;
	}
	public String getData_tx_rate() {
		return data_tx_rate;
	}
	public void setData_tx_rate(String data_tx_rate) {
		this.data_tx_rate = data_tx_rate;
	}
	public String getData_rx_rate() {
		return data_rx_rate;
	}
	public void setData_rx_rate(String data_rx_rate) {
		this.data_rx_rate = data_rx_rate;
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

	public String getMode() {
		return mode;
	}
	public void setMode(String mode) {
		this.mode = mode;
	}
	public String getVapname() {
		return vapname;
	}
	public void setVapname(String vapname) {
		this.vapname = vapname;
	}
}