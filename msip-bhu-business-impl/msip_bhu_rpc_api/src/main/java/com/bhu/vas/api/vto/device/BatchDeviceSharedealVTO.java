package com.bhu.vas.api.vto.device;

/**
 * 设备状态信息
 * @author Edmond
 *
 */
@SuppressWarnings("serial")
public class BatchDeviceSharedealVTO implements java.io.Serializable{
	private String mac;
	private String batchno;
	private int owner;
	private int distributor;
	private int distributor_l2;
	private double owner_percent;
	private double manufacturer_percent;
	private double distributor_percent;
	private double distributor_l2_percent;
	private boolean canbeturnoff;
	
	
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
	public int getOwner() {
		return owner;
	}
	public void setOwner(int owner) {
		this.owner = owner;
	}
	public int getDistributor() {
		return distributor;
	}
	public void setDistributor(int distributor) {
		this.distributor = distributor;
	}
	public int getDistributor_l2() {
		return distributor_l2;
	}
	public void setDistributor_l2(int distributor_l2) {
		this.distributor_l2 = distributor_l2;
	}
	public double getOwner_percent() {
		return owner_percent;
	}
	public void setOwner_percent(double owner_percent) {
		this.owner_percent = owner_percent;
	}
	public double getManufacturer_percent() {
		return manufacturer_percent;
	}
	public void setManufacturer_percent(double manufacturer_percent) {
		this.manufacturer_percent = manufacturer_percent;
	}
	public double getDistributor_l2_percent() {
		return distributor_l2_percent;
	}
	public void setDistributor_l2_percent(double distributor_l2_percent) {
		this.distributor_l2_percent = distributor_l2_percent;
	}
	public boolean isCanbeturnoff() {
		return canbeturnoff;
	}
	public void setCanbeturnoff(boolean canbeturnoff) {
		this.canbeturnoff = canbeturnoff;
	}
	public double getDistributor_percent() {
		return distributor_percent;
	}
	public void setDistributor_percent(double distributor_percent) {
		this.distributor_percent = distributor_percent;
	}
}
