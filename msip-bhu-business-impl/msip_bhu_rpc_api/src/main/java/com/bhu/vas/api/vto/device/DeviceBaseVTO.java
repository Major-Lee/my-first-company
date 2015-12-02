package com.bhu.vas.api.vto.device;

/**
 * 设备基础信息
 * @author Edmond
 *
 */
@SuppressWarnings("serial")
public class DeviceBaseVTO implements java.io.Serializable{
	private String mac;
	private String sn;
	//固件版本号
	private String orig_swver;
	//增值组件版本号
	private String vap_module;
	//固件 硬件版本
	private String orig_hdver;
	//设备 硬件版本
	private String hdtype;
	//归属业务线
	private String dut;
	//设备名称
	private String orig_model;
	//工作模式
	private String work_mode;
	public String getMac() {
		return mac;
	}
	public void setMac(String mac) {
		this.mac = mac;
	}
	public String getSn() {
		return sn;
	}
	public void setSn(String sn) {
		this.sn = sn;
	}
	public String getOrig_swver() {
		return orig_swver;
	}
	public void setOrig_swver(String orig_swver) {
		this.orig_swver = orig_swver;
	}
	public String getVap_module() {
		return vap_module;
	}
	public void setVap_module(String vap_module) {
		this.vap_module = vap_module;
	}
	public String getOrig_hdver() {
		return orig_hdver;
	}
	public void setOrig_hdver(String orig_hdver) {
		this.orig_hdver = orig_hdver;
	}
	public String getHdtype() {
		return hdtype;
	}
	public void setHdtype(String hdtype) {
		this.hdtype = hdtype;
	}
	public String getDut() {
		return dut;
	}
	public void setDut(String dut) {
		this.dut = dut;
	}
	public String getOrig_model() {
		return orig_model;
	}
	public void setOrig_model(String orig_model) {
		this.orig_model = orig_model;
	}
	public String getWork_mode() {
		return work_mode;
	}
	public void setWork_mode(String work_mode) {
		this.work_mode = work_mode;
	}
}
