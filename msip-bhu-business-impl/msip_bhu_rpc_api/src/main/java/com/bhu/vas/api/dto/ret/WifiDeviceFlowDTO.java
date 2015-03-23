package com.bhu.vas.api.dto.ret;

import java.io.Serializable;

/**
 * wifi设备流量以及发包状态DTO
	<return>
        <ITEM index="1" cmd="if_stat" status="done" >
                <if_stat_sub>
                        <SUB name="eth0" rx_pkts="0" rx_err_pkts="0" rx_drop_pkts="0" rx_over_pkts="0" 
                        rx_err_frames="0" rx_multicast_pkts="0" rx_bytes="0" tx_pkts="0" 
                        tx_err_pkts="0" tx_drop_pkts="0" tx_over_pkts="0" tx_err_carrier="0" tx_bytes="0" />
                        <SUB name="eth1" rx_pkts="934" rx_err_pkts="0" rx_drop_pkts="0" rx_over_pkts="0" rx_err_frames="0" rx_multicast_pkts="555" rx_bytes="228108" tx_pkts="431" tx_err_pkts="0" tx_drop_pkts="0" tx_over_pkts="0" tx_err_carrier="0" tx_bytes="44778" />
                        <SUB name="wlan0" rx_pkts="0" rx_err_pkts="0" rx_drop_pkts="0" rx_over_pkts="0" rx_err_frames="0" rx_multicast_pkts="0" rx_bytes="0" tx_pkts="447" tx_err_pkts="0" tx_drop_pkts="16" tx_over_pkts="0" tx_err_carrier="0" tx_bytes="93849" />
                        <SUB name="wlan2" rx_pkts="0" rx_err_pkts="0" rx_drop_pkts="0" rx_over_pkts="0" rx_err_frames="0" rx_multicast_pkts="0" rx_bytes="0" tx_pkts="0" tx_err_pkts="0" tx_drop_pkts="0" tx_over_pkts="0" tx_err_carrier="0" tx_bytes="0" />
                        <SUB name="wlan3" rx_pkts="0" rx_err_pkts="0" rx_drop_pkts="0" rx_over_pkts="0" rx_err_frames="0" rx_multicast_pkts="0" rx_bytes="0" tx_pkts="0" tx_err_pkts="0" tx_drop_pkts="0" tx_over_pkts="0" tx_err_carrier="0" tx_bytes="0" />
                        <SUB name="wlan1" rx_pkts="0" rx_err_pkts="0" rx_drop_pkts="0" rx_over_pkts="0" rx_err_frames="0" rx_multicast_pkts="0" rx_bytes="0" tx_pkts="0" tx_err_pkts="0" tx_drop_pkts="0" tx_over_pkts="0" tx_err_carrier="0" tx_bytes="0" />
                        <SUB name="br-lan" rx_pkts="0" rx_err_pkts="0" rx_drop_pkts="0" rx_over_pkts="0" rx_err_frames="0" rx_multicast_pkts="0" rx_bytes="0" tx_pkts="8" tx_err_pkts="0" tx_drop_pkts="0" tx_over_pkts="0" tx_err_carrier="0" tx_bytes="576" />
                </if_stat_sub>
        </ITEM>
	</return>
 * @author tangzichao
 *
 */
@SuppressWarnings("serial")
public class WifiDeviceFlowDTO implements Serializable{
	//网卡名称
	private String name;
	//接收包的数量
	private String rx_pkts;
	//接收包错误的数量
	private String rx_err_pkts;
	//接收的丢包的数量
	private String rx_drop_pkts;

	private String rx_over_pkts;
	private String rx_err_frames;
	private String rx_multicast_pkts;
	
	//接收的字节数
	private String rx_bytes;
	//发送包的数量
	private String tx_pkts;
	//发送包错误的数量
	private String tx_err_pkts;
	//发送的丢包的数量
	private String tx_drop_pkts;
	private String tx_over_pkts;
	private String tx_err_carrier;
	//发送的字节数
	private String tx_bytes;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getRx_pkts() {
		return rx_pkts;
	}
	public void setRx_pkts(String rx_pkts) {
		this.rx_pkts = rx_pkts;
	}
	public String getRx_err_pkts() {
		return rx_err_pkts;
	}
	public void setRx_err_pkts(String rx_err_pkts) {
		this.rx_err_pkts = rx_err_pkts;
	}
	public String getRx_drop_pkts() {
		return rx_drop_pkts;
	}
	public void setRx_drop_pkts(String rx_drop_pkts) {
		this.rx_drop_pkts = rx_drop_pkts;
	}
	public String getRx_over_pkts() {
		return rx_over_pkts;
	}
	public void setRx_over_pkts(String rx_over_pkts) {
		this.rx_over_pkts = rx_over_pkts;
	}
	public String getRx_err_frames() {
		return rx_err_frames;
	}
	public void setRx_err_frames(String rx_err_frames) {
		this.rx_err_frames = rx_err_frames;
	}
	public String getRx_multicast_pkts() {
		return rx_multicast_pkts;
	}
	public void setRx_multicast_pkts(String rx_multicast_pkts) {
		this.rx_multicast_pkts = rx_multicast_pkts;
	}
	public String getRx_bytes() {
		return rx_bytes;
	}
	public void setRx_bytes(String rx_bytes) {
		this.rx_bytes = rx_bytes;
	}
	public String getTx_pkts() {
		return tx_pkts;
	}
	public void setTx_pkts(String tx_pkts) {
		this.tx_pkts = tx_pkts;
	}
	public String getTx_err_pkts() {
		return tx_err_pkts;
	}
	public void setTx_err_pkts(String tx_err_pkts) {
		this.tx_err_pkts = tx_err_pkts;
	}
	public String getTx_drop_pkts() {
		return tx_drop_pkts;
	}
	public void setTx_drop_pkts(String tx_drop_pkts) {
		this.tx_drop_pkts = tx_drop_pkts;
	}
	public String getTx_over_pkts() {
		return tx_over_pkts;
	}
	public void setTx_over_pkts(String tx_over_pkts) {
		this.tx_over_pkts = tx_over_pkts;
	}
	public String getTx_err_carrier() {
		return tx_err_carrier;
	}
	public void setTx_err_carrier(String tx_err_carrier) {
		this.tx_err_carrier = tx_err_carrier;
	}
	public String getTx_bytes() {
		return tx_bytes;
	}
	public void setTx_bytes(String tx_bytes) {
		this.tx_bytes = tx_bytes;
	}
}