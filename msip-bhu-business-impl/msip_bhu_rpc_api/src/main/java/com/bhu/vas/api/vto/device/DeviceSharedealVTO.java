package com.bhu.vas.api.vto.device;

/**
 * 设备状态信息
 * @author Edmond
 *
 */
@SuppressWarnings("serial")
public class DeviceSharedealVTO implements java.io.Serializable{
	//private int owner;
	private String mac;
	private String batchno;
	private double owner_percent;
	private double manufacturer_percent;
	private double distributor_percent;
	private boolean canbeturnoff;
	private boolean runtime_applydefault;
	private boolean customized;
	private String rcp;
	private String rcm;
	private String aitm;
	private String aitp;
	/*public int getOwner() {
		return owner;
	}
	public void setOwner(int owner) {
		this.owner = owner;
	}*/
	public String getMac() {
		return mac;
	}
	public void setMac(String mac) {
		this.mac = mac;
	}
	public String getBatchno() {
		return batchno;
	}
	public void setBatchno(String batchno) {
		this.batchno = batchno;
	}
	public double getOwner_percent() {
		return owner_percent;
	}
	public void setOwner_percent(double owner_percent) {
		this.owner_percent = owner_percent;
	}
	public boolean isCanbeturnoff() {
		return canbeturnoff;
	}
	public void setCanbeturnoff(boolean canbeturnoff) {
		this.canbeturnoff = canbeturnoff;
	}
	
	public boolean isRuntime_applydefault() {
		return runtime_applydefault;
	}
	public void setRuntime_applydefault(boolean runtime_applydefault) {
		this.runtime_applydefault = runtime_applydefault;
	}
	public String getRcp() {
		return rcp;
	}
	public void setRcp(String rcp) {
		this.rcp = rcp;
	}
	public String getRcm() {
		return rcm;
	}
	public void setRcm(String rcm) {
		this.rcm = rcm;
	}
	public double getManufacturer_percent() {
		return manufacturer_percent;
	}
	public void setManufacturer_percent(double manufacturer_percent) {
		this.manufacturer_percent = manufacturer_percent;
	}
	public String getAitm() {
		return aitm;
	}
	public void setAitm(String aitm) {
		this.aitm = aitm;
	}
	public String getAitp() {
		return aitp;
	}
	public void setAitp(String aitp) {
		this.aitp = aitp;
	}
	public boolean isCustomized() {
		return customized;
	}
	public void setCustomized(boolean customized) {
		this.customized = customized;
	}
	public double getDistributor_percent() {
		return distributor_percent;
	}
	public void setDistributor_percent(double distributor_percent) {
		this.distributor_percent = distributor_percent;
	}
	
}
