package com.bhu.vas.api.rpc.devices.model;

import java.io.Serializable;

@SuppressWarnings("serial")
public class WifiHandsetDeviceMarkPK implements Serializable{
	private String mac;//设备mac
	private String hd_mac;//终端mac
	
	public WifiHandsetDeviceMarkPK(){
	}
	public WifiHandsetDeviceMarkPK(String mac, String hd_mac){
		this.mac = mac;
		this.hd_mac = hd_mac;
	}
	public String getMac() {
		return mac;
	}
	public void setMac(String mac) {
		this.mac = mac;
	}
	public String getHd_mac() {
		return hd_mac;
	}
	public void setHd_mac(String hd_mac) {
		this.hd_mac = hd_mac;
	}
	@Override
	public String toString() {
		return mac+"-"+hd_mac;
	}
	@Override
	public boolean equals(Object o) {
		if(o == null) return false;
		if(o instanceof WifiHandsetDeviceMarkPK){
			WifiHandsetDeviceMarkPK oo = (WifiHandsetDeviceMarkPK)o;
			return (this.mac.equals(oo.mac) && this.hd_mac.equals(oo.hd_mac));
		}
		return false;
	}
	@Override
	public int hashCode() {
		return this.toString().hashCode();
	}
}
