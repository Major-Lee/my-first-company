package com.bhu.vas.pa.dto;

import com.bhu.vas.api.dto.HandsetDeviceDTO;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

public class PaHandsetOnlineAction {
	private String mac;
	private String hmac;
	
	@JsonInclude(Include.NON_NULL)
	private String hname;
	
	@JsonInclude(Include.NON_NULL)
	private String hip;

	@JsonInclude(Include.NON_NULL)
	private String ssid;

	@JsonInclude(Include.NON_NULL)
	private String bssid;
	
	@JsonInclude(Include.NON_NULL)
	private String rssi;
	
	@JsonInclude(Include.NON_NULL)
	private String wan;
	
	@JsonInclude(Include.NON_NULL)
	private String internet;
	
	@JsonInclude(Include.NON_NULL)
	private String vipacc;
	
	@JsonInclude(Include.NON_NULL)
	private Long ts;
	
	@JsonInclude(Include.NON_NULL)
	private Long end_ts;
	
	@JsonInclude(Include.NON_NULL)
	private String act;


	public static PaHandsetOnlineAction builderHandsetOnlineAction(String mac, HandsetDeviceDTO dto){
		PaHandsetOnlineAction action = new PaHandsetOnlineAction();
		action.setMac(mac);
		action.setHmac(dto.getMac());
		action.setHname(dto.getDhcp_name());
		action.setHip(dto.getIp());
		action.setSsid(dto.getSsid());
		action.setBssid(dto.getBssid());
		action.setRssi(dto.getRssi());
		return action;
	}

	

	public String getAct() {
		return act;
	}



	public void setAct(String act) {
		this.act = act;
	}



	public String getVipacc() {
		return vipacc;
	}


	public void setVipacc(String mobile) {
		this.vipacc = mobile;
	}


	public String getMac() {
		return mac;
	}


	public void setMac(String mac) {
		this.mac = mac;
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


	public void setInternet(String iip) {
		this.internet = iip;
	}


	public Long getTs() {
		return ts;
	}


	public void setTs(Long auth_time) {
		this.ts = auth_time;
	}


	public Long getEnd_ts() {
		return end_ts;
	}


	public void setEnd_ts(Long end_ts) {
		this.end_ts = end_ts;
	}
	
	
}
