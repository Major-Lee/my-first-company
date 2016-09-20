package com.bhu.vas.api.dto;

import java.io.Serializable;

import org.springframework.util.StringUtils;
/**
 * 移动设备上下线请求DTO
 * @author tangzichao
 *
 */
@SuppressWarnings("serial")
public class HandsetDeviceDTO implements Serializable{
	public static final String[] copyIgnoreProperties = {"data_tx_rate","data_rx_rate","dhcp_name"};
	
	public static final String Action_Online = "online";
	public static final String Action_Offline = "offline";
	public static final String Action_Sync = "sync";
	public static final String Action_Update = "update";
	public static final String Action_Authorize = "authorize";

	public static final String VAPNAME_WLAN0 = "wlan0";
	public static final String VAPNAME_WLAN10 = "wlan10";
	public static final String VAPNAME_WLAN3 = "wlan3";
	public static final String VAPNAME_WLAN13 = "wlan13";

	public static final String PORTAL_LOCAL = "local";
	public static final String PROTAL_NONE = "none";
	public static final String PROTAL_REMOTE= "remote";


	private String action;
	//移动设备mac
	private String mac;
	//物理发送速率
	private String phy_tx_rate;
	//物理接收速率
	private String phy_rx_rate;	

	//废弃
	private String phy_rate;
	//AP对针对此设备的发射功率
	private String tx_power;
	private String rx_chain_num;
	//AP接收到的终端的信号强度
	private String rssi;
	//信噪比
	private String snr;
	//空闲时间
	private String idle;
	private String state;
	//关联时间
	private String uptime;
	//接收包数
	private String rx_pkts;
	//接收字节数
	private String rx_bytes;
	//发送包数
	private String tx_pkts;
	//发送字节数
	private String tx_bytes;
	private String rx_unicast;
	private String tx_assoc;
	//终端关联的ssid
	private String ssid;
	//终端关联的bssid = wifiId
	private String bssid;
	//AP位置（ap界面上配置的位置信息）
	private String location;
	//当前信道
	private String channel;
	//终端hostname
	private String dhcp_name;
	//终端连接的设备vapname
	private String vapname;
	//终端地址ip
	private String ip;
	private String data_tx_rate;
	private String data_rx_rate;
	private String last_wifi_id;
	//记录生成或更新时间
	private long ts;

	/**
	 * 终端是否来自于有线口
	 */
	private String ethernet;
	/**
	 * 对于来自开启portal的接口上的终端,表明终端是否认证通过
	 */
	private String authorized;

	/**
	 * 老版本固件没有portal，新版本wlan0:none, wlan3:local
	 */
	private String portal;

	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public String getMac() {
		return mac;
	}
	public void setMac(String mac) {
		this.mac = mac;
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

	public String getVapname() {
		return vapname;
	}
	public void setVapname(String vapname) {
		this.vapname = vapname;
	}
	
	
	public String getLast_wifi_id() {
		return last_wifi_id;
	}
	public void setLast_wifi_id(String last_wifi_id) {
		this.last_wifi_id = last_wifi_id;
	}
	public long getTs() {
		return ts;
	}
	public void setTs(long ts) {
		this.ts = ts;
	}
	public boolean wasOnline(){
		return !Action_Offline.equals(action);
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
	public String getDhcp_name() {
		return dhcp_name;
	}
	public void setDhcp_name(String dhcp_name) {
		this.dhcp_name = dhcp_name;
	}
	public double fetchData_rx_rate_double(){
		if(StringUtils.isEmpty(data_rx_rate)){
			return 0;
		}
		return Double.parseDouble(data_rx_rate);
	}

	public String getEthernet() {
		return ethernet;
	}

	public void setEthernet(String ethernet) {
		this.ethernet = ethernet;
	}

	public String getAuthorized() {
		return authorized;
	}

	public void setAuthorized(String authorized) {
		this.authorized = authorized;
	}

	public String getPortal() {
		return portal;
	}

	public void setPortal(String portal) {
		this.portal = portal;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	
}