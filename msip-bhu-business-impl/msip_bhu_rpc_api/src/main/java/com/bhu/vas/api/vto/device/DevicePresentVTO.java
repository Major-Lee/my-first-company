package com.bhu.vas.api.vto.device;

/**
 * 设备状态信息
 * @author Edmond
 *
 */
@SuppressWarnings("serial")
public class DevicePresentVTO implements java.io.Serializable{
	private int uid;
	private String mac;
	private boolean online;
	private boolean monline;
	private String mobileno;
	//绑定的手持设备类型 iOS、Adr
	private String handsettype;
	private String handsetn;
	//地理位置
	private String address;
	//首次上线时间
	private String first_reg_at;
	//在线时长
	private String dod;
	public int getUid() {
		return uid;
	}
	public void setUid(int uid) {
		this.uid = uid;
	}
	public String getMac() {
		return mac;
	}
	public void setMac(String mac) {
		this.mac = mac;
	}
	public boolean isOnline() {
		return online;
	}
	public void setOnline(boolean online) {
		this.online = online;
	}
	public boolean isMonline() {
		return monline;
	}
	public void setMonline(boolean monline) {
		this.monline = monline;
	}
	public String getMobileno() {
		return mobileno;
	}
	public void setMobileno(String mobileno) {
		this.mobileno = mobileno;
	}
	public String getHandsettype() {
		return handsettype;
	}
	public void setHandsettype(String handsettype) {
		this.handsettype = handsettype;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getFirst_reg_at() {
		return first_reg_at;
	}
	public void setFirst_reg_at(String first_reg_at) {
		this.first_reg_at = first_reg_at;
	}
	public String getDod() {
		return dod;
	}
	public void setDod(String dod) {
		this.dod = dod;
	}
	public String getHandsetn() {
		return handsetn;
	}
	public void setHandsetn(String handsetn) {
		this.handsetn = handsetn;
	}
	
	
}
