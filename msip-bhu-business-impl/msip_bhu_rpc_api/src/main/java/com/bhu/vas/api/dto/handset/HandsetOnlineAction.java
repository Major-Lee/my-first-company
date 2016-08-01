package com.bhu.vas.api.dto.handset;

import com.bhu.vas.api.dto.charging.ActionBuilder;

public class HandsetOnlineAction extends TerminalAction{
	private String hmac;
	private String hname;
	private String hip;
	private String vapname;
	private String bssid;
	private String authorized;
	//新增终端下线
	private String rssi;
	private String wan;
	private String internet;
	private String viptype;
	private String vipacc;
	
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

	public String getAuthorized() {
		return authorized;
	}

	public void setAuthorized(String authorized) {
		this.authorized = authorized;
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
}
