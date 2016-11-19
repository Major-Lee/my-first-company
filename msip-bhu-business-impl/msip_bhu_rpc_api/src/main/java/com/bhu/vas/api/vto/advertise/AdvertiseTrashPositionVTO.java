package com.bhu.vas.api.vto.advertise;

@SuppressWarnings("serial")
public class AdvertiseTrashPositionVTO implements java.io.Serializable{
	
	private String province;
	private String city;
	private String district;
	
	public String getProvince() {
		return province;
	}
	public void setProvince(String province) {
		this.province = province;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getDistrict() {
		return district;
	}
	public void setDistrict(String district) {
		this.district = district;
	}
}
