package com.bhu.vas.api.vto;

import java.io.Serializable;

/**
 * {"status":0,"name":"BXO2000n(2S-Lite)","mac":"84:82:f4:ff:ff:fc","ip":"192.168.66.185","note":"","para":"","ncIp":""}
 * @author tangzichao
 *
 */
@SuppressWarnings("serial")
public class GeoMapDeviceVTO implements Serializable{
	private int status;
	private String name;//handset id
	private String mac;
	private String ip;
	private String note;
	private String para;
	private String ncIp;
	
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getMac() {
		return mac;
	}
	public void setMac(String mac) {
		this.mac = mac;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public String getNote() {
		return note;
	}
	public void setNote(String note) {
		this.note = note;
	}
	public String getPara() {
		return para;
	}
	public void setPara(String para) {
		this.para = para;
	}
	public String getNcIp() {
		return ncIp;
	}
	public void setNcIp(String ncIp) {
		this.ncIp = ncIp;
	}
	
}
