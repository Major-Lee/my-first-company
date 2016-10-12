package com.bhu.vas.api.rpc.charging.vto;


@SuppressWarnings("serial")
public class SharedealDefaultVTO implements java.io.Serializable{
	private String id;
	private double owner_percent;
	private double manufacturer_percent;
	private double distributor_percent;
//	private String rcp;
//	private String rcm;
//	private String ait;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
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
	public double getDistributor_percent() {
		return distributor_percent;
	}
	public void setDistributor_percent(double distributor_percent) {
		this.distributor_percent = distributor_percent;
	}
//	public String getRcp() {
//		return rcp;
//	}
//	public void setRcp(String rcp) {
//		this.rcp = rcp;
//	}
//	public String getRcm() {
//		return rcm;
//	}
//	public void setRcm(String rcm) {
//		this.rcm = rcm;
//	}
//	public String getAit() {
//		return ait;
//	}
//	public void setAit(String ait) {
//		this.ait = ait;
//	}
}
