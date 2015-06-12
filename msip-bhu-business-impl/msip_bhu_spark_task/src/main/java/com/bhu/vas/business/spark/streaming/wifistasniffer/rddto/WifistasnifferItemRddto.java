package com.bhu.vas.business.spark.streaming.wifistasniffer.rddto;

import java.io.Serializable;

@SuppressWarnings("serial")
public class WifistasnifferItemRddto implements Serializable{
	public static final int State_Online = 0;
	public static final int State_Offline = 1;
	
	//终端探测状态
	private int state;
	//终端mac
	private String mac;
	//探测发现时间
	private long snifftime;
	//停留时间 单位秒
	private int duration;
	//终端制造厂商 HUAWEI
	private String manufacture;
	//型号名称 HUAWEI C8815
	private String model_name;
	//型号编号 HUAWEI C8815
	private String model_number;
	//终端设备的名称 C8815
	private String device_name;
	
	public int getState() {
		return state;
	}
	public void setState(int state) {
		this.state = state;
	}
	public String getMac() {
		return mac;
	}
	public void setMac(String mac) {
		this.mac = mac;
	}
	public long getSnifftime() {
		return snifftime;
	}
	public void setSnifftime(long snifftime) {
		this.snifftime = snifftime;
	}
	public int getDuration() {
		return duration;
	}
	public void setDuration(int duration) {
		this.duration = duration;
	}
	public String getManufacture() {
		return manufacture;
	}
	public void setManufacture(String manufacture) {
		this.manufacture = manufacture;
	}
	public String getModel_name() {
		return model_name;
	}
	public void setModel_name(String model_name) {
		this.model_name = model_name;
	}
	public String getModel_number() {
		return model_number;
	}
	public void setModel_number(String model_number) {
		this.model_number = model_number;
	}
	public String getDevice_name() {
		return device_name;
	}
	public void setDevice_name(String device_name) {
		this.device_name = device_name;
	}
	public boolean isOnline(){
		if(State_Online == this.getState()) return true;
		return false;
	}
}
