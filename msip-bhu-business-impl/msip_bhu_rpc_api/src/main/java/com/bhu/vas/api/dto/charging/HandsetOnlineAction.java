package com.bhu.vas.api.dto.charging;

public class HandsetOnlineAction extends ChargingAction{
	private String hmac;
	//dhcp name
	private String hname;
	private String hip;
	private String vapname;
	
//	private String bssid;
//	private String rssi;
//	private String snr;
	private String authorized;
	private String ethernet;
	
	//设备wan口地址，
	//设备internet地址， 
	//终端认证类型 微信、短信、必虎安全
	//终端认证账号 手机号
	public String getHmac() {
		return hmac;
	}

	public void setHmac(String hmac) {
		this.hmac = hmac;
	}

	public String getHname() {
		return hname;
	}

	public void setHname(String hname) {
		this.hname = hname;
	}

	public String getHip() {
		return hip;
	}

	public void setHip(String hip) {
		this.hip = hip;
	}

	@Override
	public String getAct() {
		return ActionBuilder.ActionMode.HandsetOnline.getPrefix();
	}

	public String getVapname() {
		return vapname;
	}

	public void setVapname(String vapname) {
		this.vapname = vapname;
	}
	
	/*
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
*/
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
