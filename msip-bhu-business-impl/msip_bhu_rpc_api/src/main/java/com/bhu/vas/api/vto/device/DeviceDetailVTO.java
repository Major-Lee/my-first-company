package com.bhu.vas.api.vto.device;

@SuppressWarnings("serial")
public class DeviceDetailVTO implements java.io.Serializable{
	private DeviceBaseVTO dbv;
	private DevicePresentVTO dpv;
	private DeviceOperationVTO dov;
	public DeviceBaseVTO getDbv() {
		return dbv;
	}
	public void setDbv(DeviceBaseVTO dbv) {
		this.dbv = dbv;
	}
	public DevicePresentVTO getDpv() {
		return dpv;
	}
	public void setDpv(DevicePresentVTO dpv) {
		this.dpv = dpv;
	}
	public DeviceOperationVTO getDov() {
		return dov;
	}
	public void setDov(DeviceOperationVTO dov) {
		this.dov = dov;
	}
	
}
