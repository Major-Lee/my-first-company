package com.bhu.vas.api.dto.wifistasniffer;

import java.io.Serializable;

@SuppressWarnings("serial")
public class WifistasnifferItemRddto implements Serializable{
	public static final int State_Online = 0;
	public static final int State_Offline = 1;
	//周边探测的终端离线的timeout时间 针对终端探测下线消息没有上报的情况
	public static final long State_Offline_TimeoutMs = 24 * 3600 * 1000l;
	
	//终端探测状态
	private int state;
	//终端mac
	private String mac;
	//设备mac
	private String d_mac;
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
	public String getD_mac() {
		return d_mac;
	}
	public void setD_mac(String d_mac) {
		this.d_mac = d_mac;
	}
	public boolean isOnline(){
		if(State_Online == this.getState()) return true;
		return false;
	}
	

	@Override
	public int hashCode() {
		StringBuffer sb = new StringBuffer();
		sb.append(this.d_mac).append(this.mac).append(this.snifftime);
		return sb.toString().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if(obj == null) return false;
		WifistasnifferItemRddto dto = (WifistasnifferItemRddto)obj;
		if(dto.getMac().equals(this.getMac()) && dto.getSnifftime() == this.getSnifftime()){
			return true;
		}
		return false;
	}
	
/*	public static void main(String[] args){
		WifistasnifferItemRddto dto1 = new WifistasnifferItemRddto();
		dto1.setD_mac("11");
		dto1.setMac("22");
		dto1.setSnifftime(1111);
		WifistasnifferItemRddto dto2 = new WifistasnifferItemRddto();
		dto2.setD_mac("11");
		dto2.setMac("22");
		dto2.setSnifftime(1111);
		dto2.setDuration(100);
		
		Set<WifistasnifferItemRddto> wifistasnifferOnlines1 = new HashSet<WifistasnifferItemRddto>();
		wifistasnifferOnlines1.add(dto1);
		Set<WifistasnifferItemRddto> wifistasnifferOnlines2 = new HashSet<WifistasnifferItemRddto>();
		wifistasnifferOnlines2.add(dto2);
		
//		Set<WifistasnifferItemRddto> all = new HashSet<WifistasnifferItemRddto>();
//		all.addAll(wifistasnifferOnlines2);
//		all.addAll(wifistasnifferOnlines1);
		
		wifistasnifferOnlines1.removeAll(wifistasnifferOnlines2);
		
		for(WifistasnifferItemRddto dto : wifistasnifferOnlines1){
			System.out.println(dto.getDuration());
		}
	}*/
}
