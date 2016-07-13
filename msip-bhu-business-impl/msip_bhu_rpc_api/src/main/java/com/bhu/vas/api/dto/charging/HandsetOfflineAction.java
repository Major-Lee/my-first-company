package com.bhu.vas.api.dto.charging;

public class HandsetOfflineAction extends ChargingAction{
	private String hmac;
	private String huptime;
	private String vapname;
	private String bssid;
	private long tx_bytes;
	private long rx_bytes;
	
	private String rssi;
	private String snr;
	private String authorized;
	private String ethernet;
	
	public String getHmac() {
		return hmac;
	}
	public void setHmac(String hmac) {
		this.hmac = hmac;
	}
	public long getTx_bytes() {
		return tx_bytes;
	}

	public void setTx_bytes(long tx_bytes) {
		this.tx_bytes = tx_bytes;
	}

	public long getRx_bytes() {
		return rx_bytes;
	}

	public void setRx_bytes(long rx_bytes) {
		this.rx_bytes = rx_bytes;
	}

	public String getHuptime() {
		return huptime;
	}
	public void setHuptime(String huptime) {
		this.huptime = huptime;
	}
	@Override
	public String getAct() {
		return ActionBuilder.ActionMode.HandsetOffline.getPrefix();
	}
	public String getVapname() {
		return vapname;
	}
	public void setVapname(String vapname) {
		this.vapname = vapname;
	}
	public String getBssid() {
		return bssid;
	}
	public void setBssid(String bssid) {
		this.bssid = bssid;
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
	public String getAuthorized() {
		return authorized;
	}
	public void setAuthorized(String authorized) {
		this.authorized = authorized;
	}
	public String getEthernet() {
		return ethernet;
	}
	public void setEthernet(String ethernet) {
		this.ethernet = ethernet;
	}
	
}
