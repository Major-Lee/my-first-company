package com.bhu.vas.api.rpc.devices.model;

import java.util.Date;

import org.springframework.util.StringUtils;

import com.smartwork.msip.cores.orm.model.BaseStringModel;
/*
 * 移动设备基础信息
 */
@SuppressWarnings("serial")
public class HandsetDevice extends BaseStringModel{
	
	private String phy_tx_rate;
	
	private String phy_rx_rate;	
	//设备发送终端的速率 终端的下行速率 bps
	private String data_tx_rate;
	//设备接收终端的速率 终端的上行速率 bps
	private String data_rx_rate;
	
	private String phy_rate;
	
	private String tx_power;
	
	private String rx_chain_num;
	
	private String rssi;
	
	private String snr;
	
	private String idle;
	
	private String state;
	
	private String uptime;
	
	private String rx_pkts;
	
	private String rx_bytes;
	
	private String tx_pkts;
	
	private String tx_bytes;
	
	private String rx_unicast;
	
	private String tx_assoc;
	
	private String ssid;
	
	private String bssid;
	//终端连接的vap的name
	private String vapname;
	//终端主机名称
	private String hostname;
	
	private String location;
	
	private String channel;
	//手机号码
	private String mobileno;
	//移动设备是否在线
	private boolean online;
	//最后一次连入的wifi设备id
	private String last_wifi_id;
	//最后一次连入的wifi设备的时间
	private Date last_login_at;
	private Date created_at;
	
	
	@Override
	public void preInsert() {
		if (this.created_at == null)
			this.created_at = new Date();
		super.preInsert();
	}
	
	@Override
	public void preUpdate() {
		super.preUpdate();
	}

	public String getPhy_tx_rate() {
		return phy_tx_rate;
	}

	public void setPhy_tx_rate(String phy_tx_rate) {
		this.phy_tx_rate = phy_tx_rate;
	}

	public String getPhy_rx_rate() {
		return phy_rx_rate;
	}

	public void setPhy_rx_rate(String phy_rx_rate) {
		this.phy_rx_rate = phy_rx_rate;
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

	public double getData_rx_rate_double(){
		if(StringUtils.isEmpty(data_rx_rate)){
			return 0;
		}
		return Double.parseDouble(data_rx_rate);
	}
	
	public void setData_rx_rate(String data_rx_rate) {
		this.data_rx_rate = data_rx_rate;
	}

	public String getPhy_rate() {
		return phy_rate;
	}

	public void setPhy_rate(String phy_rate) {
		this.phy_rate = phy_rate;
	}

	public String getTx_power() {
		return tx_power;
	}

	public void setTx_power(String tx_power) {
		this.tx_power = tx_power;
	}

	public String getRx_chain_num() {
		return rx_chain_num;
	}

	public void setRx_chain_num(String rx_chain_num) {
		this.rx_chain_num = rx_chain_num;
	}

	public String getRssi() {
		return rssi;
	}

	public void setRssi(String rssi) {
		this.rssi = rssi;
	}

	public String getSnr() {
		return snr;
	}

	public void setSnr(String snr) {
		this.snr = snr;
	}

	public String getIdle() {
		return idle;
	}

	public void setIdle(String idle) {
		this.idle = idle;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getVapname() {
		return vapname;
	}

	public void setVapname(String vapname) {
		this.vapname = vapname;
	}

	public String getUptime() {
		return uptime;
	}

	public void setUptime(String uptime) {
		this.uptime = uptime;
	}

	public String getRx_pkts() {
		return rx_pkts;
	}

	public void setRx_pkts(String rx_pkts) {
		this.rx_pkts = rx_pkts;
	}

	public String getHostname() {
		return hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
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

	public String getTx_bytes() {
		return tx_bytes;
	}

	public void setTx_bytes(String tx_bytes) {
		this.tx_bytes = tx_bytes;
	}

	public String getRx_unicast() {
		return rx_unicast;
	}

	public void setRx_unicast(String rx_unicast) {
		this.rx_unicast = rx_unicast;
	}

	public String getTx_assoc() {
		return tx_assoc;
	}

	public void setTx_assoc(String tx_assoc) {
		this.tx_assoc = tx_assoc;
	}

	public String getSsid() {
		return ssid;
	}

	public void setSsid(String ssid) {
		this.ssid = ssid;
	}

	public String getBssid() {
		return bssid;
	}

	public void setBssid(String bssid) {
		this.bssid = bssid;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

	public String getMobileno() {
		return mobileno;
	}

	public void setMobileno(String mobileno) {
		this.mobileno = mobileno;
	}
	
	public boolean isOnline() {
		return online;
	}

	public void setOnline(boolean online) {
		this.online = online;
	}

	public String getLast_wifi_id() {
		return last_wifi_id;
	}

	public void setLast_wifi_id(String last_wifi_id) {
		this.last_wifi_id = last_wifi_id;
	}

	public Date getLast_login_at() {
		return last_login_at;
	}

	public void setLast_login_at(Date last_login_at) {
		this.last_login_at = last_login_at;
	}

	public Date getCreated_at() {
		return created_at;
	}

	public void setCreated_at(Date created_at) {
		this.created_at = created_at;
	}
}