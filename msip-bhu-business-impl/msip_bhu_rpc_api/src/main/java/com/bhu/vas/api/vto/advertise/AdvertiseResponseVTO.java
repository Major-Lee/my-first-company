package com.bhu.vas.api.vto.advertise;

import java.util.List;

@SuppressWarnings("serial")
public class AdvertiseResponseVTO implements java.io.Serializable{
	private List<AdvertiseVTO> vtos;
	private String adcode;
	private String province;
	private String city;
	private String district;
	
	public List<AdvertiseVTO> getVtos() {
		return vtos;
	}
	public void setVtos(List<AdvertiseVTO> vtos) {
		this.vtos = vtos;
	}
	public String getAdcode() {
		return adcode;
	}
	public void setAdcode(String adcode) {
		this.adcode = adcode;
	}
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
