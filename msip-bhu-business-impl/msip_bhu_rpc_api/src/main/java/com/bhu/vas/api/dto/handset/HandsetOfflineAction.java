package com.bhu.vas.api.dto.handset;

import com.bhu.vas.api.dto.charging.ActionBuilder;

public class HandsetOfflineAction extends TerminalAction{
	private String hmac;
	private String huptime;
	private String vapname;
	private String bssid;
	private long tx_bytes;
	private long rx_bytes;
	private String authorized;
	private String act;
	//新增终端下线
	private long end_ts;
	private String hname;
	private String hip;
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
	public long getEnd_ts() {
		return end_ts;
	}
	public void setEnd_ts(long end_ts) {
		this.end_ts = end_ts;
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
