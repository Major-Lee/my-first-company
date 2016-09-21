package com.bhu.vas.pa.dto;

import com.bhu.vas.api.dto.charging.ActionBuilder;
import com.bhu.vas.api.dto.charging.ChargingAction;

public class PaHandsetOnlineAction extends ChargingAction {
	private String hmac;
	// dhcp name
	private String hname;
	private String hip;
	private String vapname;
	private String bssid;

	private String rssi;
	private String snr;
	private String authorized;
	private String ethernet;

	private String wan;
	private String internet;
	private String viptype;
	private String vipacc;
	private String act;

	private String ssid;

	public static PaHandsetOnlineAction builderHandsetOnlineAction(String hmac,
			String mac, String hname, String hip, String vapname, String bssid,
			String rssi, String snr, String authorized, String ethernet,
			long ts, String ssid) {
		PaHandsetOnlineAction action = new PaHandsetOnlineAction();
		action.setHmac(hmac);
		action.setMac(mac);
		action.setHname(hname);
		action.setHip(hip);
		action.setVapname(vapname);
		action.setBssid(bssid);
		action.setRssi(rssi);
		action.setSnr(snr);
		action.setAuthorized(authorized);
		action.setEthernet(ethernet);
		action.setTs(ts);
		action.setSnr(snr);
		return action;
	}

	@Override
	public String getAct() {
		return ActionBuilder.ActionMode.HandsetOnline.getPrefix();
	}

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

	public String getSsid() {
		return ssid;
	}

	public void setSsid(String ssid) {
		this.ssid = ssid;
	}

	public String getWan() {
		return wan;
	}

	public void setWan(String wan) {
		this.wan = wan;
	}

	public String getInternet() {
		return internet;
	}

	public void setInternet(String internet) {
		this.internet = internet;
	}

	public String getViptype() {
		return viptype;
	}

	public void setViptype(String viptype) {
		this.viptype = viptype;
	}

	public String getVipacc() {
		return vipacc;
	}

	public void setVipacc(String vipacc) {
		this.vipacc = vipacc;
	}

	public void setAct(String act) {
		this.act = act;
	}

}
